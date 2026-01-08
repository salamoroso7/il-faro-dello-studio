package it.unisa.ilfarodellostudio.gestione_feedback.service;

import it.unisa.ilfarodellostudio.gestione_feedback.dao.FeedbackRepository;
import it.unisa.ilfarodellostudio.gestione_feedback.dao.entity.Feedback;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Docente;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Famiglia;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Studente;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.StudenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service per la gestione dei feedback.
 * Gestisce l'inserimento e il recupero dei feedback.
 */
@Service
public class FeedbacksService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private FamigliaRepository famigliaRepository;

    /**
     * Consente a uno studente di inviare un feedback a un docente.
     * @param studenteEmail L'email dello studente (mittente)
     * @param docenteEmail  L'email del docente (destinatario)
     * @param valutazione   Punteggio da 1 a 5
     * @param commento      Testo opzionale del feedback
     * @throws EntityNotFoundException se lo studente o il docente non esistono
     * @throws IllegalArgumentException se la valutazione non è nel range consentito
     */
    @Transactional
    public void inviaFeedbackDaStudente(String studenteEmail, String docenteEmail, int valutazione, String commento) {
        Studente studente = studenteRepository.findById(studenteEmail)
                .orElseThrow(() -> new EntityNotFoundException("Studente non trovato"));

        Docente docente = docenteRepository.findById(docenteEmail)
                .orElseThrow(() -> new EntityNotFoundException("Docente non trovato"));

        Feedback feedback = creaBaseFeedback(docente, valutazione, commento);
        feedback.setStudente(studente);

        feedbackRepository.save(feedback);
    }

    /**
     * Consente a una famiglia di inviare un feedback a un docente.
     * @param famigliaEmail L'email della famiglia (mittente)
     * @param docenteEmail  L'email del docente (destinatario)
     * @param valutazione   Punteggio da 1 a 5
     * @param commento      Testo opzionale del feedback
     * @throws EntityNotFoundException se la famiglia o il docente non esistono
     */
    @Transactional
    public void inviaFeedbackDaFamiglia(String famigliaEmail, String docenteEmail, int valutazione, String commento) {
        Famiglia famiglia = famigliaRepository.findById(famigliaEmail)
                .orElseThrow(() -> new EntityNotFoundException("Famiglia non trovata"));

        Docente docente = docenteRepository.findById(docenteEmail)
                .orElseThrow(() -> new EntityNotFoundException("Docente non trovato"));

        Feedback feedback = creaBaseFeedback(docente, valutazione, commento);
        feedback.setFamiglia(famiglia); // Imposta la famiglia come autore

        feedbackRepository.save(feedback);
    }

    private Feedback creaBaseFeedback(Docente docente, int valutazione, String commento) {
        if (valutazione < 1 || valutazione > 5) {
            throw new IllegalArgumentException("La valutazione deve essere compresa tra 1 e 5");
        }

        Feedback feedback = new Feedback();
        feedback.setDocente(docente);
        feedback.setValutazione(valutazione);
        feedback.setCommento(commento);
        return feedback;
    }


}
