package it.unisa.ilfarodellostudio.feedbacks;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.feedbacks.entity.Feedback;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Trova tutti i feedback di una specifica attività (utile per il docente)
    List<Feedback> findByAttivita(Attivita attivita);

    // Controlla se uno studente ha già recensito un'attività (per evitare doppi voti)
    boolean existsByAttivitaAndStudente(Attivita attivita, Studente studente);
}