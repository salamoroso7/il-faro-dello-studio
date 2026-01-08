package it.unisa.ilfarodellostudio.autenticazione.service;

import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Docente;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Famiglia;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Studente;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.UtenteRegistrato;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servizio di autenticazione personalizzato che implementa {@link UserDetailsService}.
 * <p>
 * Questa classe funge da ponte tra il database e Spring Security. Poiché il sistema
 * prevede tre diverse tipologie di utenti (Docente, Studente, Famiglia) salvati in
 * tabelle diverse, il servizio si occupa di centralizzare la ricerca dell'utente
 * durante la fase di login.
 * </p>
 */
@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private FamigliaRepository famigliaRepository;

    /**
     * Recupera le credenziali dell'utente partendo dal suo username.
     * <p>
     * Questo metodo viene richiamato automaticamente da Spring Security durante il login.
     * Effettua una ricerca sequenziale su tutti i repository disponibili. Se l'utente
     * viene trovato, viene convertito in un oggetto {@link UserDetails} compatibile
     * con il framework.
     * </p>
     * <p>
     * <strong>Validazione dello stato dell'account:</strong> Se l'utente è stato disattivato
     * (isAttivo = false), il login viene impedito lanciando una {@link DisabledException}.
     * </p>
     *
     * @param username Lo username inserito nel form di login.
     * @return Un oggetto {@link UserDetails} che contiene username, password e ruoli dell'utente.
     * @throws UsernameNotFoundException Se lo username non è presente in nessuna delle tre repository.
     * @throws DisabledException Se l'account è stato disattivato dall'amministratore.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cerca l'entità utente navigando tra le diverse tipologie di repository
        Optional<? extends UtenteRegistrato> utente = cercaInTutteLeRepo(username);

        // Se l'utente non esiste in nessuna tabella, lancia l'eccezione di sicurezza
        UtenteRegistrato u = utente.orElseThrow(() ->
                new UsernameNotFoundException("Utente non trovato con: " + username));

        // Verifica che l'account sia attivo
        if (!u.isAttivo()) {
            throw new DisabledException("Account disattivato. Contattare l'amministratore.");
        }

        // Costruisce l'oggetto User di Spring Security mappando i dati della nostra entità
        return User.builder()
                .username(u.getEmail()) // Utilizzato come identificativo principale della sessione
                .password(u.getPassword()) // La password (già criptata) per il confronto
                .roles(getRuolo(u))        // Il ruolo dinamico (DOCENTE, STUDENTE o FAMIGLIA)
                .build();
    }

    /**
     * Esegue una ricerca polimorfica dello username all'interno delle repository dei docenti,
     * delle famiglie e degli studenti.
     *
     * @param email L'email da ricercare.
     * @return Un {@link Optional} contenente l'utente trovato, oppure vuoto se non trovato.
     */
    private Optional<? extends UtenteRegistrato> cercaInTutteLeRepo(String email) {
        // 1. Priorità di ricerca: Docente
        Optional<Docente> d = docenteRepository.findByEmail(email);
        if (d.isPresent()) return d;

        // 2. Seconda scelta: Famiglia
        Optional<Famiglia> f = famigliaRepository.findByEmail(email);
        if (f.isPresent()) return f;

        // 3. Terza scelta: Studente (username generato automaticamente)
        return studenteRepository.findByEmail(email);
    }

    /**
     * Determina il ruolo di sicurezza basandosi sulla classe effettiva dell'oggetto utente.
     * <p>
     * Utilizza l'operatore {@code instanceof} per identificare a quale sottoclasse
     * appartiene l'entità che estende {@link UtenteRegistrato}.
     * </p>
     *
     * @param u L'utente trovato nel database.
     * @return Una stringa rappresentante il ruolo (es. "DOCENTE").
     */
    private String getRuolo(UtenteRegistrato u) {
        if (u.isAdmin()) return "ADMIN";
        if (u instanceof Docente) return "DOCENTE";
        if (u instanceof Studente) return "STUDENTE";
        if (u instanceof Famiglia) return "FAMIGLIA";
        return "USER";
    }
}