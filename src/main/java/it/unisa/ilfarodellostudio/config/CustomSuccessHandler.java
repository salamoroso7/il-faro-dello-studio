package it.unisa.ilfarodellostudio.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Gestore personalizzato per il successo dell'autenticazione.
 * Reindirizza l'utente alla dashboard corretta in base al suo ruolo (ADMIN, DOCENTE, FAMIGLIA, STUDENTE).
 */
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Metodo invocato quando l'autenticazione ha successo.
     * Determina il ruolo dell'utente e reindirizza alla URL corrispondente.
     *
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @param authentication l'oggetto autenticazione contenente i dettagli dell'utente e i suoi ruoli
     * @throws IOException se si verifica un errore di input/output durante il redirect
     * @throws ServletException se si verifica un errore servlet
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard-admin");
        } else if (roles.contains("ROLE_DOCENTE")) {
            response.sendRedirect("/docente/dashboard-docente");
        } else if (roles.contains("ROLE_FAMIGLIA")) {
            response.sendRedirect("/famiglia/dashboard-famiglia");
        } else {
            response.sendRedirect("/studente/dashboard-studente");
        }
    }
}
