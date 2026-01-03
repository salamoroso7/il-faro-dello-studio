package it.unisa.ilfarodellostudio.feedbacks;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbacksService feedbackService;

    @Autowired
    private ActivitiesService activitiesService; // Serve per recuperare info sull'attività

    // 1. Mostra il form per lasciare il feedback
    // URL Esempio: /feedback/nuovo/15 (dove 15 è l'ID dell'attività)
    @GetMapping("/nuovo/{idAttivita}")
    public String mostraFormFeedback(@PathVariable Long idAttivita, Model model) {
        // Recupera l'attività per mostrare il titolo nella pagina
      //  Attivita attivita = activitiesService.getAttivitaById(idAttivita);

        //model.addAttribute("attivita", attivita);
        return "studente/lascia-feedback"; // Crea questo file HTML!
    }

    // 2. Salva il feedback inviato dal form
    @PostMapping("/salva")
    public String salvaFeedback(@RequestParam Long idAttivita,
                                @RequestParam int valutazione,
                                @RequestParam String commento,
                                Principal principal,
                                Model model) {
        try {
            String emailStudente = principal.getName();
            feedbackService.lasciaFeedback(idAttivita, emailStudente, valutazione, commento);

            return "redirect:/studente/dashboard-studente?success=FeedbackInviato";

        } catch (Exception e) {
            // In caso di errore ricarica la pagina con il messaggio
            model.addAttribute("error", e.getMessage());
            // Dobbiamo ricaricare l'oggetto attivita per non rompere la pagina
           // Attivita att = activitiesService.getAttivitaById(idAttivita);
          //  model.addAttribute("attivita", att);

            return "studente/lascia-feedback";
        }
    }
}
