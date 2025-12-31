package it.unisa.ilfarodellostudio.auth;

import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller dedicato alla gestione delle rotte di autenticazione.
 * <p>
 * All'interno dell'architettura 3-tier del progetto, questa classe si colloca nel
 * <b>Presentation Layer</b>. Il suo compito principale è gestire le richieste
 * HTTP relative all'accesso e all'uscita dal sistema, fungendo da porta di ingresso
 * per gli utenti (Docenti, Famiglie e Studenti).
 */
@Controller
public class AuthController {

    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private FamigliaRepository famigliaRepository;

    /**
     * Gestisce la richiesta GET per la pagina di login.
     * <p>
     * Questo metodo si limita a restituire il nome del template Thymeleaf da renderizzare.
     * È importante notare che non esiste un metodo {@code @PostMapping} per il login in
     * questa classe, poiché il processo di validazione delle credenziali e la gestione
     * della sessione HTTP sono intercettati e gestiti automaticamente da Spring Security
     * in base alla configurazione definita in {@code SecurityConfig}.
     * </p>
     *
     * @return Il nome del file HTML della pagina di login (login.html) senza estensione.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Ritorna login.html
    }

    // Dashboard per i Docenti
    @GetMapping("/docente/dashboard-docente")
    public String dashboardDocente(Authentication authentication, Model model) {
        String email = authentication.getName();

        // Cerca il docente nel DB
        Docente docente = docenteRepository.findByEmail(email).orElse(null);

        // Passa l'intero oggetto docente alla pagina HTML
        model.addAttribute("docente", docente);

        return "docente/dashboard-docente";
    }

    // Dashboard per le Famiglie
    @GetMapping("/famiglia/dashboard-famiglia")
    public String dashboardFamiglia(Authentication authentication, Model model) {
        String email = authentication.getName();
        Famiglia famiglia = famigliaRepository.findByEmail(email).orElse(null);
        model.addAttribute("famiglia", famiglia);
        return "famiglia/dashboard-famiglia";
    }

    // Dashboard per gli Studenti
    @GetMapping("/studente/dashboard-studente")
    public String dashboardStudente() {
        return "studente/dashboard-studente";
    }
}