package it.unisa.ilfarodellostudio.auth;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.users.UsersService;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import it.unisa.ilfarodellostudio.activities.ActivitiesService;


import java.security.Principal;
import java.util.List;

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
    @Autowired
    private UsersService usersService;

    @Autowired
    private ActivitiesService activitiesService;

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

    @GetMapping("/admin/dashboard-admin")
    public String dashboardAdmin() {
        return "admin/dashboard-admin";
    }

    // Dashboard per i Docenti
    @GetMapping("/docente/dashboard-docente")
    public String dashboardDocente(Authentication authentication, Model model) {
        String email = authentication.getName();

        // 2. Recupera l'oggetto Docente completo dal DB
        Docente docente = usersService.cercaDocente(email)
                .orElseThrow(() -> new RuntimeException("Errore: Docente non trovato nel sistema"));

        model.addAttribute("docente", docente);

        // 3. Recupera le attività per contarle
        // NOTA: Assicurati che in ActivitiesService esista il metodo 'dammiTutteLeAttivita(Docente d)'
        // o usa il nome del metodo che hai (es. visualizzaAttivitaDocente)
        List<Attivita> listaAttivita = activitiesService.dammiTutteLeAttivita(docente);
        System.out.println("DEBUG: Numero attività trovate nel DB -> " + listaAttivita.size()); // <--- GUARDA QUI
        model.addAttribute("numeroAttivita", listaAttivita.size());

        // 4. Dati Recensioni (Placeholder finché non implementi il sistema feedback)
        model.addAttribute("mediaRecensioni", 0.0);
        model.addAttribute("numeroRecensioni", 0);

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