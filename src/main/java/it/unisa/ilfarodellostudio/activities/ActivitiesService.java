package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.payments.entity.StatoPagamento;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivitiesService {

    @Autowired
    private AttivitaRepository attivitaRepository;
    @Autowired
    private StudenteRepository studenteRepository;
    @Autowired
    private MateriaRepository materiaRepository;

    /* =================================================================
       METODO CREA: CON VALIDAZIONI COMPLETE (Per i Test JUnit)
       ================================================================= */
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
       ALTRI METODI DI GESTIONE
       ================================================================= */

    public List<Attivita> dammiTutteLeAttivita(Docente docente) {
        return attivitaRepository.findAllByDocenteAndDataAfter(docente, LocalDate.now());
    }

    public Attivita visualizzaAttivita(Long id) {
        return attivitaRepository.findById(id).orElse(null);
    }

    public List<Attivita> visualizzaTutteLeAttivita() {
        return attivitaRepository.findAll();
    }

    public List<Materia> getAllMaterie() {
        return materiaRepository.findAll();
    }

    public Attivita modificaAttivita(Attivita attivita) {
        return attivitaRepository.save(attivita);
    }

    public void eliminaAttivita(Long id) {
        attivitaRepository.deleteById(id);
    }

    // Metodo helper (usato solo se serve controllo esterno)
    public boolean existsOverlappingLesson(Docente docente, LocalDate data, LocalTime oraInizio, LocalTime oraFine) {
        return attivitaRepository.existsOverlappingLesson(docente, data, oraInizio, oraFine);
    }

    // Wrapper per modifica (controllo sovrapposizione escludendo ID corrente)
    public boolean verificaSovrapposizioneEsclusoId(Docente docente, LocalDate data, LocalTime oraInizio, LocalTime oraFine, Long id) {
        // Assicurati che nel Repository esista il metodo 'existsOverlappingLessonExcludingId'
        // Se l'hai rimosso dal Repository, rimuovi anche questo metodo qui.
        return attivitaRepository.existsOverlappingLessonExcludingId(docente, data, oraInizio, oraFine, id);
    }

    /* =================================================================
       ISCRIZIONE STUDENTI
       ================================================================= */
    @Transactional
    public void iscriviStudenteAdAttivita(String emailStudente, Long idAttivita) {
        Studente studente = studenteRepository.findById(emailStudente)
                .orElseThrow(() -> new RuntimeException("Studente non trovato"));
        Attivita attivita = attivitaRepository.findById(idAttivita)
                .orElseThrow(() -> new RuntimeException("Attività non trovata"));

        // CORREZIONE 1: Messaggio esatto richiesto dal test
        if (attivita.getIscritti().size() >= attivita.getPosti()) {
            throw new RuntimeException("Impossibile iscriversi: l'attività " + attivita.getTitolo() + " è già al completo");
        }

        // CORREZIONE 2: Messaggio esteso richiesto dal test per i pagamenti
        Famiglia famiglia = studente.getFamiglia();
        if (famiglia != null) {
            boolean haPagamentiScaduti = famiglia.getPagamentiEffettuati().stream()
                    .anyMatch(p -> p.getStato() == StatoPagamento.SCADUTO);

            if (haPagamentiScaduti) {
                throw new RuntimeException("Iscrizione negata: la famiglia associata allo studente ha pagamenti in sospeso (Stato: SCADUTO). " +
                        "Si prega di regolarizzare la posizione prima di procedere.");
            }
        }

        attivita.aggiungiStudente(studente);
        attivitaRepository.save(attivita);
    }
}