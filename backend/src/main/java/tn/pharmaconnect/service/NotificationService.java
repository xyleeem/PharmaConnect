package tn.pharmaconnect.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pharmaconnect.dto.NotificationDTO;
import tn.pharmaconnect.entity.Notification;
import tn.pharmaconnect.entity.Utilisateur;
import tn.pharmaconnect.repository.NotificationRepository;
import tn.pharmaconnect.repository.UtilisateurRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<NotificationDTO> getUnreadForUser(Integer userId) {
        return notificationRepository.findByUtilisateur_IdAndIsReadFalse(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getAllForUser(Integer userId) {
        return notificationRepository.findByUtilisateur_Id(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void notifyPrescriptionApproved(Integer userId, String medicineName) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow();
        Notification n = new Notification();
        n.setUtilisateur(user);
        n.setMessage("Votre ordonnance pour " + medicineName + " a ete validee. Votre commande est en preparation.");
        n.setType("ORDONNANCE");
        n.setDateEnvoi(LocalDateTime.now());
        n.setIsRead(false);
        notificationRepository.save(n);
    }

    private NotificationDTO toDto(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getMessage(),
                n.getType(),
                n.getDateEnvoi(),
                n.getIsRead());
    }
}
