package tn.pharmaconnect.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrdonnanceRequest {
    @NotNull
    private Integer patientId;
    @NotEmpty
    private List<LigneOrdonnanceRequest> lignes;
    private String signatureNumerique;
    private String notes;

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public List<LigneOrdonnanceRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneOrdonnanceRequest> lignes) {
        this.lignes = lignes;
    }

    public String getSignatureNumerique() {
        return signatureNumerique;
    }

    public void setSignatureNumerique(String signatureNumerique) {
        this.signatureNumerique = signatureNumerique;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
