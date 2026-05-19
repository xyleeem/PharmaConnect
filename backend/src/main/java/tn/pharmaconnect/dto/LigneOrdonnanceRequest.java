package tn.pharmaconnect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LigneOrdonnanceRequest {
    @NotNull
    private Integer medicamentId;
    @Min(1)
    private Integer quantite = 1;
    private String posologie;
    private Integer duree = 7;

    public Integer getMedicamentId() {
        return medicamentId;
    }

    public void setMedicamentId(Integer medicamentId) {
        this.medicamentId = medicamentId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public String getPosologie() {
        return posologie;
    }

    public void setPosologie(String posologie) {
        this.posologie = posologie;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }
}
