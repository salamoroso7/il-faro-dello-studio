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

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

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
