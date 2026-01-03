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
import java.time.LocalDateTime;
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
                               @RequestParam String materia,
                               @RequestParam LocalDate data,
                               @RequestParam LocalTime oraInizio,
                               @RequestParam LocalTime oraFine,
                               @RequestParam Integer posti,
                               Model model, Principal principal,
                               RedirectAttributes redirectAttributes) {

        // --- 1. RECUPERO DOCENTE ---
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));

        // --- 2. VALIDAZIONI (Mancavano i return!) ---

        // A. Orari Invertiti
        if (!oraFine.isAfter(oraInizio)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Fine lezione deve essere successiva all'inizio.");
            return "redirect:/docente/gestione-attivita"; // <--- AGGIUNTO QUESTO
        }

        // B. Data nel Passato
        if (LocalDateTime.of(data, oraInizio).isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Errore: Non puoi creare lezioni nel passato.");
            return "redirect:/docente/gestione-attivita"; // <--- AGGIUNTO QUESTO
        }

        // C. Sovrapposizione
        // Nota: Il service deve usare il metodo che passa solo l'ID del docente (visto nel passaggio precedente)
        if (activitiesService.existsOverlappingLesson(docente, data, oraInizio, oraFine)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Sovrapposizione con un'altra lezione.");
            return "redirect:/docente/gestione-attivita"; // <--- AGGIUNTO QUESTO
        }

        // --- 3. LOGICA DI CREAZIONE (Eseguita solo se i return sopra non scattano) ---

        // CORREZIONE IMPORTANTE: Usa findByNome (perché 'materia' è una Stringa), non findById!
        Materia materiaObj = materiaRepository.findByNome(materia).orElseGet(() -> {
            Materia nuova = new Materia();
            nuova.setNome(materia);
            return materiaRepository.save(nuova);
        });

        Attivita attivita = new Attivita();
        attivita.setTitolo(nome);
        attivita.setDescrizione(descrizione);
        attivita.setData(data);
        attivita.setOraInizio(oraInizio);
        attivita.setOraFine(oraFine);
        attivita.setPosti(posti);
        attivita.setMateria(materiaObj);
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
                                   @RequestParam String materia,
                                   @RequestParam LocalDate data,
                                   @RequestParam LocalTime oraInizio,
                                   @RequestParam LocalTime oraFine,
                                   @RequestParam Integer posti,
                                   Model model, Principal principal,
                                   RedirectAttributes redirectAttributes) {

        // --- 1. RECUPERO DOCENTE ---
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));

        // --- 2. RECUPERO ATTIVITÀ E CHECK PERMESSI ---
        Attivita attivita = activitiesService.visualizzaAttivita(id);
        if (attivita == null || !attivita.getDocente().getEmail().equals(docente.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Errore: Attività non trovata o non autorizzata.");
            return "redirect:/docente/gestione-attivita";
        }

        // --- 3. VALIDAZIONE ORARI E DATE ---
        if (!oraFine.isAfter(oraInizio)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Fine lezione deve essere successiva all'inizio.");
            return "redirect:/attivita/modifica/" + id;
        }

        if (LocalDateTime.of(data, oraInizio).isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Errore: Non puoi spostare lezioni nel passato.");
            return "redirect:/attivita/modifica/" + id;
        }

        // --- 4. CONTROLLO SOVRAPPOSIZIONE ---
        // Nota: Usiamo la variante che esclude l'ID corrente per non andare in conflitto con se stessa
        if (activitiesService.verificaSovrapposizioneEsclusoId(docente, data, oraInizio, oraFine, id)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Sovrapposizione con un'altra lezione.");
            return "redirect:/attivita/modifica/" + id;
        }

        // --- 5. LOGICA DI MODIFICA (Eseguita solo se i controlli passano) ---
        // Nota: Usiamo findByNome per sicurezza (se materia è una stringa)
        Materia materiaObj = materiaRepository.findByNome(materia).orElseGet(() -> {
            Materia nuova = new Materia();
            nuova.setNome(materia);
            return materiaRepository.save(nuova);
        });

        attivita.setTitolo(nome);
        attivita.setDescrizione(descrizione);
        attivita.setData(data);
        attivita.setOraInizio(oraInizio);
        attivita.setOraFine(oraFine);
        attivita.setPosti(posti);
        attivita.setMateria(materiaObj);

        activitiesService.modificaAttivita(attivita);

        redirectAttributes.addFlashAttribute("success", "Attività modificata con successo!");
        return "redirect:/docente/gestione-attivita";
    }
}