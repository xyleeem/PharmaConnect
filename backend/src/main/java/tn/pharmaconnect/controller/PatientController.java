package tn.pharmaconnect.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.pharmaconnect.dto.CommandeDTO;
import tn.pharmaconnect.dto.MedicamentDTO;
import tn.pharmaconnect.dto.NotificationDTO;
import tn.pharmaconnect.dto.OrdonnanceRequest;
import tn.pharmaconnect.dto.OrdonnanceResponse;
import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.service.CommandeService;
import tn.pharmaconnect.service.MedicamentService;
import tn.pharmaconnect.service.NotificationService;
import tn.pharmaconnect.service.OrdonnanceService;
import tn.pharmaconnect.util.AuthContext;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final OrdonnanceService ordonnanceService;
    private final MedicamentService medicamentService;
    private final NotificationService notificationService;
    private final CommandeService commandeService;

    public PatientController(
            OrdonnanceService ordonnanceService,
            MedicamentService medicamentService,
            NotificationService notificationService,
            CommandeService commandeService) {
        this.ordonnanceService = ordonnanceService;
        this.medicamentService = medicamentService;
        this.notificationService = notificationService;
        this.commandeService = commandeService;
    }

    @GetMapping("/ordonnances/{patientId}")
    public ResponseEntity<List<OrdonnanceResponse>> getOrdonnances(
            @RequestHeader(value = AuthContext.HEADER_USER_ID, required = false) String userIdHeader,
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer patientId) {
        Integer userId = AuthContext.requireUserId(userIdHeader);
        Role role = AuthContext.requireRole(roleHeader);
        AuthContext.requirePatient(role);
        AuthContext.requireOwnPatient(userId, patientId);
        return ResponseEntity.ok(ordonnanceService.findByPatient(patientId));
    }

    @PostMapping("/ordonnance")
    public ResponseEntity<OrdonnanceResponse> createOrdonnance(
            @RequestHeader(value = AuthContext.HEADER_USER_ID, required = false) String userIdHeader,
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @Valid @RequestBody OrdonnanceRequest request) {
        Integer userId = AuthContext.requireUserId(userIdHeader);
        Role role = AuthContext.requireRole(roleHeader);
        AuthContext.requirePatient(role);
        AuthContext.requireOwnPatient(userId, request.getPatientId());
        return ResponseEntity.ok(ordonnanceService.create(request));
    }

    @GetMapping("/medicaments")
    public ResponseEntity<List<MedicamentDTO>> getMedicaments() {
        return ResponseEntity.ok(medicamentService.findAvailableForPatients());
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestHeader(value = AuthContext.HEADER_USER_ID, required = false) String userIdHeader,
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer userId) {
        Integer loggedId = AuthContext.requireUserId(userIdHeader);
        Role role = AuthContext.requireRole(roleHeader);
        AuthContext.requirePatient(role);
        AuthContext.requireOwnPatient(loggedId, userId);
        return ResponseEntity.ok(notificationService.getAllForUser(userId));
    }

    @GetMapping("/commandes/{patientId}")
    public ResponseEntity<List<CommandeDTO>> getCommandes(
            @RequestHeader(value = AuthContext.HEADER_USER_ID, required = false) String userIdHeader,
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer patientId) {
        Integer userId = AuthContext.requireUserId(userIdHeader);
        Role role = AuthContext.requireRole(roleHeader);
        AuthContext.requirePatient(role);
        AuthContext.requireOwnPatient(userId, patientId);
        return ResponseEntity.ok(commandeService.findByPatient(patientId));
    }
}
