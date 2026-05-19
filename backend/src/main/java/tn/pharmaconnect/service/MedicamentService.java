package tn.pharmaconnect.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pharmaconnect.dto.MedicamentCreateRequest;
import tn.pharmaconnect.dto.MedicamentDTO;
import tn.pharmaconnect.entity.Medicament;
import tn.pharmaconnect.entity.Pharmacie;
import tn.pharmaconnect.entity.Stock;
import tn.pharmaconnect.repository.MedicamentRepository;
import tn.pharmaconnect.repository.PharmacieRepository;
import tn.pharmaconnect.repository.StockRepository;

@Service
public class MedicamentService {

    private static final int DEFAULT_PHARMACIE_ID = 1;

    private final MedicamentRepository medicamentRepository;
    private final StockRepository stockRepository;
    private final PharmacieRepository pharmacieRepository;

    public MedicamentService(
            MedicamentRepository medicamentRepository,
            StockRepository stockRepository,
            PharmacieRepository pharmacieRepository) {
        this.medicamentRepository = medicamentRepository;
        this.stockRepository = stockRepository;
        this.pharmacieRepository = pharmacieRepository;
    }

    public List<MedicamentDTO> findAllWithStock() {
        return medicamentRepository.findAll().stream()
                .map(this::toDtoWithStock)
                .collect(Collectors.toList());
    }

    public List<MedicamentDTO> findAvailableForPatients() {
        return findAllWithStock().stream()
                .filter(m -> m.getQuantiteDisponible() != null && m.getQuantiteDisponible() > 0)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicamentDTO create(MedicamentCreateRequest request) {
        Medicament med = new Medicament();
        med.setNom(request.getNom());
        med.setCategorie(request.getCategorie());
        med.setPrixUnitaire(request.getPrixUnitaire());
        med = medicamentRepository.save(med);

        Pharmacie pharmacie = pharmacieRepository.findById(DEFAULT_PHARMACIE_ID).orElseGet(() -> {
            Pharmacie p = new Pharmacie();
            p.setNom("Pharmacie CityCare");
            p.setAdresse("Tunis");
            return pharmacieRepository.save(p);
        });

        Stock stock = new Stock();
        stock.setMedicament(med);
        stock.setPharmacie(pharmacie);
        stock.setQuantiteDisponible(request.getStock() != null ? request.getStock() : 0);
        stock.setDateExpiration(LocalDate.of(2027, 12, 31));
        stockRepository.save(stock);

        return toDtoWithStock(med);
    }

    public long countMedicaments() {
        return medicamentRepository.count();
    }

    private MedicamentDTO toDtoWithStock(Medicament med) {
        Integer qty = stockRepository
                .findByMedicament_IdAndPharmacie_Id(med.getId(), DEFAULT_PHARMACIE_ID)
                .map(Stock::getQuantiteDisponible)
                .orElse(0);
        return new MedicamentDTO(med.getId(), med.getNom(), med.getCategorie(), med.getPrixUnitaire(), qty);
    }
}
