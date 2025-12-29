package it.unisa.ilfarodellostudio.auth;

import org.springframework.stereotype.Controller;
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
}
