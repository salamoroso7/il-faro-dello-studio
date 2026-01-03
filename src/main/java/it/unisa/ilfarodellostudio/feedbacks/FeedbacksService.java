package it.unisa.ilfarodellostudio.feedbacks;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.unisa.ilfarodellostudio.feedbacks.entity.Feedback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbacksService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private AttivitaRepository attivitaRepository;
    @Autowired
    private StudenteRepository studenteRepository;

    @Transactional
    public void lasciaFeedback(Long idAttivita, String emailStudente, int valutazione, String commento) {

        // 1. Recupera Attività e Studente
        Attivita attivita = attivitaRepository.findById(idAttivita)
                .orElseThrow(() -> new IllegalArgumentException("Attività non trovata"));

        Studente studente = studenteRepository.findById(emailStudente)
                .orElseThrow(() -> new IllegalArgumentException("Studente non trovato"));

        // 2. Controllo: Lo studente ha partecipato all'attività?
        // (Opzionale: rimuovi questo if se vuoi permettere feedback liberi)
        if (!attivita.getIscritti().contains(studente)) {
            throw new IllegalStateException("Non puoi recensire un'attività a cui non sei iscritto.");
        }

        // 3. Controllo: Ha già votato?
        if (feedbackRepository.existsByAttivitaAndStudente(attivita, studente)) {
            throw new IllegalStateException("Hai già inviato un feedback per questa attività.");
        }

        // 4. Salva il Feedback
        Feedback feedback = new Feedback();
        feedback.setAttivita(attivita);
        feedback.setStudente(studente);
        feedback.setValutazione(valutazione);
        feedback.setCommento(commento);
        feedback.setDataInserimento(LocalDateTime.now());

        feedbackRepository.save(feedback);
    }

    // Recupera i feedback per una data attività (per farli vedere al docente)
    public List<Feedback> getFeedbackPerAttivita(Long idAttivita) {
        Attivita attivita = attivitaRepository.findById(idAttivita)
                .orElseThrow(() -> new IllegalArgumentException("Attività non trovata"));
        return feedbackRepository.findByAttivita(attivita);
    }
}