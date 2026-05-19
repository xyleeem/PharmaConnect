package tn.pharmaconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "pharmacien")
@PrimaryKeyJoinColumn(name = "id")
public class Pharmacien extends Utilisateur {

    @Column(name = "diplome", length = 100)
    private String diplome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacie_id")
    private Pharmacie pharmacie;

    public Pharmacien() {
        setRole(Role.PHARMACIEN);
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public Pharmacie getPharmacie() {
        return pharmacie;
    }

    public void setPharmacie(Pharmacie pharmacie) {
        this.pharmacie = pharmacie;
    }
}
