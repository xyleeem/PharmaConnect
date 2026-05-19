package tn.pharmaconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Medicament;

public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
}
