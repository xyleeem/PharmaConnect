package tn.pharmaconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "id")
public class Patient extends Utilisateur {

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "telephone", length = 20)
    private String telephone;

    public Patient() {
        setRole(Role.PATIENT);
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
