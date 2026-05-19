package tn.pharmaconnect.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.pharmaconnect.dto.CommandeDTO;
import tn.pharmaconnect.dto.CommandeStatusRequest;
import tn.pharmaconnect.dto.DashboardStatsDTO;
import tn.pharmaconnect.dto.MedicamentCreateRequest;
import tn.pharmaconnect.dto.MedicamentDTO;
import tn.pharmaconnect.dto.OrdonnanceResponse;
import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.service.CommandeService;
import tn.pharmaconnect.service.MedicamentService;
import tn.pharmaconnect.service.OrdonnanceService;
import tn.pharmaconnect.service.StockService;
import tn.pharmaconnect.util.AuthContext;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    private final MedicamentService medicamentService;
    private final StockService stockService;
    private final OrdonnanceService ordonnanceService;
    private final CommandeService commandeService;

    public PharmacyController(
            MedicamentService medicamentService,
            StockService stockService,
            OrdonnanceService ordonnanceService,
            CommandeService commandeService) {
        this.medicamentService = medicamentService;
        this.stockService = stockService;
        this.ordonnanceService = ordonnanceService;
        this.commandeService = commandeService;
    }

    private void requireStaff(String roleHeader) {
        AuthContext.requirePharmacyStaff(AuthContext.requireRole(roleHeader));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> stats(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(new DashboardStatsDTO(
                medicamentService.countMedicaments(),
                stockService.countLowStock(),
                ordonnanceService.countPending(),
                commandeService.countTodayOrders()));
    }

    @GetMapping("/medicaments")
    public ResponseEntity<List<MedicamentDTO>> medicaments(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(medicamentService.findAllWithStock());
    }

    @PostMapping("/medicament")
    public ResponseEntity<MedicamentDTO> addMedicament(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @Valid @RequestBody MedicamentCreateRequest request) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(medicamentService.create(request));
    }

    @GetMapping("/stock/low")
    public ResponseEntity<List<MedicamentDTO>> lowStock(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireStaff(roleHeader);
        List<MedicamentDTO> all = medicamentService.findAllWithStock();
        return ResponseEntity.ok(all.stream()
                .filter(m -> m.getQuantiteDisponible() != null && m.getQuantiteDisponible() < StockService.LOW_STOCK_THRESHOLD)
                .toList());
    }

    @GetMapping("/ordonnances")
    public ResponseEntity<List<OrdonnanceResponse>> allOrdonnances(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(ordonnanceService.findAll());
    }

    @GetMapping("/ordonnances/pending")
    public ResponseEntity<List<OrdonnanceResponse>> pendingOrdonnances(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(ordonnanceService.findPending());
    }

    @PutMapping("/ordonnance/{id}/approve")
    public ResponseEntity<OrdonnanceResponse> approve(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer id) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(ordonnanceService.approve(id));
    }

    @PutMapping("/ordonnance/{id}/reject")
    public ResponseEntity<OrdonnanceResponse> reject(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer id) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(ordonnanceService.reject(id));
    }

    @GetMapping("/commandes")
    public ResponseEntity<List<CommandeDTO>> commandes(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(commandeService.findAll());
    }

    @PutMapping("/commande/{id}/status")
    public ResponseEntity<CommandeDTO> updateCommandeStatus(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer id,
            @Valid @RequestBody CommandeStatusRequest request) {
        requireStaff(roleHeader);
        return ResponseEntity.ok(commandeService.updateStatus(id, request.getStatus()));
    }
}
