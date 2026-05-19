package tn.pharmaconnect.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pharmaconnect.dto.LoginRequest;
import tn.pharmaconnect.dto.LoginResponse;
import tn.pharmaconnect.dto.RegisterRequest;
import tn.pharmaconnect.entity.Patient;
import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.exception.ApiException;
import tn.pharmaconnect.repository.PatientRepository;
import tn.pharmaconnect.repository.UtilisateurRepository;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        var user = utilisateurRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ApiException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED));

        if (!matchesPassword(request.getPassword(), user.getMotDePasse())) {
            throw new ApiException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        }

        return new LoginResponse(user.getId(), user.getNom(), user.getEmail(), user.getRole());
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(email)) {
            throw new ApiException("Cet email est deja utilise", HttpStatus.CONFLICT);
        }

        Patient patient = new Patient();
        patient.setNom(request.getNom().trim());
        patient.setEmail(email);
        patient.setMotDePasse(passwordEncoder.encode(request.getPassword()));
        patient.setRole(Role.PATIENT);
        patient.setTelephone(request.getTelephone());
        patient.setAdresse(request.getAdresse());

        patient = patientRepository.save(patient);
        return new LoginResponse(patient.getId(), patient.getNom(), patient.getEmail(), patient.getRole());
    }

    private boolean matchesPassword(String raw, String stored) {
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$")) {
            return passwordEncoder.matches(raw, stored);
        }
        return stored.equals(raw);
    }
}
