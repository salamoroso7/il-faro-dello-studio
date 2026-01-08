package it.unisa.ilfarodellostudio.gestione_attivita;

import it.unisa.ilfarodellostudio.gestione_attivita.dao.entity.Attivita;
import it.unisa.ilfarodellostudio.gestione_attivita.dao.entity.Materia;
import it.unisa.ilfarodellostudio.gestione_attivita.dao.AttivitaRepository;
import it.unisa.ilfarodellostudio.gestione_attivita.dao.MateriaRepository;
import it.unisa.ilfarodellostudio.gestione_attivita.service.ActivitiesService;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Docente;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.DocenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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


    private Attivita creaAttivitaBase(Docente docente, Materia materia) {
        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Test");
        attivita.setDescrizione("Descrizione standard");
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);
        return attivita;
    }


    private void salvaAttivitaEsistente(Docente d, Materia m, LocalDate data, LocalTime start, LocalTime end) {
        Attivita a = new Attivita();
        a.setTitolo("Existing");
        a.setDescrizione("Desc");
        a.setData(data);
        a.setOraInizio(start);
        a.setOraFine(end);
        a.setMateria(m);
        a.setDocente(d);
        a.setPosti(20);
        activitiesService.creaAttivita(a);
    }

    private Attivita creaAttivitaInput(Docente d, Materia m, LocalDate data, LocalTime start, LocalTime end) {
        Attivita a = new Attivita();
        a.setTitolo("New Overlapping");
        a.setDescrizione("Desc");
        a.setData(data);
        a.setOraInizio(start);
        a.setOraFine(end);
        a.setMateria(m);
        a.setDocente(d);
        a.setPosti(20);
        return a;
    }

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
    /**
     * Variante 1: Numero alla fine
     * Input: "Informatica1"
     */
    @Test
    void testMateriaNumeroFinale() {
        Docente docente = creaDocente("prof.v1@test.it");
        Materia materia = new Materia();
        materia.setNome("Informatica1");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 2: Numero all'inizio
     * Input: "1Informatica"
     */
    @Test
    void testMateriaNumeroIniziale() {
        Docente docente = creaDocente("prof.v2@test.it");
        Materia materia = new Materia();
        materia.setNome("1Informatica");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 3: Numero nel mezzo
     * Input: "Info1rmatica"
     */
    @Test
    void testMateriaNumeroNelMezzo() {
        Docente docente = creaDocente("prof.v3@test.it");
        Materia materia = new Materia();
        materia.setNome("Info1rmatica");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 4: Solo numeri
     * Input: "123456"
     */
    @Test
    void testMateriaSoloNumeri() {
        Docente docente = creaDocente("prof.v4@test.it");
        Materia materia = new Materia();
        materia.setNome("123456");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 5: Numero con spazio (Errore comune: "Fisica 2")
     * Input: "Fisica 2"
     */
    @Test
    void testMateriaConSpazioENumero() {
        Docente docente = creaDocente("prof.v5@test.it");
        Materia materia = new Materia();
        materia.setNome("Fisica 2");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 6: Codice corso alfanumerico
     * Input: "CS101"
     */
    @Test
    void testMateriaCodiceCorso() {
        Docente docente = creaDocente("prof.v6@test.it");
        Materia materia = new Materia();
        materia.setNome("CS101");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 7: Numero Zero specifico
     * Input: "Matematica0"
     */
    @Test
    void testMateriaConZero() {
        Docente docente = creaDocente("prof.v7@test.it");
        Materia materia = new Materia();
        materia.setNome("Matematica0");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 8: Caratteri speciali misti a numeri
     * Input: "Storia_900"
     */
    @Test
    void testMateriaSpecialiENumeri() {
        Docente docente = creaDocente("prof.v8@test.it");
        Materia materia = new Materia();
        materia.setNome("Storia_900");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 9: Multipli numeri sparsi
     * Input: "3G30gr4f1a"
     */
    @Test
    void testMateriaNumeriSparsi() {
        Docente docente = creaDocente("prof.v9@test.it");
        Materia materia = new Materia();
        materia.setNome("3G30gr4f1a");

        Attivita attivita = creaAttivitaBase(docente, materia);
        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * TC_GA_1_2 - Data nel passato
     * Verifica che il sistema rifiuti la creazione se la data è antecedente a oggi.
     * /**
     * Variante 1: Ieri (Passato recente)
     * Input: LocalDate.now().minusDays(1)
     */
    @Test
    void testCreazioneDataIeri() {
        Docente docente = creaDocente("prof.ieri@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Ieri");
        attivita.setDescrizione("Descrizione");
        attivita.setData(LocalDate.now().minusDays(1)); // Ieri
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 2: Una Settimana fa
     * Input: LocalDate.now().minusWeeks(1)
     */
    @Test
    void testCreazioneDataSettimanaScorsa() {
        Docente docente = creaDocente("prof.week@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Settimana Fa");
        attivita.setDescrizione("Descrizione");
        attivita.setData(LocalDate.now().minusWeeks(1));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 3: Un Mese fa
     * Input: LocalDate.now().minusMonths(1)
     */
    @Test
    void testCreazioneDataMeseScorso() {
        Docente docente = creaDocente("prof.month@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Mese Fa");
        attivita.setDescrizione("Descrizione");
        attivita.setData(LocalDate.now().minusMonths(1));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 4: Un Anno fa
     * Input: LocalDate.now().minusYears(1)
     */
    @Test
    void testCreazioneDataAnnoScorso() {
        Docente docente = creaDocente("prof.year@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Anno Fa");
        attivita.setDescrizione("Descrizione");
        attivita.setData(LocalDate.now().minusYears(1));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 5: Data storica (Lontano passato)
     * Input: 01/01/2000
     */
    @Test
    void testCreazioneDataMillennioScorso() {
        Docente docente = creaDocente("prof.2000@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione 2000");
        attivita.setDescrizione("Descrizione");
        attivita.setData(LocalDate.of(2000, 1, 1));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 6: Oggi, ma un'ora fa (Boundary Case)
     * Input: Data = Oggi, Ora Inizio = Adesso - 1 ora
     */
    @Test
    void testCreazioneOggiMaOraPassata() {
        Docente docente = creaDocente("prof.hour@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        LocalDateTime unOraFa = LocalDateTime.now().minusHours(1);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Scaduta da poco");
        attivita.setDescrizione("Descrizione");
        attivita.setData(unOraFa.toLocalDate());
        attivita.setOraInizio(unOraFa.toLocalTime());
        // L'ora fine è irrilevante per questo check, basta che sia dopo l'inizio
        attivita.setOraFine(unOraFa.toLocalTime().plusHours(2));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 7: Oggi, ma 5 minuti fa (Tight Boundary)
     * Input: Data = Oggi, Ora Inizio = Adesso - 5 minuti
     */
    @Test
    void testCreazioneOggiMaMinutiPassati() {
        Docente docente = creaDocente("prof.min@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        LocalDateTime cinqueMinutiFa = LocalDateTime.now().minusMinutes(5);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Appena Scaduta");
        attivita.setDescrizione("Descrizione");
        attivita.setData(cinqueMinutiFa.toLocalDate());
        attivita.setOraInizio(cinqueMinutiFa.toLocalTime());
        attivita.setOraFine(cinqueMinutiFa.toLocalTime().plusHours(2));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 8: Ultimo giorno dell'anno scorso
     * Input: 31 Dicembre anno scorso
     */
    @Test
    void testCreazioneFineAnnoScorso() {
        Docente docente = creaDocente("prof.lastyear@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        // Calcola 31 Dicembre dell'anno precedente
        LocalDate fineAnnoScorso = LocalDate.now().minusYears(1).withMonth(12).withDayOfMonth(31);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Capodanno Scorso");
        attivita.setDescrizione("Descrizione");
        attivita.setData(fineAnnoScorso);
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * Variante 9: Data bisestile passata (Edge case)
     * Input: 29 Febbraio 2024 (o data fissa sicuramente passata)
     */
    @Test
    void testCreazioneBisestilePassato() {
        Docente docente = creaDocente("prof.leap@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        // Usiamo una data fissa passata nota (es. 29 Feb 2020 o 2024)
        // Se siamo nel 2026, il 2024 è passato.
        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Bisestile");
        attivita.setDescrizione("Descrizione");
        attivita.setData(LocalDate.of(2024, 2, 29));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Exception exception = assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
        assertTrue(exception.getMessage().toLowerCase().contains("passato"));
    }

    /**
     * TC_GA_1_3 - Descrizione troppo lunga
     * Verifica che il sistema rifiuti descrizioni superiori a 300 caratteri.
     * /**
     * Variante 1: Limite superato di 1 solo carattere (301 caratteri)
     * Input: "a" ripetuto 301 volte
     */
    @Test
    void testDescrizione301Caratteri() {
        Docente docente = creaDocente("prof.desc1@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");

        // Java 11+: "a".repeat(301)
        String desc301 = "a".repeat(301);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Boundary");
        attivita.setDescrizione(desc301);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 2: Descrizione composta solo da numeri (350 caratteri)
     * Input: "0123456789..." ripetuto
     */
    @Test
    void testDescrizioneNumericaLunga() {
        Docente docente = creaDocente("prof.desc2@test.it");
        Materia materia = recuperaOCreaMateria("Matematica");

        // Genera 350 numeri
        String descNumeri = "1234567890".repeat(35);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Numeri");
        attivita.setDescrizione(descNumeri);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 3: Descrizione composta solo da spazi vuoti (301 spazi)
     * Input: " " ripetuto 301 volte
     */
    @Test
    void testDescrizioneSoloSpaziLunga() {
        Docente docente = creaDocente("prof.desc3@test.it");
        Materia materia = recuperaOCreaMateria("Vuoto");

        String descSpazi = " ".repeat(301);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Spazi");
        attivita.setDescrizione(descSpazi);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 4: Descrizione con caratteri speciali (305 caratteri)
     * Input: "!!!!...."
     */
    @Test
    void testDescrizioneCaratteriSpeciali() {
        Docente docente = creaDocente("prof.desc4@test.it");
        Materia materia = recuperaOCreaMateria("Simboli");

        String descSimboli = "!".repeat(305);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Simboli");
        attivita.setDescrizione(descSimboli);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 5: Lorem Ipsum molto lungo (500 caratteri)
     * Input: Testo latino fittizio
     */
    @Test
    void testDescrizioneLoremIpsum() {
        Docente docente = creaDocente("prof.desc5@test.it");
        Materia materia = recuperaOCreaMateria("Latino");

        String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ".repeat(10);
        // Sarà sicuramente > 300

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Lorem");
        attivita.setDescrizione(lorem);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 6: Descrizione esattamente doppia del limite (600 caratteri)
     * Input: "a" ripetuto 600 volte
     */
    @Test
    void testDescrizioneDoppioLimite() {
        Docente docente = creaDocente("prof.desc6@test.it");
        Materia materia = recuperaOCreaMateria("Doppio");

        String desc600 = "b".repeat(600);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test 600 chars");
        attivita.setDescrizione(desc600);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 7: Descrizione multiline (301 righe con \n)
     * Input: "\n" ripetuto 301 volte
     */
    @Test
    void testDescrizioneSoloNewLines() {
        Docente docente = creaDocente("prof.desc7@test.it");
        Materia materia = recuperaOCreaMateria("Acapo");

        String descNewLines = "\n".repeat(301);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test NewLines");
        attivita.setDescrizione(descNewLines);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 8: Descrizione composta da frasi ripetute
     * Input: Frase "Questa descrizione non finisce mai. " ripetuta
     */
    @Test
    void testDescrizioneFrasiRipetute() {
        Docente docente = creaDocente("prof.desc8@test.it");
        Materia materia = recuperaOCreaMateria("Ripetizione");

        String frase = "Questa descrizione è decisamente troppo lunga per essere accettata dal sistema. ";
        String descLunga = frase.repeat(10); // Circa 700 chars

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Frasi");
        attivita.setDescrizione(descLunga);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 9: Descrizione mista (lettere + numeri + simboli) > 300
     * Input: Stringa complessa lunga
     */
    @Test
    void testDescrizioneMistaLunga() {
        Docente docente = creaDocente("prof.desc9@test.it");
        Materia materia = recuperaOCreaMateria("Misto");

        String base = "Abc_123_!";
        String descMista = base.repeat(40); // 9 char * 40 = 360 chars

        Attivita attivita = new Attivita();
        attivita.setTitolo("Test Misto");
        attivita.setDescrizione(descMista);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(attivita));
    }

    /**
     * TC_GA_1_4 - Sovrapposizione Oraria
     * Verifica che il sistema rifiuti la creazione se il docente è già impegnato in quell'orario.
     * Pre-condizione: Esiste già una lezione dalle 10:00 alle 12:00.
     * /**
     * Variante 1: Sovrapposizione ESATTA
     * Esistente: 10:00 - 12:00
     * Nuova:     10:00 - 12:00
     */
    @Test
    void testSovrapposizioneEsatta() {
        Docente docente = creaDocente("prof.sovr1@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Stessi orari
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 2: Sovrapposizione INTERNA (Nested)
     * Esistente: 10:00 - 12:00
     * Nuova:     10:30 - 11:30 (Completamente dentro)
     */
    @Test
    void testSovrapposizioneInterna() {
        Docente docente = creaDocente("prof.sovr2@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Inizia dopo l'inizio, finisce prima della fine
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(10, 30), LocalTime.of(11, 30));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 3: Sovrapposizione ESTERNA (Enclosing)
     * Esistente: 10:00 - 12:00
     * Nuova:     09:00 - 13:00 (Ingloba quella esistente)
     */
    @Test
    void testSovrapposizioneEsterna() {
        Docente docente = creaDocente("prof.sovr3@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Inizia prima e finisce dopo
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(9, 0), LocalTime.of(13, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 4: Sovrapposizione PARZIALE SINISTRA (Coda)
     * Esistente: 10:00 - 12:00
     * Nuova:     09:00 - 10:30 (Finisce dentro l'esistente)
     */
    @Test
    void testSovrapposizioneParzialeSinistra() {
        Docente docente = creaDocente("prof.sovr4@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Inizia prima (09:00) ma finisce dopo l'inizio della vecchia (10:30)
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(9, 0), LocalTime.of(10, 30));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 5: Sovrapposizione PARZIALE DESTRA (Testa)
     * Esistente: 10:00 - 12:00
     * Nuova:     11:30 - 13:00 (Inizia dentro l'esistente)
     */
    @Test
    void testSovrapposizioneParzialeDestra() {
        Docente docente = creaDocente("prof.sovr5@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Inizia dentro (11:30) e finisce dopo (13:00)
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(11, 30), LocalTime.of(13, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 6: Stesso INIZIO, durata diversa
     * Esistente: 10:00 - 12:00
     * Nuova:     10:00 - 11:00
     */
    @Test
    void testSovrapposizioneStessoInizio() {
        Docente docente = creaDocente("prof.sovr6@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Coincide l'inizio
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 7: Stessa FINE, durata diversa
     * Esistente: 10:00 - 12:00
     * Nuova:     11:00 - 12:00
     */
    @Test
    void testSovrapposizioneStessaFine() {
        Docente docente = creaDocente("prof.sovr7@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Coincide la fine
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(11, 0), LocalTime.of(12, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 8: Sovrapposizione "Marginale" (1 minuto)
     * Esistente: 10:00 - 12:00
     * Nuova:     11:59 - 13:00 (Si sovrappone per 1 solo minuto)
     */
    @Test
    void testSovrapposizioneMarginaleMinuto() {
        Docente docente = creaDocente("prof.sovr8@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Tentativo: Inizia un minuto prima della fine dell'altra
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(11, 59), LocalTime.of(13, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * Variante 9: Sovrapposizione con MULTIPLE lezioni
     * Esistente 1: 09:00 - 11:00
     * Esistente 2: 14:00 - 16:00
     * Nuova:       10:00 - 15:00 (Tocca entrambe)
     */
    @Test
    void testSovrapposizioneMultipla() {
        Docente docente = creaDocente("prof.sovr9@test.it");
        Materia materia = recuperaOCreaMateria("Informatica");
        LocalDate data = LocalDate.now().plusDays(10);

        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(9, 0), LocalTime.of(11, 0));
        salvaAttivitaEsistente(docente, materia, data, LocalTime.of(14, 0), LocalTime.of(16, 0));

        // Tentativo: Una mega lezione che si sovrappone alla prima E alla seconda
        Attivita nuova = creaAttivitaInput(docente, materia, data, LocalTime.of(10, 0), LocalTime.of(15, 0));

        assertThrows(RuntimeException.class, () -> activitiesService.creaAttivita(nuova));
    }

    /**
     * TC_GA_1_5 - Creazione con Successo
     * Verifica che il sistema permetta la creazione con tutti i dati validi e slot libero.
     * /**
     * Variante 1: Descrizione al Limite Massimo (300 caratteri)
     * Verifica che il sistema accetti esattamente il limite consentito.
     */
    @Test
    void testCreazioneSuccessoDescrizioneMassima() {
        Docente docente = creaDocente("prof.ok1@test.it");
        Materia materia = recuperaOCreaMateria("Analisi");

        String descrizione300 = "a".repeat(300); // 300 caratteri esatti

        Attivita attivita = new Attivita();
        attivita.setTitolo("Analisi Limite");
        attivita.setDescrizione(descrizione300);
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(50);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Attivita salvata = activitiesService.creaAttivita(attivita);
        assertNotNull(salvata.getIdAttivita());
        assertEquals(300, salvata.getDescrizione().length());
    }

    /**
     * Variante 2: Descrizione Minima (1 carattere)
     * Verifica che una descrizione molto breve sia valida.
     */
    @Test
    void testCreazioneSuccessoDescrizioneMinima() {
        Docente docente = creaDocente("prof.ok2@test.it");
        Materia materia = recuperaOCreaMateria("Fisica");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Fisica Rapida");
        attivita.setDescrizione("X"); // Minimo sindacale
        attivita.setData(LocalDate.now().plusDays(5));
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(50);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Attivita salvata = activitiesService.creaAttivita(attivita);
        assertNotNull(salvata.getIdAttivita());
    }

    /**
     * Variante 3: Data Domani (Limite inferiore futuro)
     * Verifica la creazione per la data valida più vicina possibile.
     */
    @Test
    void testCreazioneSuccessoDomani() {
        Docente docente = creaDocente("prof.ok3@test.it");
        Materia materia = recuperaOCreaMateria("Chimica");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Domani");
        attivita.setDescrizione("Urgente");
        attivita.setData(LocalDate.now().plusDays(1)); // DOMANI
        attivita.setOraInizio(LocalTime.of(9, 0));
        attivita.setOraFine(LocalTime.of(11, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        assertDoesNotThrow(() -> activitiesService.creaAttivita(attivita));
    }

    /**
     * Variante 4: Data Lontana (Anno prossimo)
     * Verifica la creazione per una data molto futura.
     */
    @Test
    void testCreazioneSuccessoAnnoProssimo() {
        Docente docente = creaDocente("prof.ok4@test.it");
        Materia materia = recuperaOCreaMateria("Storia");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Lezione Futura");
        attivita.setDescrizione("Pianificazione");
        attivita.setData(LocalDate.now().plusYears(1)); // Tra un anno
        attivita.setOraInizio(LocalTime.of(10, 0));
        attivita.setOraFine(LocalTime.of(12, 0));
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Attivita salvata = activitiesService.creaAttivita(attivita);
        assertNotNull(salvata.getIdAttivita());
    }

    /**
     * Variante 5: Orario Mattutino Presto
     * Verifica la creazione all'inizio della giornata lavorativa.
     */
    @Test
    void testCreazioneSuccessoMattinaPresto() {
        Docente docente = creaDocente("prof.ok5@test.it");
        Materia materia = recuperaOCreaMateria("Inglese");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Morning Class");
        attivita.setDescrizione("Early bird");
        attivita.setData(LocalDate.now().plusDays(10));
        attivita.setOraInizio(LocalTime.of(8, 0)); // 08:00
        attivita.setOraFine(LocalTime.of(10, 0)); // 10:00
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Attivita salvata = activitiesService.creaAttivita(attivita);
        assertEquals(LocalTime.of(8, 0), salvata.getOraInizio());
    }

    /**
     * Variante 6: Orario Serale Tardi
     * Verifica la creazione alla fine della giornata lavorativa.
     */
    @Test
    void testCreazioneSuccessoSeraTardi() {
        Docente docente = creaDocente("prof.ok6@test.it");
        Materia materia = recuperaOCreaMateria("Inglese");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Evening Class");
        attivita.setDescrizione("Late lecture");
        attivita.setData(LocalDate.now().plusDays(10));
        attivita.setOraInizio(LocalTime.of(18, 0)); // 18:00
        attivita.setOraFine(LocalTime.of(20, 0));  // 20:00
        attivita.setPosti(20);
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Attivita salvata = activitiesService.creaAttivita(attivita);
        assertEquals(LocalTime.of(18, 0), salvata.getOraInizio());
    }

    /**
     * Variante 7: Lezioni CONSECUTIVE (Caso Adiacenza - "Dopo")
     * Verifica che si possa creare una lezione che inizia ESATTAMENTE quando ne finisce un'altra.
     * Lezione A: 10:00-12:00 -> Lezione B: 12:00-14:00 (Deve passare)
     */
    @Test
    void testCreazioneSuccessoAdiacenteDopo() {
        Docente docente = creaDocente("prof.ok7@test.it");
        Materia materia = recuperaOCreaMateria("Logica");
        LocalDate data = LocalDate.now().plusDays(15);

        // Creiamo la prima lezione (10-12)
        Attivita a1 = new Attivita();
        a1.setTitolo("Lezione A");
        a1.setData(data);
        a1.setOraInizio(LocalTime.of(10, 0));
        a1.setOraFine(LocalTime.of(12, 0));
        a1.setMateria(materia);
        a1.setDocente(docente);
        a1.setPosti(20);
        activitiesService.creaAttivita(a1);

        // Creiamo la seconda che inizia alle 12 (Touch point)
        Attivita a2 = new Attivita();
        a2.setTitolo("Lezione B");
        a2.setDescrizione("Segue la prima");
        a2.setData(data);
        a2.setOraInizio(LocalTime.of(12, 0)); // 12:00 (Non deve dare conflitto)
        a2.setOraFine(LocalTime.of(14, 0));
        a2.setMateria(materia);
        a2.setDocente(docente);
        a2.setPosti(20);

        Attivita salvata = activitiesService.creaAttivita(a2);
        assertNotNull(salvata.getIdAttivita(), "Le lezioni consecutive devono essere permesse");
    }

    /**
     * Variante 8: Lezioni CONSECUTIVE (Caso Adiacenza - "Prima")
     * Verifica che si possa creare una lezione che finisce ESATTAMENTE quando ne inizia un'altra già presente.
     * Esistente: 16:00-18:00 -> Nuova: 14:00-16:00 (Deve passare)
     */
    @Test
    void testCreazioneSuccessoAdiacentePrima() {
        Docente docente = creaDocente("prof.ok8@test.it");
        Materia materia = recuperaOCreaMateria("Logica");
        LocalDate data = LocalDate.now().plusDays(16);

        // Esistente nel pomeriggio tardi
        Attivita a1 = new Attivita();
        a1.setTitolo("Lezione Pomeriggio");
        a1.setData(data);
        a1.setOraInizio(LocalTime.of(16, 0));
        a1.setOraFine(LocalTime.of(18, 0));
        a1.setMateria(materia);
        a1.setDocente(docente);
        a1.setPosti(20);
        activitiesService.creaAttivita(a1);

        // Nuova che finisce alle 16:00
        Attivita a2 = new Attivita();
        a2.setTitolo("Lezione Primo Pomeriggio");
        a2.setDescrizione("Precede la prima");
        a2.setData(data);
        a2.setOraInizio(LocalTime.of(14, 0));
        a2.setOraFine(LocalTime.of(16, 0)); // 16:00
        a2.setMateria(materia);
        a2.setDocente(docente);
        a2.setPosti(20);

        Attivita salvata = activitiesService.creaAttivita(a2);
        assertNotNull(salvata.getIdAttivita());
    }

    /**
     * Variante 9: Creazione con Materia Mai Usata Prima
     * Verifica che il sistema gestisca correttamente nuove materie valide.
     */
    @Test
    void testCreazioneSuccessoNuovaMateria() {
        Docente docente = creaDocente("prof.ok9@test.it");
        // Creiamo una materia ex novo, non recuperata dal DB
        Materia materia = new Materia();
        materia.setNome("Machine Learning");

        Attivita attivita = new Attivita();
        attivita.setTitolo("Intro ML");
        attivita.setDescrizione("Intelligenza Artificiale");
        attivita.setData(LocalDate.now().plusDays(20));
        attivita.setOraInizio(LocalTime.of(11, 0));
        attivita.setOraFine(LocalTime.of(13, 0));
        attivita.setPosti(100); // Tanti posti
        attivita.setMateria(materia);
        attivita.setDocente(docente);

        Attivita salvata = activitiesService.creaAttivita(attivita);

        assertNotNull(salvata.getIdAttivita());
        assertEquals("Machine Learning", salvata.getMateria().getNome());
    }
}
