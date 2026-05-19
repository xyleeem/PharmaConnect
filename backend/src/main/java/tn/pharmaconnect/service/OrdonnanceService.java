package tn.pharmaconnect.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pharmaconnect.dto.LigneOrdonnanceRequest;
import tn.pharmaconnect.dto.OrdonnanceRequest;
import tn.pharmaconnect.dto.OrdonnanceResponse;
import tn.pharmaconnect.entity.LignePrescription;
import tn.pharmaconnect.entity.Medicament;
import tn.pharmaconnect.entity.Ordonnance;
import tn.pharmaconnect.entity.Patient;
import tn.pharmaconnect.entity.StatutOrdonnance;
import tn.pharmaconnect.entity.Stock;
import tn.pharmaconnect.exception.ApiException;
import tn.pharmaconnect.repository.LignePrescriptionRepository;
import tn.pharmaconnect.repository.MedicamentRepository;
import tn.pharmaconnect.repository.OrdonnanceRepository;
import tn.pharmaconnect.repository.PatientRepository;
import tn.pharmaconnect.repository.StockRepository;

@Service
public class OrdonnanceService {

    private static final int DEFAULT_PHARMACIE_ID = 1;

    private final OrdonnanceRepository ordonnanceRepository;
    private final PatientRepository patientRepository;
    private final MedicamentRepository medicamentRepository;
    private final LignePrescriptionRepository lignePrescriptionRepository;
    private final StockRepository stockRepository;
    private final NotificationService notificationService;
    private final CommandeService commandeService;

