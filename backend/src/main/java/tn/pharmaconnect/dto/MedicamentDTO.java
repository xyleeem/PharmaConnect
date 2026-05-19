package tn.pharmaconnect.dto;

import java.math.BigDecimal;

public class MedicamentDTO {
    private Integer id;
    private String nom;
    private String categorie;
    private BigDecimal prixUnitaire;
    private Integer quantiteDisponible;

    public MedicamentDTO() {
    }

    public MedicamentDTO(Integer id, String nom, String categorie, BigDecimal prixUnitaire, Integer quantiteDisponible) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prixUnitaire = prixUnitaire;
        this.quantiteDisponible = quantiteDisponible;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Integer getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public void setQuantiteDisponible(Integer quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }
}
