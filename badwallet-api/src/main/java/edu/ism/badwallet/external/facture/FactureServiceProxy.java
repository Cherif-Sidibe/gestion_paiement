package edu.ism.badwallet.external.facture;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import edu.ism.badwallet.client.web.external.dto.FactureDto;
import edu.ism.badwallet.client.web.external.dto.PaymentEnvelope;
import edu.ism.badwallet.shared.exceptions.EntityNotFoundException;
import edu.ism.badwallet.shared.exceptions.PaymentServiceUnavailableException;

// Proxy : meme contrat local qu'ExternalFactureService, execution distante via HTTP
// vers payment-service. badwallet n'accede jamais a la base de payment-service.
@Service
public class FactureServiceProxy implements ExternalFactureService {

    private static final ParameterizedTypeReference<PaymentEnvelope<List<FactureDto>>> FACTURE_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient paymentRestClient;

    public FactureServiceProxy(RestClient paymentRestClient) {
        this.paymentRestClient = paymentRestClient;
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
}
