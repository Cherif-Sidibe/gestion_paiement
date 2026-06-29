package edu.ism.badwallet.external.facture;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.ObjectMapper;

import edu.ism.badwallet.client.web.external.dto.FactureDto;
import edu.ism.badwallet.client.web.external.dto.PayByReferencesPayload;
import edu.ism.badwallet.client.web.external.dto.PayCurrentPayload;
import edu.ism.badwallet.client.web.external.dto.PaymentEnvelope;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.shared.exceptions.PaymentRejectedException;
import edu.ism.badwallet.shared.exceptions.PaymentServiceUnavailableException;

// Proxy : meme contrat local qu'ExternalFactureService, execution distante via HTTP
// vers payment-service. badwallet n'accede jamais a la base de payment-service.
@Service
public class FactureServiceProxy implements ExternalFactureService {

    private static final ParameterizedTypeReference<PaymentEnvelope<List<FactureDto>>> FACTURE_LIST =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<PaymentEnvelope<FactureDto>> FACTURE_SINGLE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient paymentRestClient;
    private final ObjectMapper objectMapper;

    public FactureServiceProxy(RestClient paymentRestClient, ObjectMapper objectMapper) {
        this.paymentRestClient = paymentRestClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<FactureDto> getCurrentUnpaid(String walletCode, String unite) {
        return relay(paymentRestClient.get()
                .uri(uri -> uri.path("/api/factures/{walletCode}/current")
                        .queryParamIfPresent("unite", Optional.ofNullable(unite).filter(u -> !u.isBlank()))
                        .build(walletCode)));
    }

    @Override
    public List<FactureDto> getUnpaidInPeriod(String walletCode, LocalDate debut, LocalDate fin) {
        return relay(paymentRestClient.get()
                .uri(uri -> uri.path("/api/factures/{walletCode}/periode")
                        .queryParam("debut", debut)
                        .queryParam("fin", fin)
                        .build(walletCode)));
    }

    @Override
    public FactureDto payCurrent(String walletCode, String service, BigDecimal amount) {
        PaymentEnvelope<FactureDto> envelope = post(
                "/api/factures/pay-current",
                new PayCurrentPayload(walletCode, service, amount),
                FACTURE_SINGLE);
        return envelope == null ? null : envelope.body();
    }

    @Override
    public List<FactureDto> payByReferences(String walletCode, String service, List<String> references) {
        PaymentEnvelope<List<FactureDto>> envelope = post(
                "/api/factures/pay-by-references",
                new PayByReferencesPayload(walletCode, service, references),
                FACTURE_LIST);
        return envelope == null || envelope.body() == null ? List.of() : envelope.body();
    }

    private List<FactureDto> relay(RestClient.RequestHeadersSpec<?> request) {
        try {
            PaymentEnvelope<List<FactureDto>> envelope = request
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                    .body(FACTURE_LIST);
            return envelope == null || envelope.body() == null ? List.of() : envelope.body();
        } catch (ResourceAccessException ex) {
            throw new PaymentServiceUnavailableException("Service de factures indisponible");
        }
    }

    // POST de paiement : tout refus distant (4xx) devient une PaymentRejectedException
    // qui remonte dans la @Transactional appelante et provoque le rollback du debit.
    private <T> T post(String path, Object payload, ParameterizedTypeReference<T> bodyType) {
        try {
            return paymentRestClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handlePayRejection)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                    .body(bodyType);
        } catch (ResourceAccessException ex) {
            throw new PaymentServiceUnavailableException("Service de factures indisponible");
        }
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
            throw new EntityNotFoundException(
                    "Facture introuvable pour cette wallet aupres du service de factures");
        }
        throw new PaymentServiceUnavailableException("Le service de factures a rejete la requete");
    }

    private void handleServerError(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new PaymentServiceUnavailableException("Service de factures indisponible");
    }

    // Traduit le refus de payment-service (statut + message) en erreur metier badwallet.
    private void handlePayRejection(HttpRequest request, ClientHttpResponse response) throws IOException {
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
        String message = extractMessage(response);
        throw new PaymentRejectedException(status,
                message != null ? message : "Le service de factures a rejete le paiement");
    }

    private String extractMessage(ClientHttpResponse response) {
        try {
            Map<?, ?> body = objectMapper.readValue(response.getBody(), Map.class);
            Object message = body.get("message");
            return message != null ? message.toString() : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
