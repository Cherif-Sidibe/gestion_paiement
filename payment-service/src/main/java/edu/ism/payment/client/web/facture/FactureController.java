package edu.ism.payment.client.web.facture;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ism.payment.client.web.facture.dto.FactureResponseDto;
import edu.ism.payment.client.web.facture.dto.PayByReferencesRequestDto;
import edu.ism.payment.client.web.facture.dto.PayCurrentRequestDto;
import edu.ism.payment.facture.Facture;
import edu.ism.payment.facture.FactureService;
import edu.ism.payment.facture.ServiceFacture;
import edu.ism.payment.shared.response.RestResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService factureService;
    private final FactureMapper factureMapper;

    public FactureController(FactureService factureService, FactureMapper factureMapper) {
        this.factureService = factureService;
        this.factureMapper = factureMapper;
    }

    // Factures impayees du mois courant (unite = ISM|WOYAFAL optionnel).
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<RestResponse<List<FactureResponseDto>>> getCurrentUnpaid(
            @PathVariable String walletCode,
            @RequestParam(required = false) ServiceFacture unite) {
        List<Facture> factures = factureService.findCurrentUnpaid(walletCode, unite);
        List<FactureResponseDto> dtos = factures.stream().map(factureMapper::toDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dtos, HttpStatus.OK));
    }

    // Factures impayees dont le mois est dans [debut, fin].
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<RestResponse<List<FactureResponseDto>>> getUnpaidInPeriod(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<Facture> factures = factureService.findUnpaidInPeriod(walletCode, debut, fin);
        List<FactureResponseDto> dtos = factures.stream().map(factureMapper::toDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dtos, HttpStatus.OK));
    }

    // Paie la facture impayee du mois courant pour ce service.
    @PostMapping("/pay-current")
    public ResponseEntity<RestResponse<FactureResponseDto>> payCurrent(
            @Valid @RequestBody PayCurrentRequestDto request) {
        Facture facture = factureService.payCurrent(request.walletCode(), request.service(), request.amount());
        FactureResponseDto dto = factureMapper.toDto(facture);
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dto, HttpStatus.OK));
    }

    // Paie les factures listees par reference (toutes ou aucune).
    @PostMapping("/pay-by-references")
    public ResponseEntity<RestResponse<List<FactureResponseDto>>> payByReferences(
            @Valid @RequestBody PayByReferencesRequestDto request) {
        List<Facture> factures = factureService.payByReferences(
                request.walletCode(), request.service(), request.references());
        List<FactureResponseDto> dtos = factures.stream().map(factureMapper::toDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(RestResponse.success(dtos, HttpStatus.OK));
    }
}
