package tn.pharmaconnect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.pharmaconnect.entity.Ordonnance;
import tn.pharmaconnect.entity.StatutOrdonnance;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Integer> {
    List<Ordonnance> findByPatient_Id(Integer patientId);
    List<Ordonnance> findByStatut(StatutOrdonnance statut);

    @Query("SELECT o FROM Ordonnance o LEFT JOIN FETCH o.patient LEFT JOIN FETCH o.lignes l LEFT JOIN FETCH l.medicament WHERE o.patient.id = :patientId")
    List<Ordonnance> findByPatientIdWithDetails(@Param("patientId") Integer patientId);

    @Query("SELECT DISTINCT o FROM Ordonnance o LEFT JOIN FETCH o.patient LEFT JOIN FETCH o.lignes l LEFT JOIN FETCH l.medicament")
    List<Ordonnance> findAllWithDetails();

    @Query("SELECT DISTINCT o FROM Ordonnance o LEFT JOIN FETCH o.patient LEFT JOIN FETCH o.lignes l LEFT JOIN FETCH l.medicament WHERE o.statut = :statut")
    List<Ordonnance> findByStatutWithDetails(@Param("statut") StatutOrdonnance statut);
}
