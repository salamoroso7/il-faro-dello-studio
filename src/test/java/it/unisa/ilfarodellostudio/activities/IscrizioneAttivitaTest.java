package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.payments.PaymentsService;
import it.unisa.ilfarodellostudio.payments.entity.Pagamento;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class IscrizioneAttivitaTest {

    @Autowired private ActivitiesService activitiesService;
    @Autowired private AttivitaRepository attivitaRepository;
    @Autowired private StudenteRepository studenteRepository;
    @Autowired private FamigliaRepository famigliaRepository;
    @Autowired private MateriaRepository materiaRepository;
    @Autowired private PaymentsService paymentsService;

    private Materia materiaBase;
    private Famiglia famigliaBase;

    // Metodi Helper per ridurre il codice
    private Materia creaMateria(String nome) {
        Materia m = new Materia();
        m.setNome(nome);
        return materiaRepository.save(m);
    }

    private Famiglia creaFamiglia(String email) {
        Famiglia f = new Famiglia();
        f.setEmail(email);
        f.setNome("NomeFamiglia");
        f.setCognome("CognomeFamiglia");
        f.setPassword("password");
        return famigliaRepository.save(f);
    }

    private Studente creaStudente(String email, Famiglia f) {
        Studente s = new Studente();
        s.setEmail(email);
        s.setNome("Studente");
        s.setCognome("Test");

        // Generiamo un CF di esattamente 16 caratteri
        String timestamp = String.valueOf(System.currentTimeMillis());
        s.setCodiceFiscale(("CF" + timestamp + "AAAAAAAAAAAAAAA").substring(0, 16));

        s.setDataNascita(LocalDate.of(2010, 1, 1));
        s.setPassword("password");
        s.setFamiglia(f);
        return studenteRepository.save(s);
    }

    private Attivita creaAttivita(String titolo, int posti, Materia m) {
        Attivita a = new Attivita();
        a.setTitolo(titolo);
        a.setPosti(posti);
        a.setMateria(m);
        a.setData(LocalDate.now().plusDays(7));
        a.setOraInizio(LocalTime.of(15, 0));
        a.setOraFine(LocalTime.of(17, 0));
        return attivitaRepository.save(a);
    }

    // --- TEST CASES ---

    // TC_GA_6_1
    @Test
    void testIscrizionePostiMassimi() {
        Materia m = creaMateria("Algebra");
        Famiglia f = creaFamiglia("famiglia.limite@test.com");
        Attivita a = creaAttivita("Corso Algebra", 2, m);

        // Riempiamo i posti
        creaStudente("s1@test.com", f);
        creaStudente("s2@test.com", f);

        // Iscriviamo manualmente i primi due
        for (Studente s : studenteRepository.findAll()) {
            if(!s.getEmail().contains("extra")) a.aggiungiStudente(s);
        }
        attivitaRepository.save(a);

        // Studente che proverà a entrare nel corso pieno
        Studente sExtra = creaStudente("extra@test.com", f);

        // Verifica eccezione
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.iscriviStudenteAdAttivita(sExtra.getEmail(), a.getIdAttivita());
        });

        assertEquals("Impossibile iscriversi: l'attività " + a.getTitolo() + " è già al completo", exception.getMessage());
    }

    // TC_GA_6_2
    @Test
    void testIscrizionePagamentiScaduti() {
        // 1. Setup dati base
        Materia m = creaMateria("Storia");
        Famiglia f = creaFamiglia("famiglia.scaduta@test.com");
        Studente s = creaStudente("studente.debitore@test.com", f);
        Attivita a = creaAttivita("Ripasso Storia", 5, m);

        // 2. Creiamo un pagamento con scadenza nel passato (ieri)
        Pagamento p = new Pagamento();
        p.setNome("Resta Scolastica Arretrato");
        p.setImporto(150.0);
        p.setDataScadenza(LocalDate.now().minusDays(1)); // Scaduto ieri

        paymentsService.creaPagamentoGenerale(p);

        paymentsService.aggiornaStatiScaduti();

        // 4. Verifica eccezione durante l'iscrizione
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.iscriviStudenteAdAttivita(s.getEmail(), a.getIdAttivita());
        });

        // Verifichiamo che il messaggio contenga il riferimento al debito
        assertTrue(exception.getMessage().contains("SCADUTO"),
                "L'eccezione dovrebbe menzionare i pagamenti scaduti");
    }

    // TC_GA_6_3
    @Test
    void testIscrizioneCorretta() {
        Materia m = creaMateria("Inglese");
        Famiglia f = creaFamiglia("famiglia.ok@test.com");
        Studente s = creaStudente("studente.ok@test.com", f);
        Attivita a = creaAttivita("Lab Inglese", 10, m);

        // Esecuzione
        activitiesService.iscriviStudenteAdAttivita(s.getEmail(), a.getIdAttivita());

        // Verifica
        Attivita salvata = attivitaRepository.findById(a.getIdAttivita()).orElseThrow();
        assertTrue(salvata.getIscritti().stream().anyMatch(is -> is.getEmail().equals(s.getEmail())));
    }
}
