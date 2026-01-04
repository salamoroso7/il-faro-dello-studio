package it.unisa.ilfarodellostudio.auth;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.feedbacks.entity.Feedback;
import it.unisa.ilfarodellostudio.users.UsersService;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.UtenteRegistrato;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    /**
     * Gestisce la dashboard per l'amministratore.
     * Recupera le statistiche principali (utenti totali, sospesi, ultime registrazioni)
     * e le passa alla vista.
     *
     * @param model il modello per la vista
     * @return la vista della dashboard admin
     */
    @GetMapping("/admin/dashboard-admin")
    public String dashboardAdmin(Model model) {
        // Recupera le statistiche dal service
        long totalUtenti = usersService.countAllUtenti();
        long utentiSospesi = usersService.countUtentiSospesi();
        List<UtenteRegistrato> ultimeRegistrazioni = usersService.getUltimeRegistrazioni(3);

        // Passa i dati al template
        model.addAttribute("totalUtenti", totalUtenti);
        model.addAttribute("utentiSospesi", utentiSospesi);
        model.addAttribute("ultimeRegistrazioni", ultimeRegistrazioni);

        return "admin/dashboard-admin";
    }

    /**
     * Gestisce la dashboard per il docente.
     * Recupera i dati del docente e le statistiche delle sue attività.
     *
     * @param authentication informazioni sull'autenticazione corrente
     * @param model il modello per la vista
     * @return la vista della dashboard docente
     */
    @GetMapping("/docente/dashboard-docente")
    public String dashboardDocente(Authentication authentication, Model model) {
        String email = authentication.getName();

        // 1. Recupera l'oggetto Docente (che contiene già la lista feedbackRicevuti)
        Docente docente = usersService.cercaDocente(email)
                .orElseThrow(() -> new RuntimeException("Errore: Docente non trovato"));

        model.addAttribute("docente", docente);

        // 2. Calcolo Attività
        List<Attivita> listaAttivita = activitiesService.dammiTutteLeAttivita(docente);
        model.addAttribute("numeroAttivita", listaAttivita.size());

        // 3. CALCOLO REALE FEEDBACK
        List<Feedback> feedbacks = docente.getFeedbackRicevuti();
        int numeroRecensioni = (feedbacks != null) ? feedbacks.size() : 0;

        // Calcolo della media usando gli Stream di Java
        double mediaRecensioni = 0.0;
        if (numeroRecensioni > 0) {
            mediaRecensioni = feedbacks.stream()
                    .mapToInt(Feedback::getValutazione) // Estrae il voto (int)
                    .average()                          // Calcola la media
                    .orElse(0.0);
        }

        model.addAttribute("numeroRecensioni", numeroRecensioni);
        model.addAttribute("mediaRecensioni", mediaRecensioni);

        // 4. Calcolo Iscritti (esempio basato sulle attività)
        // Se ogni attività ha una lista di iscritti, potresti sommarli così:
        long totaleIscritti = listaAttivita.stream()
                .mapToLong(a -> a.getIscritti() != null ? a.getIscritti().size() : 0)
                .sum();
        model.addAttribute("totaleIscritti", totaleIscritti);

        return "docente/dashboard-docente";
    }

    /**
     * Gestisce la dashboard per la famiglia.
     * Visualizza le informazioni relative alla famiglia.
     *
     * @param authentication informazioni sull'autenticazione corrente
     * @param model il modello per la vista
     * @return la vista della dashboard famiglia
     */
    @GetMapping("/famiglia/dashboard-famiglia")
    public String dashboardFamiglia(Authentication authentication, Model model) {
        String email = authentication.getName();
        Famiglia famiglia = famigliaRepository.findByEmail(email).orElse(null);
        model.addAttribute("famiglia", famiglia);
        return "famiglia/dashboard-famiglia";
    }

    /**
     * Gestisce la dashboard per lo studente.
     *
     * @return la vista della dashboard studente
     */
    @GetMapping("/studente/dashboard-studente")
    public String dashboardStudente() {
        return "studente/dashboard-studente";
    }
}