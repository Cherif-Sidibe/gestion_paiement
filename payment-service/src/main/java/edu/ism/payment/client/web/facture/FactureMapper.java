package edu.ism.payment.client.web.facture;

import org.springframework.stereotype.Component;

import edu.ism.payment.client.web.facture.dto.FactureResponseDto;
import edu.ism.payment.facture.Facture;
import edu.ism.payment.shared.mapper.DateMapper;

@Component
public class FactureMapper {

    private final DateMapper dateMapper;

    public FactureMapper(DateMapper dateMapper) {
        this.dateMapper = dateMapper;
    }

    public FactureResponseDto toDto(Facture facture) {
        if (facture == null) {
            return null;
        }

        return FactureResponseDto.builder()
                .id(facture.getId())
                .reference(facture.getReference())
                .walletCode(facture.getWalletCode())
                .service(facture.getService() != null ? facture.getService().name() : null)
                .montant(facture.getMontant())
                .mois(dateMapper.formatLocalDate(facture.getMois(), "yyyy-MM"))
                .payee(facture.isPayee())
                .datePaiement(dateMapper.formatLocalDate(facture.getDatePaiement(), "yyyy-MM-dd"))
                .build();
    }
}
