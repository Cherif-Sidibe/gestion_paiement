package edu.ism.badwallet.client.web.external;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ism.badwallet.client.web.external.dto.FactureDto;
import edu.ism.badwallet.external.facture.ExternalFactureService;
import edu.ism.badwallet.shared.response.RestResponse;

@RestController
@RequestMapping("/api/external/factures")
public class ExternalFactureController {

    private final ExternalFactureService externalFactureService;

    public ExternalFactureController(ExternalFactureService externalFactureService) {
        this.externalFactureService = externalFactureService;
    }

    // Relaie vers payment-service : factures impayees du mois courant (unite optionnelle).
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<RestResponse<List<FactureDto>>> getCurrentUnpaid(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        List<FactureDto> factures = externalFactureService.getCurrentUnpaid(walletCode, unite);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(factures, HttpStatus.OK));
    }

    // Relaie vers payment-service : factures impayees dont le mois est dans [debut, fin].
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<RestResponse<List<FactureDto>>> getUnpaidInPeriod(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<FactureDto> factures = externalFactureService.getUnpaidInPeriod(walletCode, debut, fin);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(factures, HttpStatus.OK));
    }
}
