package it.unisa.ilfarodellostudio.activities.controller;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.users.UsersService;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private MateriaRepository materiaRepository; // Serve per gestire le materie!

    @Autowired
    private UsersService usersService;

    // Rotta per visualizzare "Le mie Attività"
    @GetMapping("/docente/gestione-attivita")
    public String gestioneAttivita(Model model, Principal principal) {
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));
        
        // Passiamo la lista delle attività create da questo docente
        model.addAttribute("attivitaDocente", docente.getAttivitaCreate());
        
        return "docente/gestione-attivita";
    }

    // Rotta per visualizzare il form "Crea Nuova"
    @GetMapping("/docente/crea-attivita")
    public String creaNuovaAttivita(Model model, Principal principal) {
        // 1. Recuperiamo l'email del docente loggato
        String emailDocente = principal.getName();

        // 2. Cerchiamo il docente nel DB
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));

        // 3. Passiamo al modello solo le materie insegnate da lui
        model.addAttribute("materieDocente", docente.getMaterieInsegnate());

        return "docente/crea-attivita";
    }

    /* =======================
       CREA ATTIVITÀ (POST)
       ======================= */
    @PostMapping("/attivita/crea")
    public String creaAttivita(@RequestParam String nome,
                               @RequestParam String descrizione,
                               @RequestParam String materia, // Riceve la stringa dal form
                               @RequestParam LocalDate data,
                               @RequestParam LocalTime oraInizio,
                               @RequestParam LocalTime oraFine,
                               @RequestParam Integer posti,
                               Model model, Principal principal,
                               RedirectAttributes redirectAttributes) {

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
        attivita.setOraInizio(oraInizio);
        attivita.setOraFine(oraFine);
        attivita.setPosti(posti);
        attivita.setMateria(materiaObj); // Setta l'oggetto Materia

        // 3. Recupera il Docente loggato e associalo all'attività
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));
        attivita.setDocente(docente);

        activitiesService.creaAttivita(attivita);

        redirectAttributes.addFlashAttribute("success", "Attività creata con successo!");
        return "redirect:/docente/gestione-attivita";
    }

    /* =======================
       FORM MODIFICA
       ======================= */
    @GetMapping("/attivita/modifica/{id}")
    public String mostraFormModifica(@PathVariable Long id, Model model, Principal principal) {
        Attivita attivita = activitiesService.visualizzaAttivita(id);
        
        // Carichiamo anche le materie del docente per la modifica
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));
        model.addAttribute("materieDocente", docente.getMaterieInsegnate());
        
        model.addAttribute("attivita", attivita);
        return "docente/modifica-attivita";
    }

    /* =======================
     MODIFICA ATTIVITÀ (POST)
     ======================= */
    @PostMapping("/attivita/modifica")
    public String modificaAttivita(@RequestParam Long id,
                                   @RequestParam String nome,
                                   @RequestParam String descrizione,
                                   @RequestParam String materia, // Riceve il nome della materia
                                   @RequestParam LocalDate data,
                                   @RequestParam LocalTime oraInizio,
                                   @RequestParam LocalTime oraFine,
                                   @RequestParam Integer posti,
                                   Model model, Principal principal,
                                   RedirectAttributes redirectAttributes) {

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
            attivita.setOraInizio(oraInizio);
            attivita.setOraFine(oraFine);
            attivita.setPosti(posti);
            attivita.setMateria(materiaObj); // Imposta l'oggetto Materia

            // 4. Salva
            activitiesService.modificaAttivita(attivita);

            // Messaggio di successo e reindirizzamento
            redirectAttributes.addFlashAttribute("success", "Attività modificata con successo!");
            return "redirect:/docente/gestione-attivita";
            
        } else {
            model.addAttribute("error", "Errore: Attività non trovata");
            // Ricarichiamo i dati necessari per il form in caso di errore
            String emailDocente = principal.getName();
            Docente docente = usersService.cercaDocente(emailDocente)
                    .orElseThrow(() -> new RuntimeException("Docente non trovato"));
            model.addAttribute("materieDocente", docente.getMaterieInsegnate());
            model.addAttribute("attivita", attivita);
            return "docente/modifica-attivita";
        }
    }
}