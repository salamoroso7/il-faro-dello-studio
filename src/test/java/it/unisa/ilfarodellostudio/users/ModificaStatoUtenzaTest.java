package it.unisa.ilfarodellostudio.users;

import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la modifica dello stato delle utenze (Attivo/Disattivato).
 * Copre i test case TC_GU_10_1 e TC_GU_10_2.
 */
@SpringBootTest
@Transactional
public class ModificaStatoUtenzaTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private FamigliaRepository famigliaRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    // --- METODI HELPER ---

    private Docente creaDocente(String email, boolean attivo) {
        Docente d = new Docente();
        d.setEmail(email);
        d.setNome("Mario");
        d.setCognome("Rossi");
        d.setPassword("password123");
        d.setAttivo(attivo);
        return docenteRepository.save(d);
    }

    private Famiglia creaFamiglia(String email, boolean attivo) {
        Famiglia f = new Famiglia();
        f.setEmail(email);
        f.setNome("Giovanni");
        f.setCognome("Bianchi");
        f.setPassword("password123");
        f.setAttivo(attivo);
        return famigliaRepository.save(f);
    }

    private Studente creaStudente(String email, boolean attivo, Famiglia famiglia) {
        Studente s = new Studente();
        s.setEmail(email);
        s.setNome("Luca");
        s.setCognome("Verdi");
        s.setPassword("password123");
        
        // Generiamo un CF di esattamente 16 caratteri
        String timestamp = String.valueOf(System.currentTimeMillis());
        s.setCodiceFiscale(("CF" + timestamp + "AAAAAAAAAAAAAAA").substring(0, 16));
        
        s.setDataNascita(LocalDate.of(2010, 5, 15));
        s.setAttivo(attivo);
        s.setFamiglia(famiglia);
        return studenteRepository.save(s);
    }

    // --- TEST CASES ---

    /**
     * TC_GU_10_1.1 - Test con email null
     * Verifica che il sistema rifiuti la modifica quando l'email è null.
     */
    @Test
    void testAttivazioneEmailNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.attivaUtente(null);
        });

        assertTrue(exception.getMessage().contains("Utente non trovato"),
                "Il messaggio di errore dovrebbe indicare che l'utente non è stato trovato");
    }

    /**
     * TC_GU_10_1.2 - Test con email vuota
     * Verifica che il sistema rifiuti la modifica quando l'email è vuota.
     */
    @Test
    void testAttivazioneEmailVuota() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.attivaUtente("");
        });

        assertTrue(exception.getMessage().contains("Utente non trovato"),
                "Il messaggio di errore dovrebbe indicare che l'utente non è stato trovato");
    }

    /**
     * TC_GU_10_1.3 - Test con email inesistente
     * Verifica che il sistema rifiuti la modifica quando l'email non esiste nel database.
     */
    @Test
    void testAttivazioneEmailInesistente() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.attivaUtente("utente.inesistente@test.com");
        });

        assertEquals("Utente non trovato con email: utente.inesistente@test.com", 
                exception.getMessage(),
                "Il messaggio di errore dovrebbe contenere l'email cercata");
    }

    /**
     * TC_GU_10_1.4 - Test disattivazione con email null
     * Verifica che il sistema rifiuti la disattivazione quando l'email è null.
     */
    @Test
    void testDisattivazioneEmailNull() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.disattivaUtente(null);
        });

        assertTrue(exception.getMessage().contains("Utente non trovato"),
                "Il messaggio di errore dovrebbe indicare che l'utente non è stato trovato");
    }

    /**
     * TC_GU_10_1.5 - Test disattivazione con email inesistente
     * Verifica che il sistema rifiuti la disattivazione quando l'email non esiste.
     */
    @Test
    void testDisattivazioneEmailInesistente() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.disattivaUtente("altro.inesistente@test.com");
        });

        assertEquals("Utente non trovato con email: altro.inesistente@test.com",
                exception.getMessage(),
                "Il messaggio di errore dovrebbe contenere l'email cercata");
    }

    /**
     * TC_GU_10_2 - Tentativo di cambio stato con valore non valido.
     *
     * Questo test case NON è eseguibile nel contesto attuale per motivi architetturali:
     *
     * 1. Livello Service: La logica di business è separata in due metodi distinti e specifici:
     *    - {@link UsersService#attivaUtente(String)} imposta esplicitamente lo stato a 'true'.
     *    - {@link UsersService#disattivaUtente(String)} imposta esplicitamente lo stato a 'false'.
     *    Non esiste un singolo metodo che accetta uno stato arbitrario come parametro, rendendo impossibile
     *    il passaggio di un valore non booleano o non valido a questo livello.
     *
     * 2. Livello Controller: Anche l'esposizione web riflette questa separazione con due endpoint distinti:
     *    - POST /admin/utenti/attiva (gestito da {@link UsersController#attivaUtente})
     *    - POST /admin/utenti/disattiva (gestito da {@link UsersController#disattivaUtente})
     *    Il controller non riceve lo stato come parametro di input dall'utente, ma deduce l'azione
     *    dall'endpoint chiamato.
     *
     * Di conseguenza, la logica di validazione dello "stato non valido" è implicitamente gestita
     * dall'assenza di un punto di ingresso che permetta tale scenario.
     */


    /**
     * TC_GU_10_3.1 - Test attivazione Docente
     * Verifica che un docente disattivato possa essere attivato correttamente.
     */
    @Test
    void testAttivazioneDocenteCorretta() {
        // Setup: crea docente disattivato
        Docente docente = creaDocente("alfredo.marraffa@test.com", false);
        assertFalse(docente.isAttivo(), "Il docente dovrebbe essere disattivato inizialmente");

        // Esecuzione: attiva il docente
        usersService.attivaUtente(docente.getEmail());

        // Verifica: controlla che sia stato attivato
        Docente docenteAggiornato = docenteRepository.findByEmail(docente.getEmail()).orElseThrow();
        assertTrue(docenteAggiornato.isAttivo(), 
                "Il docente dovrebbe essere attivo dopo l'attivazione");
    }

    /**
     * TC_GU_10_3.2 - Test disattivazione Docente
     * Verifica che un docente attivo possa essere disattivato correttamente.
     */
    @Test
    void testDisattivazioneDocenteCorretta() {
        // Setup: crea docente attivo
        Docente docente = creaDocente("angelo.deluca@test.com", true);
        assertTrue(docente.isAttivo(), "Il docente dovrebbe essere attivo inizialmente");

        // Esecuzione: disattiva il docente
        usersService.disattivaUtente(docente.getEmail());

        // Verifica: controlla che sia stato disattivato
        Docente docenteAggiornato = docenteRepository.findByEmail(docente.getEmail()).orElseThrow();
        assertFalse(docenteAggiornato.isAttivo(),
                "Il docente dovrebbe essere disattivato dopo la disattivazione");
    }

    /**
     * TC_GU_10_3.3 - Test attivazione Famiglia
     * Verifica che una famiglia disattivata possa essere attivata correttamente.
     */
    @Test
    void testAttivazioneFamigliaCorretta() {
        // Setup: crea famiglia disattivata
        Famiglia famiglia = creaFamiglia("famiglia.test@test.com", false);
        assertFalse(famiglia.isAttivo(), "La famiglia dovrebbe essere disattivata inizialmente");

        // Esecuzione: attiva la famiglia
        usersService.attivaUtente(famiglia.getEmail());

        // Verifica: controlla che sia stata attivata
        Famiglia famigliaAggiornata = famigliaRepository.findByEmail(famiglia.getEmail()).orElseThrow();
        assertTrue(famigliaAggiornata.isAttivo(),
                "La famiglia dovrebbe essere attiva dopo l'attivazione");
    }

    /**
     * TC_GU_10_3.4 - Test disattivazione Famiglia
     * Verifica che una famiglia attiva possa essere disattivata correttamente.
     */
    @Test
    void testDisattivazioneFamigliaCorretta() {
        // Setup: crea famiglia attiva
        Famiglia famiglia = creaFamiglia("famiglia.attiva@test.com", true);
        assertTrue(famiglia.isAttivo(), "La famiglia dovrebbe essere attiva inizialmente");

        // Esecuzione: disattiva la famiglia
        usersService.disattivaUtente(famiglia.getEmail());

        // Verifica: controlla che sia stata disattivata
        Famiglia famigliaAggiornata = famigliaRepository.findByEmail(famiglia.getEmail()).orElseThrow();
        assertFalse(famigliaAggiornata.isAttivo(),
                "La famiglia dovrebbe essere disattivata dopo la disattivazione");
    }

    /**
     * TC_GU_10_3.5 - Test attivazione Studente
     * Verifica che uno studente disattivato possa essere attivato correttamente.
     */
    @Test
    void testAttivazioneStudenteCorretta() {
        // Setup: crea famiglia e studente disattivato
        Famiglia famiglia = creaFamiglia("famiglia.studente@test.com", true);
        Studente studente = creaStudente("studente.test@test.com", false, famiglia);
        assertFalse(studente.isAttivo(), "Lo studente dovrebbe essere disattivato inizialmente");

        // Esecuzione: attiva lo studente
        usersService.attivaUtente(studente.getEmail());

        // Verifica: controlla che sia stato attivato
        Studente studenteAggiornato = studenteRepository.findByEmail(studente.getEmail()).orElseThrow();
        assertTrue(studenteAggiornato.isAttivo(),
                "Lo studente dovrebbe essere attivo dopo l'attivazione");
    }

    /**
     * TC_GU_10_3.6 - Test disattivazione Studente
     * Verifica che uno studente attivo possa essere disattivato correttamente.
     */
    @Test
    void testDisattivazioneStudenteCorretta() {
        // Setup: crea famiglia e studente attivo
        Famiglia famiglia = creaFamiglia("famiglia.studente2@test.com", true);
        Studente studente = creaStudente("studente.attivo@test.com", true, famiglia);
        assertTrue(studente.isAttivo(), "Lo studente dovrebbe essere attivo inizialmente");

        // Esecuzione: disattiva lo studente
        usersService.disattivaUtente(studente.getEmail());

        // Verifica: controlla che sia stato disattivato
        Studente studenteAggiornato = studenteRepository.findByEmail(studente.getEmail()).orElseThrow();
        assertFalse(studenteAggiornato.isAttivo(),
                "Lo studente dovrebbe essere disattivato dopo la disattivazione");
    }

    /**
     * Test aggiuntivo - Verifica persistenza dopo attivazione multipla
     * Controlla che lo stato rimanga persistente anche dopo più modifiche.
     */
    @Test
    void testPersistenzaStatoDopoModificheMultiple() {
        // Setup: crea docente attivo
        Docente docente = creaDocente("docente.multiplo@test.com", true);

        // Disattiva
        usersService.disattivaUtente(docente.getEmail());
        Docente step1 = docenteRepository.findByEmail(docente.getEmail()).orElseThrow();
        assertFalse(step1.isAttivo(), "Dovrebbe essere disattivato dopo la prima modifica");

        // Riattiva
        usersService.attivaUtente(docente.getEmail());
        Docente step2 = docenteRepository.findByEmail(docente.getEmail()).orElseThrow();
        assertTrue(step2.isAttivo(), "Dovrebbe essere attivo dopo la seconda modifica");

        // Disattiva di nuovo
        usersService.disattivaUtente(docente.getEmail());
        Docente step3 = docenteRepository.findByEmail(docente.getEmail()).orElseThrow();
        assertFalse(step3.isAttivo(), "Dovrebbe essere disattivato dopo la terza modifica");
    }
}
