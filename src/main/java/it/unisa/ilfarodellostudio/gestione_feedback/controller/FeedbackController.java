package it.unisa.ilfarodellostudio.gestione_feedback.controller;

import it.unisa.ilfarodellostudio.gestione_feedback.service.FeedbacksService;
import it.unisa.ilfarodellostudio.gestione_utenze.service.UsersService;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Docente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbacksService feedbacksService;

    @Autowired
    private UsersService usersService;

    /**
     * Mostra il form per lasciare un feedback.
     * Utilizza UsersService per recuperare i docenti.
     */
    @GetMapping("/lascia")
    public String mostraFormFeedback(Authentication authentication, Model model) {
        List<Docente> docenti = usersService.getAllDocenti();
        model.addAttribute("listaDocenti", docenti);

        // Determiniamo il ruolo qui per semplificare la vita a Thymeleaf
        boolean isStudente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENTE"));

        model.addAttribute("userRole", isStudente ? "STUDENTE" : "FAMIGLIA");

        return "lascia-feedback";
    }

    /**
     * Gestisce l'invio del form.
     * Identifica l'utente loggato e smista la richiesta al metodo corretto del service.
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

