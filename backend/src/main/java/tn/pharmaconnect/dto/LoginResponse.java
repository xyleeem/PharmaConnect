package tn.pharmaconnect.dto;

import tn.pharmaconnect.entity.Role;

public class LoginResponse {
    private Integer id;
    private String nom;
    private String email;
    private Role role;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(Integer id, String nom, String email, Role role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
        this.message = "Connexion reussie";
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
