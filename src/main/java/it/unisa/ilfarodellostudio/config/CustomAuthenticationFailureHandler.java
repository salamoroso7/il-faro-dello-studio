package it.unisa.ilfarodellostudio.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Gestore personalizzato per i fallimenti di autenticazione.
 * Distingue tra account disabilitati e credenziali errate, mostrando messaggi appropriati.
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Metodo invocato quando l'autenticazione fallisce.
     * Reindirizza alla pagina di login con un parametro di errore specifico.
     *
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @param exception l'eccezione di autenticazione che ha causato il fallimento
     * @throws IOException se si verifica un errore di input/output durante il redirect
     * @throws ServletException se si verifica un errore servlet
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage;
        
        // Verifica se l'errore è dovuto a un account disabilitato
        // Controlla sia l'eccezione stessa che la sua causa, poiché Spring Security
        // potrebbe wrappare la DisabledException in un'altra eccezione
        if (isDisabledException(exception)) {
            errorMessage = "disabled";
        } else {
            // Errore generico (credenziali errate, utente non trovato, ecc.)
            errorMessage = "invalid";
        }
        
        // Reindirizza alla pagina di login con il parametro di errore appropriato
        response.sendRedirect("/login?error=" + errorMessage);
    }
    
    /**
     * Verifica se l'eccezione o una delle sue cause è una DisabledException.
     * 
     * @param exception l'eccezione da verificare
     * @return true se l'eccezione o una delle sue cause è DisabledException
     */
    private boolean isDisabledException(Throwable exception) {
        if (exception == null) {
            return false;
        }
        
        if (exception instanceof DisabledException) {
            return true;
        }
        
        // Verifica ricorsivamente la causa
        return isDisabledException(exception.getCause());
    }
}
