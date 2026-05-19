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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordonnance")
public class Ordonnance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "date_emission")
    private LocalDate dateEmission;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutOrdonnance statut;

    @Column(name = "signature_numerique", columnDefinition = "TEXT")
    private String signatureNumerique;

    @OneToMany(mappedBy = "ordonnance")
    private List<LignePrescription> lignes = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public StatutOrdonnance getStatut() {
        return statut;
    }

    public void setStatut(StatutOrdonnance statut) {
        this.statut = statut;
    }

    public String getSignatureNumerique() {
        return signatureNumerique;
    }

    public void setSignatureNumerique(String signatureNumerique) {
        this.signatureNumerique = signatureNumerique;
    }

    public List<LignePrescription> getLignes() {
        return lignes;
    }

    public void setLignes(List<LignePrescription> lignes) {
        this.lignes = lignes;
    }
}
