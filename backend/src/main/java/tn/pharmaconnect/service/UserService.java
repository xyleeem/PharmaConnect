package tn.pharmaconnect.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pharmaconnect.dto.UserDTO;
import tn.pharmaconnect.dto.UserRequest;
import tn.pharmaconnect.entity.Administrateur;
import tn.pharmaconnect.entity.Patient;
import tn.pharmaconnect.entity.Pharmacien;
import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.entity.Utilisateur;
import tn.pharmaconnect.exception.ApiException;
import tn.pharmaconnect.repository.PatientRepository;
import tn.pharmaconnect.repository.PharmacienRepository;
import tn.pharmaconnect.repository.UtilisateurRepository;

@Service
public class UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final PharmacienRepository pharmacienRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UtilisateurRepository utilisateurRepository,
            PatientRepository patientRepository,
            PharmacienRepository pharmacienRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.pharmacienRepository = pharmacienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAllUsers() {
        return utilisateurRepository.findAll().stream().map(UserDTO::from).toList();
    }

    @Transactional
    public UserDTO createUser(UserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(email)) {
            throw new ApiException("Cet email est deja utilise", HttpStatus.CONFLICT);
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException("Mot de passe requis", HttpStatus.BAD_REQUEST);
        }
        Role role = request.getRole();
        switch (role) {
            case PATIENT -> {
                Patient patient = new Patient();
                fillBaseUser(patient, request, email);
                patient.setTelephone(request.getTelephone());
                patient.setAdresse(request.getAdresse());
                return UserDTO.from(patientRepository.save(patient));
            }
            case PHARMACIEN -> {
                Pharmacien pharmacien = new Pharmacien();
                fillBaseUser(pharmacien, request, email);
                pharmacien.setDiplome(request.getDiplome());
                return UserDTO.from(pharmacienRepository.save(pharmacien));
            }
            case ADMIN -> {
                Administrateur admin = new Administrateur();
                fillBaseUser(admin, request, email);
                admin.setNiveauAcces(request.getNiveauAcces() == null ? 1 : request.getNiveauAcces());
                return UserDTO.from(utilisateurRepository.save(admin));
            }
            default -> throw new ApiException("Role non supporte", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public UserDTO updateUser(Integer id, UserRequest request) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ApiException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
        if (request.getRole() != null && request.getRole() != user.getRole()) {
            throw new ApiException("Le changement de role n'est pas supporte", HttpStatus.BAD_REQUEST);
        }
        fillBaseUser(user, request, user.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setMotDePasse(passwordEncoder.encode(request.getPassword()));
        }
        if (user instanceof Patient patient) {
            patient.setTelephone(request.getTelephone());
            patient.setAdresse(request.getAdresse());
            user = patientRepository.save(patient);
        } else if (user instanceof Pharmacien pharmacien) {
            pharmacien.setDiplome(request.getDiplome());
            user = pharmacienRepository.save(pharmacien);
        } else {
            if (user instanceof Administrateur administrateur) {
                administrateur.setNiveauAcces(request.getNiveauAcces() == null ? administrateur.getNiveauAcces() : request.getNiveauAcces());
            }
            user = utilisateurRepository.save(user);
        }
        return UserDTO.from(user);
    }

    public void deleteUser(Integer id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ApiException("Utilisateur introuvable", HttpStatus.NOT_FOUND);
        }
        utilisateurRepository.deleteById(id);
    }

    private void fillBaseUser(Utilisateur user, UserRequest request, String email) {
        user.setNom(request.getNom().trim());
        user.setEmail(email);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setMotDePasse(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
    }
}
