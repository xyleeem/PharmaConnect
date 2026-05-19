package tn.pharmaconnect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.pharmaconnect.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    Optional<Stock> findByMedicament_IdAndPharmacie_Id(Integer medicamentId, Integer pharmacieId);
    List<Stock> findByPharmacie_Id(Integer pharmacieId);
    List<Stock> findByQuantiteDisponibleLessThan(Integer quantity);
}
