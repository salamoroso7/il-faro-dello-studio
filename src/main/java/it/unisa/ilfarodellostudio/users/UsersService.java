package it.unisa.ilfarodellostudio.users;

import it.unisa.ilfarodellostudio.users.dto.DocenteDto;
import it.unisa.ilfarodellostudio.users.dto.FamigliaDto;
import it.unisa.ilfarodellostudio.users.dto.StudenteDto;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.repository.DocenteRepository;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
     * Esegue la registrazione del docente
     * @param dto relativo ai dati inseriti nel form
     */
    public void registraDocente(DocenteDto dto) {
        // TODO
    }

    /**
     * Esegue la registrazione della famiglia
     * @param dto relativo ai dati inseriti nel form
     */
    public void registraFamiglia(FamigliaDto dto) {
        // TODO
    }

    /**
     * Esegue la registrazione dello studente
     * effettuata dalla famiglia.
     * @param dto relativo ai dati inseriti nel form
     * @param famiglia entità associata alla famiglia loggata
     * @return la stringa dello username generata
     */
    public String creaStudente(StudenteDto dto, Famiglia famiglia) {
        // TODO
        return "username";
    }

    /**
     * Funzione per generare lo username dello studente
     * @return la stringa generata
     */
    private String generaUsername() {
        // TODO
        return "username";
    }
}
