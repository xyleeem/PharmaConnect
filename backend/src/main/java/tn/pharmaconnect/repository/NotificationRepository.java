package tn.pharmaconnect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUtilisateur_IdAndIsReadFalse(Integer utilisateurId);
    List<Notification> findByUtilisateur_Id(Integer utilisateurId);
}
