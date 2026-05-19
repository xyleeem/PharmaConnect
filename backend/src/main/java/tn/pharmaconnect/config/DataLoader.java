package tn.pharmaconnect.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.pharmaconnect.entity.Administrateur;
import tn.pharmaconnect.entity.Commande;
import tn.pharmaconnect.entity.LignePrescription;
import tn.pharmaconnect.entity.Medicament;
import tn.pharmaconnect.entity.Notification;
import tn.pharmaconnect.entity.Ordonnance;
import tn.pharmaconnect.entity.Patient;
import tn.pharmaconnect.entity.Pharmacie;
import tn.pharmaconnect.entity.Pharmacien;
import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.entity.StatutCommande;
import tn.pharmaconnect.entity.StatutOrdonnance;
import tn.pharmaconnect.entity.Stock;
import tn.pharmaconnect.repository.CommandeRepository;
import tn.pharmaconnect.repository.LignePrescriptionRepository;
import tn.pharmaconnect.repository.MedicamentRepository;
import tn.pharmaconnect.repository.NotificationRepository;
import tn.pharmaconnect.repository.OrdonnanceRepository;
import tn.pharmaconnect.repository.PatientRepository;
import tn.pharmaconnect.repository.PharmacieRepository;
import tn.pharmaconnect.repository.PharmacienRepository;
import tn.pharmaconnect.repository.StockRepository;
import tn.pharmaconnect.repository.UtilisateurRepository;

@Configuration
public class DataLoader {

    public static final String DEMO_PASSWORD = "password123";

