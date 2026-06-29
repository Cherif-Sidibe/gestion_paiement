package edu.ism.badwallet.transaction.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.ism.badwallet.client.web.external.dto.FactureDto;
import edu.ism.badwallet.client.web.payment.dto.PayFacturesRequestDto;
import edu.ism.badwallet.client.web.payment.dto.PayFacturesResponseDto;
import edu.ism.badwallet.client.web.payment.dto.PayRequestDto;
import edu.ism.badwallet.client.web.payment.dto.PayResponseDto;
import edu.ism.badwallet.external.facture.ExternalFactureService;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.shared.exceptions.PaymentRejectedException;
import edu.ism.badwallet.transaction.observer.TransactionPublisher;
import edu.ism.badwallet.wallet.Wallet;
import edu.ism.badwallet.wallet.WalletRepository;

/**
 * Paiement de factures depuis une wallet : debite la wallet PUIS marque les
 * factures payees cote payment-service. Orchestration distribuee tout-ou-rien.
 *
 * Ordre (dans la @Transactional) : 1) charger la wallet, 2) determiner le total,
 * 3) DEBITER (Template Method, verifie le solde + historise PAIEMENT),
 * 4) APPELER payment-service. Si l'appel distant echoue -> exception -> rollback
 * du debit (rien n'a ete commite) : soit tout passe, soit rien.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    // Periode large : sert a recuperer toutes les factures impayees de la wallet
    // (payment-service n'expose pas de "toutes impayees" sans bornes de mois).
    private static final LocalDate PERIODE_DEBUT = LocalDate.of(2000, 1, 1);
    private static final LocalDate PERIODE_FIN = LocalDate.of(2999, 12, 31);

    private final WalletRepository walletRepository;
    private final TransactionPublisher publisher;
    private final ExternalFactureService externalFactureService;

    public PaymentServiceImpl(WalletRepository walletRepository,
                              TransactionPublisher publisher,
                              ExternalFactureService externalFactureService) {
        this.walletRepository = walletRepository;
        this.publisher = publisher;
        this.externalFactureService = externalFactureService;
    }

    @Override
    @Transactional
    public PayResponseDto pay(PayRequestDto request) {
        Wallet wallet = loadWallet(request.phoneNumber());
        Facturier facturier = Facturier.from(request.serviceName());

        // 1) DEBIT d'abord (Template Method) : solde verifie, transaction PAIEMENT historisee.
        new PaymentOperation(wallet, request.amount(), walletRepository, publisher).execute();

        // 2) Puis marquage distant. Echec -> exception -> rollback du debit.
        FactureDto payee = externalFactureService.payCurrent(
                wallet.getCode(), facturier.name(), request.amount());

        return PayResponseDto.builder()
                .walletCode(wallet.getCode())
                .phoneNumber(wallet.getPhoneNumber())
                .montantDebite(request.amount())
                .newBalance(wallet.getBalance())
                .facturePayee(payee)
                .build();
    }

    @Override
    @Transactional
    public PayFacturesResponseDto payFactures(PayFacturesRequestDto request) {
        Wallet wallet = loadWallet(request.phoneNumber());
        Facturier facturier = Facturier.from(request.serviceName());
        List<String> references = new ArrayList<>(new LinkedHashSet<>(request.factureReferences()));

        // 1) Total a debiter : factures impayees de la wallet indexees par reference,
        //    filtrees sur le service cible. Reference absente/deja payee -> erreur AVANT debit.
        Map<String, FactureDto> impayeesParReference = externalFactureService
                .getUnpaidInPeriod(wallet.getCode(), PERIODE_DEBUT, PERIODE_FIN).stream()
                .filter(f -> facturier.name().equals(f.service()))
                .collect(Collectors.toMap(FactureDto::reference, Function.identity(), (a, b) -> a));

        List<FactureDto> ciblees = new ArrayList<>();
        List<String> invalides = new ArrayList<>();
        for (String reference : references) {
            FactureDto facture = impayeesParReference.get(reference);
            if (facture == null) {
                invalides.add(reference);
            } else {
                ciblees.add(facture);
            }
        }
        if (!invalides.isEmpty()) {
            throw new PaymentRejectedException(HttpStatus.CONFLICT,
                    "Facture(s) introuvable(s) ou deja payee(s) pour le service " + facturier.name()
                            + " : " + String.join(", ", invalides));
        }

        BigDecimal total = ciblees.stream()
                .map(FactureDto::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2) DEBIT d'abord (Template Method).
        new PaymentOperation(wallet, total, walletRepository, publisher).execute();

        // 3) Puis marquage distant tout-ou-rien. Echec -> exception -> rollback du debit.
        List<FactureDto> payees = externalFactureService.payByReferences(
                wallet.getCode(), facturier.name(), references);

        return PayFacturesResponseDto.builder()
                .walletCode(wallet.getCode())
                .phoneNumber(wallet.getPhoneNumber())
                .montantDebite(total)
                .newBalance(wallet.getBalance())
                .facturesPayees(payees)
                .build();
    }

    private Wallet loadWallet(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable pour le numero " + phoneNumber));
    }
}
