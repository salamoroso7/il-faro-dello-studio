package it.unisa.ilfarodellostudio.activities.controller;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
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
    private UsersService usersService;

    @GetMapping("/studente/dettaglio-attivita")
    public String mostraDettagliAttivita() {
        return "studente/dettaglio-attivita";
    }

    // --- LISTA ATTIVITÀ ---
    @GetMapping("/docente/gestione-attivita")
    public String gestioneAttivita(Model model, Principal principal) {
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));

        model.addAttribute("attivitaDocente", activitiesService.dammiTutteLeAttivita(docente));
        return "docente/gestione-attivita";
    }

    // --- FORM CREA ---
    @GetMapping("/docente/crea-attivita")
    public String creaNuovaAttivita(Model model, Principal principal) {
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));
        model.addAttribute("materieDocente", docente.getMaterieInsegnate());
        return "docente/crea-attivita";
    }

    // --- POST CREA (GESTIONE ERRORI TRAMITE CATCH) ---
    @PostMapping("/attivita/crea")
    public String creaAttivita(@RequestParam String nome,
                               @RequestParam String descrizione,
                               @RequestParam String materia,
                               @RequestParam LocalDate data,
                               @RequestParam LocalTime oraInizio,
                               @RequestParam LocalTime oraFine,
                               @RequestParam Integer posti,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            // 1. Recupero Dati
            String emailDocente = principal.getName();
            Docente docente = usersService.cercaDocente(emailDocente)
                    .orElseThrow(() -> new RuntimeException("Docente non trovato"));

            // 2. Costruzione Oggetti
            Materia materiaObj = new Materia();
            materiaObj.setNome(materia);

            Attivita attivita = new Attivita();
            attivita.setTitolo(nome);
            attivita.setDescrizione(descrizione);
            attivita.setData(data);
            attivita.setOraInizio(oraInizio);
            attivita.setOraFine(oraFine);
            attivita.setPosti(posti);
            attivita.setMateria(materiaObj);
            attivita.setDocente(docente);

            // 3. CHIAMATA AL SERVICE (Qui avvengono le validazioni)
            activitiesService.creaAttivita(attivita);

            // 4. Successo
            redirectAttributes.addFlashAttribute("success", "Attività creata con successo!");
            return "redirect:/docente/gestione-attivita";

        } catch (RuntimeException e) {
            // 5. ERRORE CATTURATO (Data passata, sovrapposizione, ecc.)
            // Mostriamo il messaggio nell'HTML senza far crashare il sito
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/docente/gestione-attivita";
        }
    }

    // --- FORM MODIFICA ---
    @GetMapping("/attivita/modifica/{id}")
    public String mostraFormModifica(@PathVariable Long id, Model model, Principal principal) {
        Attivita attivita = activitiesService.visualizzaAttivita(id);
        String emailDocente = principal.getName();

        if (attivita == null || !attivita.getDocente().getEmail().equals(emailDocente)) {
            return "redirect:/docente/gestione-attivita?error=Non autorizzato";
        }

        Docente docente = usersService.cercaDocente(emailDocente).orElseThrow();
        model.addAttribute("materieDocente", docente.getMaterieInsegnate());
        model.addAttribute("attivita", attivita);
        return "docente/modifica-attivita";
    }

    // --- POST MODIFICA ---
    @PostMapping("/attivita/modifica")
    public String modificaAttivita(@RequestParam Long id,
                                   @RequestParam String nome,
                                   @RequestParam String descrizione,
                                   @RequestParam String materia,
                                   @RequestParam LocalDate data,
                                   @RequestParam LocalTime oraInizio,
                                   @RequestParam LocalTime oraFine,
                                   @RequestParam Integer posti,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        Attivita attivita = activitiesService.visualizzaAttivita(id);
        String emailDocente = principal.getName();

        if (attivita == null || !attivita.getDocente().getEmail().equals(emailDocente)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Attività non trovata o non autorizzata.");
            return "redirect:/docente/gestione-attivita";
        }

        // Qui replichiamo i controlli base per sicurezza anche in modifica
        if (LocalDateTime.of(data, oraInizio).isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Errore: Non puoi spostare lezioni nel passato.");
            return "redirect:/attivita/modifica/" + id;
        }

        // Verifica sovrapposizione escluso se stesso
        if (activitiesService.verificaSovrapposizioneEsclusoId(attivita.getDocente(), data, oraInizio, oraFine, id)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Sovrapposizione con un'altra lezione.");
            return "redirect:/attivita/modifica/" + id;
        }

        // Aggiornamento
        attivita.setTitolo(nome);
        attivita.setDescrizione(descrizione);
        attivita.setData(data);
        attivita.setOraInizio(oraInizio);
        attivita.setOraFine(oraFine);
        attivita.setPosti(posti);

        if(!attivita.getMateria().getNome().equals(materia)) {
            Materia m = new Materia();
            m.setNome(materia);

            Materia materiaDb = activitiesService.cercaMateriaPerNome(materia)
                    .orElseGet(() -> activitiesService.salvaMateria(m));

            attivita.setMateria(materiaDb);
        }

        activitiesService.modificaAttivita(attivita);

        redirectAttributes.addFlashAttribute("success", "Attività modificata con successo!");
        return "redirect:/docente/gestione-attivita";
    }

    // --- CANCELLA ---
    @GetMapping("/attivita/cancella/{id}")
    public String cancellaAttivita(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Attivita attivita = activitiesService.visualizzaAttivita(id);
            String emailDocente = principal.getName();

            if (attivita != null && attivita.getDocente().getEmail().equals(emailDocente)) {
                activitiesService.eliminaAttivita(id);
                redirectAttributes.addFlashAttribute("success", "Attività eliminata.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Errore: Non autorizzato.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore durante l'eliminazione.");
        }
        return "redirect:/docente/gestione-attivita";
    }
}