    @Bean
    CommandLineRunner loadInitialData(
            UtilisateurRepository utilisateurRepository,
            PatientRepository patientRepository,
            PharmacienRepository pharmacienRepository,
            PharmacieRepository pharmacieRepository,
            MedicamentRepository medicamentRepository,
            StockRepository stockRepository,
            OrdonnanceRepository ordonnanceRepository,
            LignePrescriptionRepository lignePrescriptionRepository,
            NotificationRepository notificationRepository,
            CommandeRepository commandeRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (utilisateurRepository.count() > 0) {
                return;
            }

            String encodedPwd = passwordEncoder.encode(DEMO_PASSWORD);

            Pharmacie pharmacie = new Pharmacie();
            pharmacie.setNom("Pharmacie CityCare");
            pharmacie.setAdresse("Centre Urbain Nord, Tunis");
            pharmacie.setHoraires("08:00 - 22:00");
            pharmacie.setTelephone("+216 71 111 222");
            pharmacie = pharmacieRepository.save(pharmacie);

            Patient ahmed = new Patient();
            ahmed.setNom("Ahmed Ben Salah");
            ahmed.setEmail("ahmed@pharmaconnect.tn");
            ahmed.setMotDePasse(encodedPwd);
            ahmed.setRole(Role.PATIENT);
            ahmed.setDateNaissance(LocalDate.of(1993, 6, 19));
            ahmed.setAdresse("Ariana, Tunisie");
            ahmed.setTelephone("+216 22 123 456");
            ahmed = patientRepository.save(ahmed);

            Patient sana = new Patient();
            sana.setNom("Sana Trabelsi");
            sana.setEmail("sana@pharmaconnect.tn");
            sana.setMotDePasse(encodedPwd);
            sana.setRole(Role.PATIENT);
            sana.setDateNaissance(LocalDate.of(1988, 11, 2));
            sana.setAdresse("Sfax, Tunisie");
            sana.setTelephone("+216 55 987 654");
            sana = patientRepository.save(sana);

            Pharmacien youssef = new Pharmacien();
            youssef.setNom("Dr. Youssef Gharbi");
            youssef.setEmail("youssef@pharmaciecentrale.tn");
            youssef.setMotDePasse(encodedPwd);
            youssef.setRole(Role.PHARMACIEN);
            youssef.setDiplome("Docteur en Pharmacie");
            youssef.setPharmacie(pharmacie);
            pharmacienRepository.save(youssef);

            Administrateur admin = new Administrateur();
            admin.setNom("Admin PharmaConnect");
            admin.setEmail("admin@pharmaconnect.tn");
            admin.setMotDePasse(encodedPwd);
            admin.setRole(Role.ADMIN);
            admin.setNiveauAcces(10);
            utilisateurRepository.save(admin);

            Medicament m1 = saveMed(medicamentRepository, "Paracetamol 500mg", "Antalgique", "5.8");
            Medicament m2 = saveMed(medicamentRepository, "Amoxicilline 1g", "Antibiotique", "19.5");
            Medicament m3 = saveMed(medicamentRepository, "Sirop Toux 150ml", "ORL", "13.2");
            Medicament m4 = saveMed(medicamentRepository, "Vitamine C 1000mg", "Complement", "16.9");

            saveStock(stockRepository, m1, pharmacie, 52, "2027-03-01");
            saveStock(stockRepository, m2, pharmacie, 12, "2026-12-20");
            saveStock(stockRepository, m3, pharmacie, 8, "2027-01-11");
            saveStock(stockRepository, m4, pharmacie, 20, "2027-04-15");

            Ordonnance o1 = saveOrd(ordonnanceRepository, ahmed, "2026-05-02", StatutOrdonnance.EN_ATTENTE);
            Ordonnance o2 = saveOrd(ordonnanceRepository, sana, "2026-05-01", StatutOrdonnance.VALIDEE);
            Ordonnance o3 = saveOrd(ordonnanceRepository, ahmed, "2026-04-22", StatutOrdonnance.REFUSEE);

            saveLigne(lignePrescriptionRepository, o1, m1, 2, "1 cp x 3/jour", 5);
            saveLigne(lignePrescriptionRepository, o2, m2, 1, "1 cp x 2/jour", 7);
            saveLigne(lignePrescriptionRepository, o3, m3, 1, "10 ml x 3/jour", 4);

            Notification notif = new Notification();
            notif.setUtilisateur(ahmed);
            notif.setMessage("Votre commande est prete a la Pharmacie CityCare.");
            notif.setType("ORDONNANCE");
            notif.setDateEnvoi(LocalDateTime.of(2026, 5, 5, 15, 10));
            notif.setIsRead(false);
            notificationRepository.save(notif);

            seedCommande(commandeRepository, "PC-TN-2105", o2, sana, "2026-05-05", StatutCommande.Preparation, "38.4");
            seedCommande(commandeRepository, "PC-TN-2104", null, ahmed, "2026-05-05", StatutCommande.Pret, "19.5");
            seedCommande(commandeRepository, "PC-TN-2103", null, sana, "2026-05-05", StatutCommande.Livre, "26.4");
            seedCommande(commandeRepository, "PC-TN-2102", null, ahmed, "2026-05-04", StatutCommande.Livre, "16.9");
            seedCommande(commandeRepository, "PC-TN-2101", null, ahmed, "2026-05-04", StatutCommande.Annule, "0");

            System.out.println(">>> PharmaConnect: donnees initiales chargees. Mot de passe demo: " + DEMO_PASSWORD);
        };
    }

    private static Medicament saveMed(MedicamentRepository repo, String nom, String cat, String prix) {
        Medicament m = new Medicament();
        m.setNom(nom);
        m.setCategorie(cat);
        m.setPrixUnitaire(new BigDecimal(prix));
        return repo.save(m);
    }

    private static void saveStock(
            StockRepository repo, Medicament med, Pharmacie ph, int qty, String exp) {
        Stock s = new Stock();
        s.setMedicament(med);
        s.setPharmacie(ph);
        s.setQuantiteDisponible(qty);
        s.setDateExpiration(LocalDate.parse(exp));
        repo.save(s);
    }

    private static Ordonnance saveOrd(
            OrdonnanceRepository repo, Patient patient, String date, StatutOrdonnance statut) {
        Ordonnance o = new Ordonnance();
        o.setPatient(patient);
        o.setDateEmission(LocalDate.parse(date));
        o.setDateExpiration(LocalDate.parse(date).plusMonths(1));
        o.setStatut(statut);
        o.setSignatureNumerique("sig_demo");
        return repo.save(o);
    }

    private static void saveLigne(
            LignePrescriptionRepository repo,
            Ordonnance ord,
            Medicament med,
            int qty,
            String posologie,
            int duree) {
        LignePrescription lp = new LignePrescription();
        lp.setOrdonnance(ord);
        lp.setMedicament(med);
        lp.setQuantite(qty);
        lp.setPosologie(posologie);
        lp.setDuree(duree);
        repo.save(lp);
    }

    private static void seedCommande(
            CommandeRepository repo,
            String code,
            Ordonnance ord,
            Patient patient,
            String date,
            StatutCommande statut,
            String total) {
        Commande c = new Commande();
        c.setCodeCommande(code);
        c.setOrdonnance(ord);
        c.setPatient(patient);
        c.setDateCommande(LocalDate.parse(date));
        c.setStatut(statut);
        c.setMontantTotal(new BigDecimal(total));
        repo.save(c);
    }
}
