package it.unisa.ilfarodellostudio.feedbacks;

import it.unisa.ilfarodellostudio.feedbacks.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    /**
     * Restituisce tutti i feedback ricevuti da un docente specifico.
     */
    List<Feedback> findByDocenteEmail(String email);

    /**
     * Restituisce tutti i feedback inviati da uno studente specifico.
     */
    List<Feedback> findByStudenteEmail(String email);

    /**
     * Restituisce tutti i feedback inviati da una famiglia specifica.
     */
    List<Feedback> findByFamigliaEmail(String email);
}