package it.unisa.ilfarodellostudio.activities;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.AttivitaRepository;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.users.repository.StudenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Autowired
    private FamigliaRepository famigliaRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    // Test Case TC_GA_6_1
    @Test
    void testIscrizionePostiMassimi() {
        Materia materia = new Materia();
        materia.setNome("Matematica");
        materiaRepository.save(materia);

        Famiglia famiglia = new Famiglia();
        famiglia.setEmail("famiglia@test.com");
        famiglia.setNome("Famiglia");
        famiglia.setCognome("Test");
        famiglia.setUsername("famigliaTest");
        famiglia.setPassword("password");
        famigliaRepository.save(famiglia);

        Attivita attivita = new Attivita();
        attivita.setTitolo("Ripetizione di Algebra");
        attivita.setData(LocalDate.of(2026, 1, 15));
        attivita.setOraInizio(LocalTime.of(16, 0));
        attivita.setMateria(materia);
        attivitaRepository.save(attivita);

        for (int i=0; i < Attivita.MAX_POSTI ; i++) {
            Studente studente = new Studente();
            studente.setNome("Nome" + i);
            studente.setCognome("Cognome" + i);
            studente.setUsername("Username" + i);
            studente.setPassword("Password" + i);
            studente.setEmail("Email" + i + "@test.com");
            studente.setFamiglia(famiglia);
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
        studenteExtra.setFamiglia(famiglia);
        studenteExtra = studenteRepository.save(studenteExtra);

        Long idAttivita = attivita.getIdAttivita();
        String emailStudenteExtra = studenteExtra.getEmail();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            activitiesService.iscriviStudenteAdAttivita(emailStudenteExtra, idAttivita);
        });

        String messaggioAtteso = "Impossibile iscriversi: l'attività " + attivita.getTitolo() + " è già al completo";
        assertEquals(messaggioAtteso, exception.getMessage());
    }
}
