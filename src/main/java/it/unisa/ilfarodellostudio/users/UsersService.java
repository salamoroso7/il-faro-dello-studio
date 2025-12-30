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

        // Utilizzo del SuperBuilder grazie a lombok @SuperBuilder
        Docente docente = Docente.builder()
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(dto.getEmail()) // ID della classe padre
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                // Nota: se l'entity Docente non ha il campo 'materia',
                // questo dato del DTO non può essere salvato qui a meno di non estendere l'Entity.
                .build();

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

        Famiglia famiglia = Famiglia.builder()
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(dto.getEmail()) // ID
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        famigliaRepository.save(famiglia);
    }

    /**
     * Esegue la registrazione dello studente effettuata dalla famiglia.
     * Genera automaticamente Username e Password (poiché assenti nel StudenteDto).
     */
    @Transactional
    public String creaStudente(StudenteDto dto, Famiglia famiglia) {
        // Genera username univoco (es. matricola)
        String generatedUsername = generaUsername();

        // Genera una password provvisoria (es. basata sul nome o random)
        String rawPassword = "Pass" + new Random().nextInt(10000);

        // L'email è l'ID (@Id) in UtenteRegistrato.
        // Poiché StudenteDto non ha email, ne generiamo una fittizia o usiamo lo username come email interna
        // Attenzione: UtenteRegistrato richiede email come ID.
        String generatedEmail = generatedUsername.toLowerCase() + "@studenti.ilfaro.it";

        Studente studente = Studente.builder()
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(generatedEmail) // ID obbligatorio
                .username(generatedUsername)
                .password(passwordEncoder.encode(rawPassword))
                // .famiglia(famiglia) // Da scommentare se aggiungeremo la relazione @ManyToOne in Studente
                .build();

        // Se  aggiungeremo la relazione bidirezionale in Famiglia, potresti dover fare:
        // famiglia.getStudenti().add(studente);

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