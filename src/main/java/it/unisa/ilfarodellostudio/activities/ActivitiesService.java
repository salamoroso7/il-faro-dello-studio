package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivitiesService {

    @Autowired
    private AttivitaRepository attivitaRepository;

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
}
