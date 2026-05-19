package tn.pharmaconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateur")
@PrimaryKeyJoinColumn(name = "id")
public class Administrateur extends Utilisateur {

    @Column(name = "niveau_acces")
    private Integer niveauAcces;

    public Administrateur() {
        setRole(Role.ADMIN);
    }

    public Integer getNiveauAcces() {
        return niveauAcces;
    }

    public void setNiveauAcces(Integer niveauAcces) {
        this.niveauAcces = niveauAcces;
    }
}
