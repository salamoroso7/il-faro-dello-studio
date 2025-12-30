package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.users.Studente;
import it.unisa.ilfarodellostudio.users.StudenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class IscrizioneAttivitaTest {

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private AttivitaRepository attivitaRepository;

    @Autowired
    private StudenteRepository studenteRepository;

    // Test Case TC_GA_6_1
    @Test
    void testIscrizionePostiMassimi() {
        Attivita attivita = new Attivita("Ripetizione di Algebra", LocalDateTime.now());
        attivitaRepository.save(attivita);
        for (int i=0; i < Attivita.MAX_POSTI ; i++) {
            Studente studente = new Studente();
            studente.setNome("Nome" + i);
            studente.setCognome("Cognome" + i);
            studente.setUsername("Username" + i);
            studente.setPassword("Password" + i);
            studente.setEmail("Email" + i + "@test.com");
            studenteRepository.save(studente);
            attivita.aggiungiStudente(studente);
        }
        attivitaRepository.save(attivita);

        Studente studenteExtra = new Studente();
        studenteExtra.setNome("Mario");
        studenteExtra.setCognome("Rossi");
        studenteExtra.setUsername("mariorossi");
        studenteExtra.setEmail("mario@test.com");
        studenteExtra.setPassword("pass");
        studenteExtra = studenteRepository.save(studenteExtra);

        Long idAttivita = attivita.getId();
        Long idStudenteExtra = studenteExtra.getId();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.iscriviStudenteAdAttivita(idStudenteExtra, idAttivita);
        });

        String messaggioAtteso = "Impossibile iscriversi: l'attività " + attivita.getNome() + " è già al completo";
        assertEquals(messaggioAtteso, exception.getMessage());
    }
}