    public OrdonnanceService(
            OrdonnanceRepository ordonnanceRepository,
            PatientRepository patientRepository,
            MedicamentRepository medicamentRepository,
            LignePrescriptionRepository lignePrescriptionRepository,
            StockRepository stockRepository,
            NotificationService notificationService,
            @Lazy CommandeService commandeService) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.patientRepository = patientRepository;
        this.medicamentRepository = medicamentRepository;
        this.lignePrescriptionRepository = lignePrescriptionRepository;
        this.stockRepository = stockRepository;
        this.notificationService = notificationService;
        this.commandeService = commandeService;
    }

    public List<OrdonnanceResponse> findByPatient(Integer patientId) {
        return ordonnanceRepository.findByPatientIdWithDetails(patientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrdonnanceResponse> findAll() {
        return ordonnanceRepository.findAllWithDetails().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrdonnanceResponse> findPending() {
        return ordonnanceRepository.findByStatutWithDetails(StatutOrdonnance.EN_ATTENTE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long countPending() {
        return ordonnanceRepository.findByStatut(StatutOrdonnance.EN_ATTENTE).size();
    }

    @Transactional
    public OrdonnanceResponse create(OrdonnanceRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ApiException("Patient introuvable", HttpStatus.NOT_FOUND));

        Ordonnance ord = new Ordonnance();
        ord.setPatient(patient);
        ord.setDateEmission(LocalDate.now());
        ord.setDateExpiration(LocalDate.now().plusMonths(1));
        ord.setStatut(StatutOrdonnance.EN_ATTENTE);
        ord.setSignatureNumerique(
                request.getSignatureNumerique() != null ? request.getSignatureNumerique() : "sans_photo");
        ord = ordonnanceRepository.save(ord);

        String defaultPosologie = request.getNotes() != null ? request.getNotes() : "A confirmer";
        for (LigneOrdonnanceRequest ligneReq : request.getLignes()) {
            Medicament med = medicamentRepository.findById(ligneReq.getMedicamentId())
                    .orElseThrow(() -> new ApiException("Medicament introuvable: " + ligneReq.getMedicamentId(), HttpStatus.NOT_FOUND));

            LignePrescription ligne = new LignePrescription();
            ligne.setOrdonnance(ord);
            ligne.setMedicament(med);
            ligne.setQuantite(ligneReq.getQuantite() != null ? ligneReq.getQuantite() : 1);
            ligne.setPosologie(ligneReq.getPosologie() != null ? ligneReq.getPosologie() : defaultPosologie);
            ligne.setDuree(ligneReq.getDuree() != null ? ligneReq.getDuree() : 7);
            lignePrescriptionRepository.save(ligne);
        }

        return toResponse(ordonnanceRepository.findById(ord.getId()).orElse(ord));
    }

    @Transactional
    public OrdonnanceResponse approve(Integer ordonnanceId) {
        Ordonnance ord = ordonnanceRepository.findById(ordonnanceId)
                .orElseThrow(() -> new ApiException("Ordonnance introuvable", HttpStatus.NOT_FOUND));

        if (ord.getStatut() != StatutOrdonnance.EN_ATTENTE) {
            throw new ApiException("Cette ordonnance n'est plus en attente", HttpStatus.BAD_REQUEST);
        }

        List<LignePrescription> lignes = lignePrescriptionRepository.findByOrdonnance_Id(ordonnanceId);
        if (lignes.isEmpty()) {
            throw new ApiException("Ordonnance sans lignes de prescription", HttpStatus.BAD_REQUEST);
        }

        for (LignePrescription ligne : lignes) {
            Stock stock = stockRepository
                    .findByMedicament_IdAndPharmacie_Id(ligne.getMedicament().getId(), DEFAULT_PHARMACIE_ID)
                    .orElseThrow(() -> new ApiException("Stock insufficient", HttpStatus.BAD_REQUEST));

            int needed = ligne.getQuantite() != null ? ligne.getQuantite() : 1;
            if (stock.getQuantiteDisponible() == null || stock.getQuantiteDisponible() < needed) {
                throw new ApiException("Stock insufficient", HttpStatus.BAD_REQUEST);
            }
            stock.setQuantiteDisponible(stock.getQuantiteDisponible() - needed);
            stockRepository.save(stock);
        }

        ord.setStatut(StatutOrdonnance.VALIDEE);
        ordonnanceRepository.save(ord);

        String medicineName = lignes.get(0).getMedicament().getNom();
        notificationService.notifyPrescriptionApproved(ord.getPatient().getId(), medicineName);

        commandeService.createFromOrdonnance(ord, lignes);

        return toResponse(ordonnanceRepository.findById(ordonnanceId).orElse(ord));
    }

    @Transactional
    public OrdonnanceResponse reject(Integer ordonnanceId) {
        Ordonnance ord = ordonnanceRepository.findById(ordonnanceId)
                .orElseThrow(() -> new ApiException("Ordonnance introuvable", HttpStatus.NOT_FOUND));
        ord.setStatut(StatutOrdonnance.REFUSEE);
        ordonnanceRepository.save(ord);
        return toResponse(ord);
    }

    public OrdonnanceResponse toResponse(Ordonnance ord) {
        List<LignePrescription> lignes = lignePrescriptionRepository.findByOrdonnance_Id(ord.getId());
        OrdonnanceResponse res = new OrdonnanceResponse();
        res.setId(ord.getId());
        if (ord.getPatient() != null) {
            res.setPatientId(ord.getPatient().getId());
            res.setPatientName(ord.getPatient().getNom());
        }
        res.setStatut(formatStatut(ord.getStatut()));
        res.setDateEmission(ord.getDateEmission());
        res.setDateExpiration(ord.getDateExpiration());

        BigDecimal total = BigDecimal.ZERO;
        StringBuilder items = new StringBuilder();
        String firstMed = "Medicament";
        for (LignePrescription lp : lignes) {
            Medicament m = lp.getMedicament();
            if (m != null) {
                if (firstMed.equals("Medicament")) {
                    firstMed = m.getNom();
                }
                int q = lp.getQuantite() != null ? lp.getQuantite() : 1;
                total = total.add(m.getPrixUnitaire().multiply(BigDecimal.valueOf(q)));
                if (items.length() > 0) {
                    items.append(", ");
                }
                items.append(m.getNom()).append(" x").append(q);
            }
        }
        res.setMedicineName(firstMed);
        res.setTotal(total);
        res.setItems(items.length() > 0 ? items.toString() : "Aucun article");
        return res;
    }

    private String formatStatut(StatutOrdonnance statut) {
        if (statut == null) {
            return "Inconnu";
        }
        return switch (statut) {
            case EN_ATTENTE -> "En attente";
            case VALIDEE -> "Validee";
            case REFUSEE -> "Refusee";
        };
    }
}
