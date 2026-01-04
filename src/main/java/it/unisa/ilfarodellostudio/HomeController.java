package it.unisa.ilfarodellostudio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller principale che gestisce la pagina iniziale dell'applicazione.
 */
@Controller
public class HomeController {

    /**
     * Gestisce la richiesta GET per la root ("/") dell'applicazione.
     *
     * @return il nome della vista "index"
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
