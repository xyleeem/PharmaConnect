package tn.pharmaconnect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.LignePrescription;

public interface LignePrescriptionRepository extends JpaRepository<LignePrescription, Integer> {
    List<LignePrescription> findByOrdonnance_Id(Integer ordonnanceId);
}
