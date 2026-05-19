package tn.pharmaconnect.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
}
