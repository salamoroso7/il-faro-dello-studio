package it.unisa.ilfarodellostudio.activities.controller;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private MateriaRepository materiaRepository; // Serve per gestire le materie!

    // Rotta per visualizzare "Le mie Attività"
    @GetMapping("/docente/gestione-attivita")
    public String gestioneAttivita() {
        // Punta a templates/docente/gestione-attivita.html
        return "docente/gestione-attivita";
    }

    // Rotta per visualizzare il form "Crea Nuova"
    @GetMapping("/docente/crea-attivita")
    public String creaNuovaAttivita() {
        // Punta a templates/docente/crea-attivita.html
        return "docente/crea-attivita";
    }

    /* =======================
       CREA ATTIVITÀ (POST)
       ======================= */
    @PostMapping("/crea")
    public String creaAttivita(@RequestParam String nome,
                               @RequestParam String descrizione,
                               @RequestParam String materia, // Riceve la stringa dal form
                               @RequestParam LocalDate data,
                               @RequestParam LocalTime ora,
                               Model model) {

        // 1. Gestione della Materia
        // Cerchiamo se la materia esiste già, altrimenti la creiamo al volo
        Materia materiaObj = materiaRepository.findById(materia).orElseGet(() -> {
            Materia nuova = new Materia();
            nuova.setNome(materia);
            return materiaRepository.save(nuova);
        });

        // 2. Creazione Attività
        Attivita attivita = new Attivita();
        attivita.setTitolo(nome); // L'entità usa setTitolo, non setNome
        attivita.setDescrizione(descrizione);
        attivita.setData(data);
        attivita.setOraInizio(ora);
        attivita.setMateria(materiaObj); // Setta l'oggetto Materia

        activitiesService.creaAttivita(attivita);

        model.addAttribute("success", "Attività creata con successo");
        return "crea-attivita";
    }

    /* =======================
       FORM MODIFICA
       ======================= */
    @GetMapping("/modifica/{id}")
    public String mostraFormModifica(@PathVariable Long id, Model model) {
        Attivita attivita = activitiesService.visualizzaAttivita(id);
        model.addAttribute("attivita", attivita);
        return "modifica-attivita";
    }

    /* =======================
     MODIFICA ATTIVITÀ (POST)
     ======================= */
    @PostMapping("/modifica")
    public String modificaAttivita(@RequestParam Long id,
                                   @RequestParam String nome,
                                   @RequestParam String descrizione,
                                   @RequestParam String materia, // Riceve il nome della materia
                                   @RequestParam LocalDate data,
                                   @RequestParam LocalTime ora,
                                   Model model) {

        // 1. Recupera l'attività esistente
        Attivita attivita = activitiesService.visualizzaAttivita(id);

        if (attivita != null) {
            // 2. Gestione Materia (Cerca o Crea)
            // Usiamo la repository per trovare la materia o crearne una nuova
            Materia materiaObj = materiaRepository.findById(materia).orElseGet(() -> {
                Materia nuova = new Materia();
                nuova.setNome(materia);
                return materiaRepository.save(nuova);
            });

            // 3. Aggiorna i campi
            attivita.setTitolo(nome); // Mappa 'nome' del form su 'titolo' dell'entità
            attivita.setDescrizione(descrizione);
            attivita.setData(data);
            attivita.setOraInizio(ora);
            attivita.setMateria(materiaObj); // Imposta l'oggetto Materia

            // 4. Salva
            activitiesService.modificaAttivita(attivita);

            // Messaggio di successo
            model.addAttribute("success", "Attività modificata con successo!");
            model.addAttribute("attivita", attivita); // Ricarica l'oggetto aggiornato nella pagina
        } else {
            model.addAttribute("error", "Errore: Attività non trovata");
        }

        // Rimane sulla pagina di modifica
        return "modifica-attivita";
        // Oppure se preferisci tornare alla lista (che non hai ancora): return "redirect:/attivita/lista";
    }
}