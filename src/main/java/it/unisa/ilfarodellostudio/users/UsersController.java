package it.unisa.ilfarodellostudio.users;

import it.unisa.ilfarodellostudio.users.dto.DocenteDto;
import it.unisa.ilfarodellostudio.users.dto.FamigliaDto;
import it.unisa.ilfarodellostudio.users.dto.StudenteDto;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private FamigliaRepository famigliaRepository;

    /* =========================================================================
       1. REGISTRAZIONE PUBBLICA UNIFICATA (Docenti e Famiglie)
       Usa la pagina 'registrazione.html' che abbiamo creato.
       ========================================================================= */

    // Mostra il form unico di registrazione
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registrazione"; // Corrisponde al file registrazione.html
    }

    // Gestisce l'invio del form unico
    @PostMapping("/register")
    public String handleRegistration(
            @RequestParam String ruolo, // Campo <select name="ruolo">
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String materia, // Solo per Docenti
            Model model) {

        try {
            switch (ruolo.toLowerCase()) {
                case "docente":
                    DocenteDto docenteDto = new DocenteDto();
                    docenteDto.setNome(nome);
                    docenteDto.setCognome(cognome);
                    docenteDto.setEmail(email);
                    docenteDto.setPassword(password);
                    docenteDto.setMateria(materia);
                    // docenteDto.setUsername(email); // Se necessario

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
            model.addAttribute("error", "Errore durante la registrazione: " + e.getMessage());
            return "registrazione";
        }
    }

    /* =========================================================================
       2. CREAZIONE STUDENTE (Area Riservata Famiglia)
       Usa la pagina 'crea_studente.html' accessibile solo dopo il login.
       ========================================================================= */

    @GetMapping("/users/crea-studente")
    public String showStudenteForm(Model model) {
        model.addAttribute("studenteDto", new StudenteDto());
        return "crea_studente"; // Corrisponde al file crea_studente.html
    }

    @PostMapping("/users/crea-studente")
    public String createStudente(@ModelAttribute StudenteDto studenteDto, Principal principal, Model model) {
        try {
            // 1. Recupera l'utente loggato (Famiglia)
            String emailFamiglia = principal.getName();

            // 2. Crea lo studente associandolo alla famiglia
            String usernameGenerato = usersService.creaStudente(studenteDto, emailFamiglia);

            model.addAttribute("success", "Studente creato con successo! Username: " + usernameGenerato);
            model.addAttribute("studenteDto", new StudenteDto()); // Reset del form
            return "crea_studente";

        } catch (Exception e) {
            model.addAttribute("error", "Errore creazione studente: " + e.getMessage());
            return "crea_studente";
        }
    }
}