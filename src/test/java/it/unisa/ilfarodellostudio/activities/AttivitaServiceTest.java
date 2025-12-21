package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.users.Studente;
import it.unisa.ilfarodellostudio.users.StudenteDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class AttivitaServiceTest {

    @Autowired
    private AttivitaService attivitaService;

    @Autowired
    private AttivitaDAO attivitaDAO;

    @Autowired
    private StudenteDAO studenteDAO;

    // Test Case TC_GA_6_1
    @Test
    void testIscrizionePostiMassimi() {
        Attivita attivita = new Attivita("Ripetizione di Algebra", LocalDateTime.now());
        attivitaDAO.save(attivita);
        for (int i=0; i < Attivita.MAX_POSTI ; i++) {
            Studente studente = new Studente();
            studente.setNome("Nome" + i);
            studente.setCognome("Cognome" + i);
            studente.setUsername("Username" + i);
            studente.setPassword("Password" + i);
            studente.setEmail("Email" + i + "@test.com");
            studenteDAO.save(studente);
            attivita.aggiungiStudente(studente);
        }
        attivitaDAO.save(attivita);

        Studente studenteExtra = new Studente();
        studenteExtra.setNome("Mario");
        studenteExtra.setCognome("Rossi");
        studenteExtra.setUsername("mariorossi");
        studenteExtra.setEmail("mario@test.com");
        studenteExtra.setPassword("pass");
        studenteExtra = studenteDAO.save(studenteExtra);

        Long idAttivita = attivita.getId();
        Long idStudenteExtra = studenteExtra.getId();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attivitaService.iscriviStudenteAdAttivita(idStudenteExtra, idAttivita);
        });

        String messaggioAtteso = "Impossibile iscriversi: l'attività " + attivita.getNome() + " è già al completo";
        assertEquals(messaggioAtteso, exception.getMessage());
    }
}
