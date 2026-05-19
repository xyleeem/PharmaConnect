package tn.pharmaconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Pharmacien;

public interface PharmacienRepository extends JpaRepository<Pharmacien, Integer> {
}
