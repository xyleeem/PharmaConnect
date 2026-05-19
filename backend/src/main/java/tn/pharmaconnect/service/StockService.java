package tn.pharmaconnect.service;

import java.util.List;
import org.springframework.stereotype.Service;
import tn.pharmaconnect.entity.Stock;
import tn.pharmaconnect.repository.StockRepository;

@Service
public class StockService {

    public static final int LOW_STOCK_THRESHOLD = 10;
    private static final int DEFAULT_PHARMACIE_ID = 1;

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> findLowStock() {
        return stockRepository.findByQuantiteDisponibleLessThan(LOW_STOCK_THRESHOLD);
    }

    public Stock getStockForMedicament(Integer medicamentId) {
        return stockRepository
                .findByMedicament_IdAndPharmacie_Id(medicamentId, DEFAULT_PHARMACIE_ID)
                .orElse(null);
    }

    public long countLowStock() {
        return stockRepository.findByQuantiteDisponibleLessThan(LOW_STOCK_THRESHOLD).size();
    }
}
