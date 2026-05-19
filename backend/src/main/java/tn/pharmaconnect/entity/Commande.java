package tn.pharmaconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "commande")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code_commande", length = 50, unique = true)
    private String codeCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordonnance_id")
    private Ordonnance ordonnance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "date_commande")
    private LocalDate dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 30)
    private StatutCommande statut;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeCommande() {
        return codeCommande;
    }

    public void setCodeCommande(String codeCommande) {
        this.codeCommande = codeCommande;
    }

    public Ordonnance getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
}
