package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.payments.PaymentsService;
import it.unisa.ilfarodellostudio.users.UsersService;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione delle attività (lezioni).
 * Contiene la logica di business per la creazione, modifica, recupero e gestione delle attività.
 */
@Service
public class ActivitiesService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private AttivitaRepository attivitaRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PaymentsService paymentsService;

    /* =================================================================
       METODO CREA: CON VALIDAZIONI COMPLETE (Per i Test JUnit)
       ================================================================= */
    /**
     * Crea una nuova attività con validazioni complete.
     * Effettua controlli sugli orari, date, sovrapposizioni e dati della materia.
     *
     * @param attivita l'oggetto attività da creare
     * @return l'oggetto attività salvato
     * @throws RuntimeException se le validazioni falliscono
     */
    @Transactional
    public Attivita creaAttivita(Attivita attivita) {

        // 1. Validazione Orari (Fine deve essere dopo Inizio)
        if (!attivita.getOraFine().isAfter(attivita.getOraInizio())) {
            throw new RuntimeException("Errore: L'orario di fine deve essere successivo all'inizio.");
        }

        // 2. Validazione Data Passata (TC_GA_1_2)
        if (LocalDateTime.of(attivita.getData(), attivita.getOraInizio()).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Errore: Non puoi creare lezioni nel passato.");
        }

        // 3. Validazione Lunghezza Descrizione (TC_GA_1_3)
        if (attivita.getDescrizione() != null && attivita.getDescrizione().length() > 300) {
            throw new RuntimeException("Errore: La descrizione supera i 300 caratteri.");
        }

        // 4. Validazione Nome Materia (TC_GA_1_1 - Nessun numero permesso)
        if (attivita.getMateria() != null && attivita.getMateria().getNome().matches(".*\\d.*")) {
            throw new RuntimeException("Errore: Il nome della materia non può contenere numeri.");
        }

        // 5. Validazione Sovrapposizione Oraria (TC_GA_1_4)
        boolean sovrapposizione = attivitaRepository.existsOverlappingLesson(
                attivita.getDocente(),
                attivita.getData(),
                attivita.getOraInizio(),
                attivita.getOraFine()
        );

        if (sovrapposizione) {
            throw new RuntimeException("Errore: Sovrapposizione con un'altra lezione.");
        }

        // 6. Gestione Materia (Salva se nuova, usa esistente se c'è)
        Materia m = attivita.getMateria();
        Materia materiaDb = materiaRepository.findByNome(m.getNome())
                .orElseGet(() -> materiaRepository.save(m));
        attivita.setMateria(materiaDb);

        // 7. Salvataggio finale
        return attivitaRepository.save(attivita);
    }

    /* =================================================================
       METOOO ISCRIZIONE STUDENTI AD ATTIVITA': USE CASE UC_GA_6 (CON TEST)
       ================================================================= */
    @Transactional
    public void iscriviStudenteAdAttivita(String emailStudente, Long idAttivita) {
        Studente studente = usersService.cercaStudente(emailStudente)
                .orElseThrow(() -> new RuntimeException("Studente non trovato"));

        Attivita attivita = visualizzaAttivita(idAttivita);

        // Controllo posti
        if (attivita.getIscritti().size() >= attivita.getPosti()) {
            throw new RuntimeException("Impossibile iscriversi: l'attività " + attivita.getTitolo() + " è già al completo");
        }

        // Controllo pagamenti
        paymentsService.verificaSituazioneDebitoria(studente.getFamiglia());

        attivita.aggiungiStudente(studente);
        attivitaRepository.save(attivita);
    }

    /* =================================================================
       ALTRI METODI DI GESTIONE
       ================================================================= */

    /**
     * Recupera tutte le attività di un docente a partire dalla data odierna.
     *
     * @param docente il docente di cui recuperare le attività
     * @return lista delle attività future del docente
     */
    public List<Attivita> dammiTutteLeAttivita(Docente docente) {
        return attivitaRepository.findAllByDocenteAndDataAfter(docente, LocalDate.now());
    }

    /**
     * Restituisce un'attività tramite il suo ID.
     *
     * @param id l'ID dell'attività
     * @return l'oggetto attività o null se non trovato
     */
    public Attivita visualizzaAttivita(Long id) {
        return attivitaRepository.findById(id).orElse(null);
    }

    public List<Attivita> visualizzaAttivitaDisponibili(String emailStudente) {
        return attivitaRepository.findDisponibiliUpcoming(
                emailStudente,
                LocalDate.now(),
                LocalTime.now()
        );
    }

    /**
     * Restituisce tutte le materie disponibili.
     *
     * @return lista delle materie
     */
    public List<Materia> getAllMaterie() {
        return materiaRepository.findAll();
    }

    /**
     * Modifica un'attività esistente.
     *
     * @param attivita l'oggetto attività modificato
     * @return l'attività aggiornata
     */
    public Attivita modificaAttivita(Attivita attivita) {
        return attivitaRepository.save(attivita);
    }

    /**
     * Elimina un'attività tramite ID.
     *
     * @param id l'ID dell'attività da eliminare
     */
    public void eliminaAttivita(Long id) {
        attivitaRepository.deleteById(id);
    }

    // Metodo helper (usato solo se serve controllo esterno)
    /**
     * Verifica l'esistenza di lezioni sovrapposte per un docente in una data e orario specifici.
     *
     * @param docente il docente
     * @param data la data
     * @param oraInizio ora di inizio
     * @param oraFine ora di fine
     * @return true se esiste una sovrapposizione, false altrimenti
     */
    public boolean existsOverlappingLesson(Docente docente, LocalDate data, LocalTime oraInizio, LocalTime oraFine) {
        return attivitaRepository.existsOverlappingLesson(docente, data, oraInizio, oraFine);
    }

    // Wrapper per modifica (controllo sovrapposizione escludendo ID corrente)
    /**
     * Verifica l'esistenza di lezioni sovrapposte escludendo un'attività specifica (per modifiche).
     *
     * @param docente il docente
     * @param data la data
     * @param oraInizio ora di inizio
     * @param oraFine ora di fine
     * @param id l'ID dell'attività da escludere dal controllo
     * @return true se esiste una sovrapposizione, false altrimenti
     */
    public boolean verificaSovrapposizioneEsclusoId(Docente docente, LocalDate data, LocalTime oraInizio, LocalTime oraFine, Long id) {
        // Assicurati che nel Repository esista il metodo 'existsOverlappingLessonExcludingId'
        // Se l'hai rimosso dal Repository, rimuovi anche questo metodo qui.
        return attivitaRepository.existsOverlappingLessonExcludingId(docente, data, oraInizio, oraFine, id);
    }

    /**
     * Cerca una materia tramite il nome.
     * @param nome Il nome della materia da cercare.
     * @return Un Optional contenente la materia se trovata.
     */
    public Optional<Materia> cercaMateriaPerNome(String nome) {
        return materiaRepository.findByNome(nome);
    }

    /**
     * Salva o aggiorna una materia nel database.
     * @param materia L'oggetto materia da salvare.
     * @return La materia salvata.
     */
    @Transactional
    public Materia salvaMateria(Materia materia) {
        return materiaRepository.save(materia);
    }
}