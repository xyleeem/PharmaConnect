package tn.pharmaconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrdonnanceResponse {
    private Integer id;
    private Integer patientId;
    private String patientName;
    private String medicineName;
    private String statut;
    private LocalDate dateEmission;
    private LocalDate dateExpiration;
    private BigDecimal total;
    private String items;
    private List<LigneOrdonnanceRequest> lignes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public List<LigneOrdonnanceRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneOrdonnanceRequest> lignes) {
        this.lignes = lignes;
    }
}
