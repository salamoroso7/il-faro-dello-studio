package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la creazione di nuove attività didattiche.
 * Copre i test case da TC_GA_1_1 a TC_GA_1_5 definiti nel TCS v1.0.
 */
@SpringBootTest
@Transactional
public class CreaAttivitaTest {

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private AttivitaRepository attivitaRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    // --- METODI HELPER ---

    private Docente creaDocente(String email) {
        Docente d = new Docente();
        d.setEmail(email);
        d.setNome("Mario");
        d.setCognome("Alti");
        d.setPassword("passwordSecure");
        d.setAttivo(true);
        return docenteRepository.save(d);
    }

    private Materia recuperaOCreaMateria(String nome) {
        return materiaRepository.findByNome(nome).orElseGet(() -> {
            Materia m = new Materia();
            m.setNome(nome);
            return materiaRepository.save(m);
        });
    }

    // --- TEST CASES ---

    /**
     * TC_GA_1_1 - Formato Materia Non Valido
     * Verifica che il sistema rifiuti la creazione se il nome della materia contiene numeri.
     * Input: Nome Materia "Informatica1"
     */
    @Test
    void testCreazioneMateriaNomeNonValido() {
        // Setup
        Docente docente = creaDocente("prof.test1@unisa.it");
        Materia materiaNonValida = new Materia();
        materiaNonValida.setNome("Informatica1"); // Nome con numeri
        // Nota: Si assume che la validazione avvenga nel Service o tramite Bean Validation prima del save

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Test");
        attivita.setDescrizione("Descrizione valida");
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(12, 0));
        attivita.setOraFine(LocalTime.of(14, 0));
        attivita.setPosti(30);
        attivita.setMateria(materiaNonValida);
        attivita.setDocente(docente);

        // Oracolo: La creazione non va a buon fine
        Exception exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.creaAttivita(attivita);
        });

        // Verifica opzionale sul messaggio se implementato
        // assertTrue(exception.getMessage().contains("formato"), "Dovrebbe segnalare errore formato materia");
    }

    /**
     * TC_GA_1_2 - Data nel passato
     * Verifica che il sistema rifiuti la creazione se la data è antecedente a oggi.
     * Input: Data 11/06/2025 (considerata passata nel contesto del test)
     */
    @Test
    void testCreazioneDataPassata() {
        // Setup
        Docente docente = creaDocente("prof.test2@unisa.it");
        Materia materia = recuperaOCreaMateria("Informatica");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Passata");
        attivita.setDescrizione("Descrizione valida");
        // Usiamo una data dinamica nel passato per rendere il test robusto sempre
        attivita.setData(LocalDate.now().minusDays(1));
        attivita.setOraInizio(LocalTime.of(12, 0));
        attivita.setOraFine(LocalTime.of(14, 0));
        attivita.setPosti(30);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        // Oracolo: La creazione non va a buon fine
        Exception exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.creaAttivita(attivita);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("passato") ||
                        exception.getMessage().toLowerCase().contains("data"),
                "Dovrebbe segnalare errore data nel passato");
    }

    /**
     * TC_GA_1_3 - Descrizione troppo lunga
     * Verifica che il sistema rifiuti descrizioni superiori a 300 caratteri.
     * Input: Descrizione > 300 chars
     */
    @Test
    void testCreazioneDescrizioneTroppoLunga() {
        // Setup
        Docente docente = creaDocente("prof.test3@unisa.it");
        Materia materia = recuperaOCreaMateria("Informatica");

        // Genera una stringa di 301 caratteri
        String descrizioneLunga = String.join("", Collections.nCopies(31, "1234567890")) + "1";

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Verbosa");
        attivita.setDescrizione(descrizioneLunga);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(12, 0));
        attivita.setOraFine(LocalTime.of(14, 0));
        attivita.setPosti(30);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        // Oracolo: La creazione non va a buon fine
        assertThrows(RuntimeException.class, () -> {
            activitiesService.creaAttivita(attivita);
        });
    }

    /**
     * TC_GA_1_4 - Sovrapposizione Oraria
     * Verifica che il sistema rifiuti la creazione se il docente è già impegnato in quell'orario.
     * Pre-condizione: Esiste già una lezione alle 11:00.
     * Input: Nuova lezione alle 11:00 (o sovrapposta).
     */
    @Test
    void testCreazioneSovrapposta() {
        // Setup Docente e Materia
        Docente docente = creaDocente("prof.test4@unisa.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate dataLezione = LocalDate.now().plusDays(10);

        // 1. Creiamo la prima attività (Conflitto esistente)
        Attivita attivitaEsistente = new Attivita();
        attivitaEsistente.setTitolo("Lezione Esistente");
        attivitaEsistente.setDescrizione("Descrizione ok");
        attivitaEsistente.setData(dataLezione);
        attivitaEsistente.setOraInizio(LocalTime.of(11, 0)); // 11:00
        attivitaEsistente.setOraFine(LocalTime.of(13, 0));   // 13:00
        attivitaEsistente.setPosti(30);
        attivitaEsistente.setMateria(materia);
        attivitaEsistente.setDocente(docente);

        // Salviamo la prima lezione (questa deve andare a buon fine)
        activitiesService.creaAttivita(attivitaEsistente);

        // 2. Tentiamo di creare la seconda attività sovrapposta (Input del TC)
        Attivita attivitaSovrapposta = new Attivita();
        attivitaSovrapposta.setTitolo("Lezione Sovrapposta");
        attivitaSovrapposta.setDescrizione("Descrizione ok");
        attivitaSovrapposta.setData(dataLezione);
        attivitaSovrapposta.setOraInizio(LocalTime.of(11, 30)); // Inizia durante l'altra
        attivitaSovrapposta.setOraFine(LocalTime.of(13, 30));
        attivitaSovrapposta.setPosti(30);
        attivitaSovrapposta.setMateria(materia);
        attivitaSovrapposta.setDocente(docente);

        // Oracolo: La creazione non va a buon fine per sovrapposizione
        Exception exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.creaAttivita(attivitaSovrapposta);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("sovrapposizione") ||
                        exception.getMessage().toLowerCase().contains("impegnato"),
                "Dovrebbe segnalare sovrapposizione oraria");
    }

    /**
     * TC_GA_1_5 - Creazione con Successo
     * Verifica che il sistema permetta la creazione con tutti i dati validi e slot libero.
     * Input: Dati validi, slot libero.
     */
    @Test
    void testCreazioneCorretta() {
        // Setup
        Docente docente = creaDocente("prof.test5@unisa.it");
        Materia materia = recuperaOCreaMateria("Informatica");

        // Input Validi (Slot sicuramente libero in data molto futura)
        Attivita attivita = new Attivita();
        attivita.setTitolo("Esercitazione C");
        attivita.setDescrizione("Esercitazione sulla programmazione in C con strutture dati");
        attivita.setData(LocalDate.now().plusDays(20)); // Data futura valida
        attivita.setOraInizio(LocalTime.of(12, 0));
        attivita.setOraFine(LocalTime.of(14, 0));
        attivita.setPosti(30);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        // Esecuzione
        activitiesService.creaAttivita(attivita);

        // Oracolo: La creazione va a buon fine (Verifica persistenza)
        assertNotNull(attivita.getIdAttivita(), "L'ID dell'attività non dovrebbe essere null dopo il salvataggio");

        Attivita attivitaSalvata = attivitaRepository.findById(attivita.getIdAttivita()).orElseThrow();
        assertEquals("Esercitazione C", attivitaSalvata.getTitolo());
        assertEquals(docente.getEmail(), attivitaSalvata.getDocente().getEmail());
    }
}