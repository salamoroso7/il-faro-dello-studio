package it.unisa.ilfarodellostudio.users;

import it.unisa.ilfarodellostudio.users.dto.DocenteDto;
import it.unisa.ilfarodellostudio.users.dto.FamigliaDto;
import it.unisa.ilfarodellostudio.users.dto.StudenteDto;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PasswordEncoder passwordEncoder;

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
        docente.setUsername(dto.getUsername());
        docente.setPassword(passwordEncoder.encode(dto.getPassword()));

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
        famiglia.setUsername(dto.getUsername());
        famiglia.setPassword(passwordEncoder.encode(dto.getPassword()));

        famigliaRepository.save(famiglia);
    }

    /**
     * Esegue la registrazione dello studente effettuata dalla famiglia.
     * Genera automaticamente Username e Password (poiché assenti nel StudenteDto).
     */
    @Transactional
    public String creaStudente(StudenteDto dto, String emailFamiglia) {
        // Genera username univoco (es. matricola)
        String generatedUsername = generaUsername();

        // Genera una password provvisoria (es. basata sul nome o random)
        String rawPassword = "Pass" + new Random().nextInt(10000);

        // L'email è l'ID (@Id) in UtenteRegistrato.
        // Poiché StudenteDto non ha email, ne generiamo una fittizia o usiamo lo username come email interna
        // Attenzione: UtenteRegistrato richiede email come ID.
        String generatedEmail = generatedUsername.toLowerCase() + "@studenti.ilfaro.it";

        Famiglia famiglia = famigliaRepository.findById(emailFamiglia)
                .orElseThrow(() -> new RuntimeException("Famiglia non trovata o sessione scaduta."));

        Studente studente = new Studente();
        studente.setNome(dto.getNome());
        studente.setCognome(dto.getCognome());
        studente.setEmail(generatedEmail);
        studente.setUsername(generatedUsername);
        studente.setPassword(passwordEncoder.encode(rawPassword));
        studente.setFamiglia(famiglia);

        famiglia.getStudenti().add(studente);

        studenteRepository.save(studente);

        return generatedUsername;
    }

    /**
     * Genera uno username nel formato S-XXXXX
     */
    private String generaUsername() {
        return "S-" + (10000 + new Random().nextInt(90000));
    }
}