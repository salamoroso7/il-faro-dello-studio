package it.unisa.ilfarodellostudio.users;

import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.users.dto.DocenteDto;
import it.unisa.ilfarodellostudio.users.dto.FamigliaDto;
import it.unisa.ilfarodellostudio.users.dto.StudenteDto;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.entity.UtenteRegistrato;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Classe Service che fornisce la logica di business
 * per la creazione dei profili utente.
 */
@Service
public class UsersService {

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private FamigliaRepository famigliaRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Recupera tutti gli utenti registrati (Docenti, Famiglie e Studenti)
     * e li unisce in un'unica lista.
     */
    public List<UtenteRegistrato> getAllUtenti() {
        List<UtenteRegistrato> tuttiGliUtenti = new ArrayList<>();

        // Recuperiamo e aggiungiamo i docenti
        tuttiGliUtenti.addAll(docenteRepository.findAll());

        // Recuperiamo e aggiungiamo le famiglie
        tuttiGliUtenti.addAll(famigliaRepository.findAll());

        // Recuperiamo e aggiungiamo gli studenti
        tuttiGliUtenti.addAll(studenteRepository.findAll());

        return tuttiGliUtenti;
    }

    public Optional<Docente> cercaDocente(String email) {
        return docenteRepository.findByEmail(email);
    }

    /**
     * Esegue la registrazione del docente.
     * Mappa i dati dal DTO all'Entity Docente e cifra la password.
     */
    @Transactional
    public void registraDocente(DocenteDto dto) {
        if (docenteRepository.existsById(dto.getEmail())) {
            throw new IllegalArgumentException("Email già esistente");
        }

        Docente docente = new Docente();
        docente.setNome(dto.getNome());
        docente.setCognome(dto.getCognome());
        docente.setEmail(dto.getEmail());
        docente.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Gestione Multiselect (N-N)
        if (dto.getMaterie() != null && !dto.getMaterie().isEmpty()) {
            // Recuperiamo tutte le materie in una volta sola per efficienza
            List<Materia> materieDb = materiaRepository.findAllById(dto.getMaterie());

            // Aggiungiamo tutte le materie trovate al Set del docente
            docente.getMaterieInsegnate().addAll(materieDb);
        }

        docenteRepository.save(docente);
    }

    /**
     * Esegue la registrazione della famiglia.
     * Mappa i dati dal DTO all'Entity Famiglia e cifra la password.
     */
    @Transactional
    public void registraFamiglia(FamigliaDto dto) {
        if (famigliaRepository.existsById(dto.getEmail())) {
            throw new IllegalArgumentException("Email già esistente");
        }

        Famiglia famiglia = new Famiglia();
        famiglia.setNome(dto.getNome());
        famiglia.setCognome(dto.getCognome());
        famiglia.setEmail(dto.getEmail());
        famiglia.setPassword(passwordEncoder.encode(dto.getPassword()));

        famigliaRepository.save(famiglia);
    }

    public record RegistrazioneResult(String email, String password) {}

    /**
     * Esegue la registrazione dello studente effettuata dalla famiglia.
     * Genera automaticamente Email e Password (poiché assenti nel StudenteDto).
     */
    @Transactional
    public RegistrazioneResult creaStudente(StudenteDto dto, String emailFamiglia) {
        // Esempio di validazione nel Service
        if (dto.getNome().length() > 50) throw new IllegalArgumentException("Il Nome supera i 50 caratteri");
        if (dto.getCodiceFiscale().length() != 16) throw new IllegalArgumentException("Il Codice Fiscale deve essere di 16 cifre");
        if (!dto.getDataNascita().isBefore(LocalDate.now())) throw new IllegalArgumentException("La Data di Nascita è futura o odierna");

        Famiglia famiglia = famigliaRepository.findById(emailFamiglia)
                .orElseThrow(() -> new RuntimeException("Famiglia non trovata o sessione scaduta."));

        String generatedUsername = generaUsername(dto.getCognome());
        String generatedEmail = generatedUsername.toLowerCase() + "@studenti.ilfaro.it";

        if (studenteRepository.existsById(generatedEmail)) {
            generatedEmail = generatedUsername.toLowerCase() + new Random().nextInt(100) + "@studenti.ilfaro.it";
        }

        String rawPassword = "Pass" + new Random().nextInt(10000);

        Studente studente = new Studente();
        studente.setNome(dto.getNome());
        studente.setCognome(dto.getCognome());
        studente.setEmail(generatedEmail);
        studente.setPassword(passwordEncoder.encode(rawPassword));
        studente.setCodiceFiscale(dto.getCodiceFiscale());
        studente.setDataNascita(dto.getDataNascita());
        studente.setFamiglia(famiglia);

        studenteRepository.save(studente);

        studenteRepository.save(studente);

        return new RegistrazioneResult(generatedEmail, rawPassword);
    }

    /**
     * Genera uno username nel formato S-XXXXX
     */
    private String generaUsername(String cognome) {
        return cognome + (10000 + new Random().nextInt(90000));
    }

    // Metodo per disattivare account
}