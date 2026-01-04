package it.unisa.ilfarodellostudio.feedbacks;

import it.unisa.ilfarodellostudio.users.UsersService;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller per la gestione delle richieste web relative ai feedback.
 */
@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbacksService feedbacksService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private AttivitaRepository attivitaRepository;

    /**
     * Mostra il form per lasciare un feedback su un'attività.
     *
     * @param idAttivita ID dell'attività
     * @param model modello per la vista
     * @return la vista del form feedback
     */
    @GetMapping("/nuovo/{idAttivita}")
    public String mostraFormFeedback(@PathVariable Long idAttivita, Model model, Authentication authentication) {
        // Recupera l'attività per mostrare il titolo nella pagina
        Attivita attivita = attivitaRepository.findById(idAttivita)
                .orElseThrow(() -> new IllegalArgumentException("Attività non trovata"));

        model.addAttribute("attivita", attivita);
        model.addAttribute("docenteEmail", attivita.getDocente().getEmail());

        // Determiniamo il ruolo qui per semplificare la vita a Thymeleaf
        boolean isStudente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENTE"));

        model.addAttribute("userRole", isStudente ? "STUDENTE" : "FAMIGLIA");

        return "lascia-feedback";
    }

    /**
     * Salva il feedback inviato tramite form.
     *
     * @param idAttivita ID dell'attività
     * @param valutazione voto
     * @param commento commento
     * @param principal info utente
     * @param model modello vista
     * @return redirect alla dashboard o al form in caso di errore
     */
    @PostMapping("/salva")
    public String salvaFeedback(@RequestParam String docenteEmail,
                                @RequestParam int valutazione,
                                @RequestParam(required = false) String commento,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            // Otteniamo l'email dell'utente autenticato (mittente)
            String emailMittente = authentication.getName();

            // Verifichiamo il ruolo dell'utente
            boolean isStudente = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENTE"));

            if (isStudente) {
                feedbacksService.inviaFeedbackDaStudente(emailMittente, docenteEmail, valutazione, commento);
            } else {
                feedbacksService.inviaFeedbackDaFamiglia(emailMittente, docenteEmail, valutazione, commento);
            }

            // Messaggio di successo che apparirà nella dashboard
            redirectAttributes.addFlashAttribute("success", "Il tuo feedback è stato inviato con successo al docente.");

            // Redirect dinamico alla dashboard corretta
            return isStudente ? "redirect:/studente/dashboard-studente" : "redirect:/famiglia/dashboard-famiglia";

        } catch (Exception e) {
            // In caso di errore (es. docente non trovato o validazione fallita)
            redirectAttributes.addFlashAttribute("error", "Impossibile inviare il feedback: " + e.getMessage());
            return "redirect:/feedback/lascia";
        }
    }
}
