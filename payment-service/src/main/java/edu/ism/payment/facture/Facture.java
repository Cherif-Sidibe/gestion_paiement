package edu.ism.payment.facture;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "factures")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference metier : FAC-<SERVICE>-<numeroWallet>-<sequence>, ex: FAC-ISM-3-1.
    // Le mois n'y figure PAS : c'est le champ "mois" ci-dessous.
    @Column(unique = true, updatable = false)
    private String reference;

    // Convention de couplage avec badwallet : WLT-0000001 ... WLT-0000010.
    @Column(name = "wallet_code")
    private String walletCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "service")
    private ServiceFacture service;

    // XOF sans decimale : on fige l'echelle a 0 (NUMERIC(12,0)).
    @Column(name = "montant", precision = 12, scale = 0)
    private BigDecimal montant;

    // 1er jour du mois concerne par la facture. Champ separe de la reference.
    @Column(name = "mois")
    private LocalDate mois;

    @Builder.Default
    @Column(name = "payee")
    private boolean payee = false;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
