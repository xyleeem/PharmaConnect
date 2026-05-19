package tn.pharmaconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Pharmacie;

public interface PharmacieRepository extends JpaRepository<Pharmacie, Integer> {
}
