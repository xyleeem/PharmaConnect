package tn.pharmaconnect.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    Optional<Commande> findByCodeCommande(String codeCommande);
    List<Commande> findByPatient_Id(Integer patientId);
    List<Commande> findByDateCommande(LocalDate date);
}
