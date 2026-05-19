package tn.pharmaconnect.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pharmaconnect.dto.CommandeDTO;
import tn.pharmaconnect.entity.Commande;
import tn.pharmaconnect.entity.LignePrescription;
import tn.pharmaconnect.entity.Medicament;
import tn.pharmaconnect.entity.Ordonnance;
import tn.pharmaconnect.entity.StatutCommande;
import tn.pharmaconnect.exception.ApiException;
import tn.pharmaconnect.repository.CommandeRepository;
import tn.pharmaconnect.repository.LignePrescriptionRepository;

@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final LignePrescriptionRepository lignePrescriptionRepository;

    public CommandeService(CommandeRepository commandeRepository, LignePrescriptionRepository lignePrescriptionRepository) {
        this.commandeRepository = commandeRepository;
        this.lignePrescriptionRepository = lignePrescriptionRepository;
    }

    public List<CommandeDTO> findAll() {
        return commandeRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CommandeDTO> findByPatient(Integer patientId) {
        return commandeRepository.findByPatient_Id(patientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public long countTodayOrders() {
        return commandeRepository.findByDateCommande(LocalDate.now()).size();
    }

    @Transactional
    public Commande createFromOrdonnance(Ordonnance ord, List<LignePrescription> lignes) {
        BigDecimal total = BigDecimal.ZERO;
        for (LignePrescription lp : lignes) {
            Medicament m = lp.getMedicament();
            int q = lp.getQuantite() != null ? lp.getQuantite() : 1;
            total = total.add(m.getPrixUnitaire().multiply(BigDecimal.valueOf(q)));
        }

        Commande cmd = new Commande();
        cmd.setOrdonnance(ord);
        cmd.setPatient(ord.getPatient());
        cmd.setDateCommande(LocalDate.now());
        cmd.setStatut(StatutCommande.Preparation);
        cmd.setMontantTotal(total);
        cmd = commandeRepository.save(cmd);
        cmd.setCodeCommande("PC-TN-" + (2100 + cmd.getId()));
        return commandeRepository.save(cmd);
    }

    @Transactional
    public CommandeDTO updateStatus(Integer id, String status) {
        Commande cmd = commandeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Commande introuvable", HttpStatus.NOT_FOUND));
        try {
            cmd.setStatut(StatutCommande.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new ApiException("Statut invalide: " + status, HttpStatus.BAD_REQUEST);
        }
        return toDto(commandeRepository.save(cmd));
    }

    @Transactional
    public CommandeDTO updateStatusByCode(String code, String status) {
        Commande cmd = commandeRepository.findByCodeCommande(code)
                .orElseThrow(() -> new ApiException("Commande introuvable", HttpStatus.NOT_FOUND));
        return updateStatus(cmd.getId(), status);
    }

    private CommandeDTO toDto(Commande cmd) {
        String items = "";
        if (cmd.getOrdonnance() != null) {
            var lignes = lignePrescriptionRepository.findByOrdonnance_Id(cmd.getOrdonnance().getId());
            items = lignes.stream()
                    .map(lp -> lp.getMedicament().getNom() + " x" + lp.getQuantite())
                    .collect(Collectors.joining(", "));
        }
        return new CommandeDTO(
                cmd.getId(),
                cmd.getCodeCommande(),
                cmd.getDateCommande(),
                cmd.getStatut() != null ? cmd.getStatut().name() : "",
                cmd.getMontantTotal(),
                items);
    }
}
