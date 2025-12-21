package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.users.Studente;
import it.unisa.ilfarodellostudio.users.StudenteDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servizio per la gestione delle operazioni relative alle attività.
 * Fornisce la logica di business per le iscrizioni e la gestione delle attività.
 */
@Service
public class AttivitaService {

    @Autowired
    private AttivitaDAO attivitaDAO;

    @Autowired
    private StudenteDAO studenteDAO;

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

        Studente studente = studenteDAO.findById(idStudente)
                .orElseThrow(() -> new RuntimeException("Studente " + idStudente + " non trovato"));

        Attivita attivita = attivitaDAO.findById(idAttivita)
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
        attivitaDAO.save(attivita);
    }
}
