package it.unisa.ilfarodellostudio.users;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.users.dto.DocenteDto;
import it.unisa.ilfarodellostudio.users.dto.FamigliaDto;
import it.unisa.ilfarodellostudio.users.dto.StudenteDto;
import it.unisa.ilfarodellostudio.users.entity.UtenteRegistrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private ActivitiesService activitiesService;

    /* =========================================================================
       1. REGISTRAZIONE PUBBLICA UNIFICATA (Docenti e Famiglie)
       Usa la pagina 'registrazione.html' che abbiamo creato.
       ========================================================================= */

    // Mostra il form unico di registrazione
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Recuperiamo la lista dal DB
        List<Materia> materie = activitiesService.getAllMaterie();

        // La passiamo al template
        model.addAttribute("materie", materie);

        // Inizializziamo un oggetto vuoto per il form se necessario
        return "registrazione";
    }

    // Gestisce l'invio del form unico
    @PostMapping("/register")
    public String handleRegistration(
            @RequestParam String ruolo, // Campo <select name="ruolo">
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String conferma_password,
            @RequestParam(required = false) List<String> materie,
            Model model) {

        try {

            if (!password.equals(conferma_password)) {
                throw new IllegalArgumentException("Le password non coincidono");
            }

            switch (ruolo.toLowerCase()) {
                case "docente":
                    DocenteDto docenteDto = new DocenteDto();
                    docenteDto.setNome(nome);
                    docenteDto.setCognome(cognome);
                    docenteDto.setEmail(email);
                    docenteDto.setPassword(password);
                    docenteDto.setMaterie(materie);

                    usersService.registraDocente(docenteDto);
                    break;

                case "famiglia":
                    FamigliaDto famigliaDto = new FamigliaDto();
                    famigliaDto.setNome(nome);
                    famigliaDto.setCognome(cognome);
                    famigliaDto.setEmail(email);
                    famigliaDto.setPassword(password);

                    usersService.registraFamiglia(famigliaDto);
                    break;

                case "studente":
                    model.addAttribute("error", "Errore: Gli studenti devono essere registrati da un genitore tramite l'area riservata.");
                    return "registrazione";

                default:
                    throw new IllegalArgumentException("Ruolo non valido selezionato.");
            }

            // Successo: reindirizza al login con parametro per mostrare alert
            return "redirect:/login?success=true";

        } catch (Exception e) {
            // Errore: ricarica la pagina mostrando l'errore
            model.addAttribute("materie", activitiesService.getAllMaterie());
            model.addAttribute("error", "Errore durante la registrazione: " + e.getMessage());
            model.addAttribute("ruolo", ruolo);
            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("email", email);
            if (ruolo.equalsIgnoreCase("docente")) {
                model.addAttribute("materia", materie);
            }
            return "registrazione";
        }
    }

    @GetMapping("/admin/lista-utenti")
    public String showListaUtenti(Model model) {
        List<UtenteRegistrato> utenti = usersService.getAllUtenti();
        model.addAttribute("utenti", utenti);
        return "admin/lista-utenti";
    }

    @GetMapping("/famiglia/registrazione-studente")
    public String showStudenteForm(Model model) {
        model.addAttribute("studenteDto", new StudenteDto());
        return "famiglia/registrazione-studente";
    }

    @PostMapping("/famiglia/registrazione-studente")
    public String createStudente(@ModelAttribute StudenteDto studenteDto, Principal principal, Model model) {
        try {
            String emailFamiglia = principal.getName();

            // Riceviamo l'oggetto con email e password in chiaro
            var result = usersService.creaStudente(studenteDto, emailFamiglia);

            model.addAttribute("success", "Studente creato con successo!");
            model.addAttribute("emailGenerata", result.email());
            model.addAttribute("passwordGenerata", result.password());

            // Puliamo il DTO per svuotare i campi del form dopo il successo
            model.addAttribute("studenteDto", new StudenteDto());

            return "famiglia/registrazione-studente"; // Ritorna alla stessa pagina per mostrare il box successo

        } catch (Exception e) {
            model.addAttribute("error", "Errore: " + e.getMessage());
            model.addAttribute("studenteDto", studenteDto); // Mantiene i dati inseriti nel form
            return "famiglia/registrazione-studente";
        }
    }

    @GetMapping("/studente/calendario-attivita")
    public String mostraAttivitaStudente() {
        return "studente/calendario-attivita";
    }

}