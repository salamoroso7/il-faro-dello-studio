package it.unisa.ilfarodellostudio.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe di configurazione per la sicurezza di Spring Security.
 * Definisce le regole di autorizzazione, il form di login, il logout e la gestione delle password.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler successHandler;

    /**
     * Definisce la catena di filtri di sicurezza.
     * Configura le regole di accesso URL-based, il login form e il logout.
     *
     * @param http l'oggetto HttpSecurity per configurare la sicurezza web
     * @return la catena di filtri costruita
     * @throws Exception se si verifica un errore durante la configurazione
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/login", "/register", "/css/**", "/js/**").permitAll() // Accesso libero
                        .requestMatchers("/docente/**").hasRole("DOCENTE")
                        .requestMatchers("/famiglia/**").hasRole("FAMIGLIA")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // Tutto il resto richiede login
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Spring intercetta il POST qui
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true") // Se sbaglia credenziali
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Definisce il bean per la codifica delle password.
     * Utilizza BCrypt, un algoritmo di hashing sicuro.
     *
     * @return un'istanza di BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}