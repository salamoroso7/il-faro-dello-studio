package it.unisa.ilfarodellostudio.feedbacks;

import it.unisa.ilfarodellostudio.feedbacks.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository per la gestione della persistenza dei feedback.
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    /**
     * Restituisce tutti i feedback ricevuti da un docente specifico.
     */
    List<Feedback> findByDocenteEmail(String email);

    /**
     * Trova tutti i feedback relativi a un'attività.
     *
     * @param attivita l'attività
     * @return lista di feedback
     */
    List<Feedback> findByAttivita(Attivita attivita);

    /**
     * Verifica se uno studente ha già lasciato un feedback per un'attività.
     *
     * @param attivita l'attività
     * @param studente lo studente
     * @return true se il feedback esiste già, false altrimenti
     */
    boolean existsByAttivitaAndStudente(Attivita attivita, Studente studente);
}