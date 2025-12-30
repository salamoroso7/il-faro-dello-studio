package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servizio per la gestione delle operazioni relative alle attività.
 * Fornisce la logica di business per le iscrizioni e la gestione delle attività.
 */
@Service
public class ActivitiesService {

    @Autowired
    private AttivitaRepository attivitaRepository;
  
    @Autowired
    private StudenteRepository studenteRepository;

    // CREATE
    public Attivita creaAttivita(Attivita attivita) {
        return attivitaRepository.save(attivita);
    }

    // READ ALL
    public List<Attivita> visualizzaTutteLeAttivita() {
        return attivitaRepository.findAll();
    }

    // READ BY ID
    public Attivita visualizzaAttivita(Long id) {
        Optional<Attivita> risultato = attivitaRepository.findById(id);
        return risultato.orElse(null);
    }

    // UPDATE
    public Attivita modificaAttivita(Attivita attivita) {
        return attivitaRepository.save(attivita);
    }

    // DELETE
    public void eliminaAttivita(Long id) {
        attivitaRepository.deleteById(id);
    }

    /**
     * Esegue l'iscrizione di uno studente a una determinata attività.
     * <p>
     * Il metodo verifica l'esistenza delle entità, controlla che ci siano posti
     * disponibili nell'attività e, in futuro, verificherà lo stato dei pagamenti
     * dell'account Famiglia associato.
     * </p>
     *
     * @param idStudente l'identificativo univoco dello studente da iscrivere
     * @param idAttivita l'identificativo univoco dell'attività a cui iscriversi
     * @throws RuntimeException se lo studente o l'attività non vengono trovati,
     * o se il numero massimo di posti è stato raggiunto.
     */
    @Transactional
    public void iscriviStudenteAdAttivita(Long idStudente, Long idAttivita) {

        Studente studente = studenteRepository.findById(idStudente)
                .orElseThrow(() -> new RuntimeException("Studente " + idStudente + " non trovato"));

        Attivita attivita = attivitaRepository.findById(idAttivita)
                .orElseThrow(() -> new RuntimeException("Attività " + idAttivita + " non trovata"));

        // Controllo posti
        if(attivita.getStudenti().size() >= Attivita.MAX_POSTI) {
            throw new RuntimeException("Impossibile iscriversi: l'attività " + attivita.getNome() + " è già al completo");
        }

        // Controllo pagamenti famiglia
        /* TODO */

        // Aggiunta bidirezionale dello studente all'attività
        attivita.aggiungiStudente(studente);

        // Salvataggio dell'entità proprietaria della relazione
        attivitaRepository.save(attivita);
    }
}
