package tn.pharmaconnect.dto;

import tn.pharmaconnect.entity.Administrateur;
import tn.pharmaconnect.entity.Patient;
import tn.pharmaconnect.entity.Pharmacien;
import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.entity.Utilisateur;

public class UserDTO {

    private Integer id;
    private String nom;
    private String email;
    private Role role;
    private String telephone;
    private String adresse;
    private String diplome;
    private Integer niveauAcces;

    public UserDTO() {
    }

    public static UserDTO from(Utilisateur user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        if (user instanceof Patient patient) {
            dto.setTelephone(patient.getTelephone());
            dto.setAdresse(patient.getAdresse());
        }
        if (user instanceof Pharmacien pharmacien) {
            dto.setDiplome(pharmacien.getDiplome());
        }
        if (user instanceof Administrateur administrateur) {
            dto.setNiveauAcces(administrateur.getNiveauAcces());
        }
        return dto;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public Integer getNiveauAcces() {
        return niveauAcces;
    }

    public void setNiveauAcces(Integer niveauAcces) {
        this.niveauAcces = niveauAcces;
    }
}
