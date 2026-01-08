package it.unisa.ilfarodellostudio.gestione_utenze;

import it.unisa.ilfarodellostudio.gestione_utenze.controller.dto.StudenteDto;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Famiglia;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.repository.FamigliaRepository;
import it.unisa.ilfarodellostudio.gestione_utenze.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RegistrazioneStudenteTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private FamigliaRepository famigliaRepository;

    private Famiglia creaFamiglia(String email) {
        Famiglia f = new Famiglia();
        f.setEmail(email);
        f.setNome("NomeFamiglia");
        f.setCognome("CognomeFamiglia");
        f.setPassword("password");
        return famigliaRepository.save(f);
    }

    @Test
    @DisplayName("TC_GU_1_1: Nome troppo lungo")
    void testNomeTroppoLungo() {
        Famiglia famiglia = creaFamiglia("famiglia@test.com");

        StudenteDto dto = new StudenteDto();
        dto.setNome("A".repeat(51)); // Stringa di 51 caratteri

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usersService.creaStudente(dto, famiglia.getEmail());
        });

        assertEquals("Il Nome supera i 50 caratteri", exception.getMessage());
    }

    @Test
    @DisplayName("TC_GU_1_2: Cognome troppo lungo")
    void testCognomeTroppoLungo() {
        Famiglia famiglia = creaFamiglia("famiglia@test.com");

        StudenteDto dto = new StudenteDto();
        dto.setNome("Mario");
        dto.setCognome("A".repeat(51)); // Stringa di 51 caratteri

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usersService.creaStudente(dto, famiglia.getEmail());
        });

        assertEquals("Il Cognome supera i 50 caratteri", exception.getMessage());
    }

    @Test
    @DisplayName("TC_GU_1_3: Codice Fiscale < 16 cifre")
    void testCodiceFiscaleLungo() {
        Famiglia famiglia = creaFamiglia("famiglia@test.com");

        StudenteDto dto = new StudenteDto();
        dto.setNome("Mario");
        dto.setCognome("Rossi");
        dto.setCodiceFiscale("AAAAAAAAAAAAAAAAA"); // 17 cifre

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usersService.creaStudente(dto, famiglia.getEmail());
        });

        assertTrue(exception.getMessage().contains("superiore alle 16 cifre") ||
                exception.getMessage().contains("deve essere di 16 cifre"));
    }

    @Test
    @DisplayName("TC_GU_1_4: Codice Fiscale > 16 cifre")
    void testCodiceFiscaleCorto() {
        Famiglia famiglia = creaFamiglia("famiglia@test.com");

        StudenteDto dto = new StudenteDto();
        dto.setNome("Mario");
        dto.setCognome("Rossi");
        dto.setCodiceFiscale("AAAAAAAAAAAAAAA"); // 15 cifre

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usersService.creaStudente(dto, famiglia.getEmail());
        });

        assertTrue(exception.getMessage().contains("inferiore alle 16 cifre") ||
                exception.getMessage().contains("deve essere di 16 cifre"));
    }

    @Test
    @DisplayName("TC_GU_1_5: Data di nascita futura")
    void testDataNascitaFutura() {
        Famiglia famiglia = creaFamiglia("famiglia@test.com");

        StudenteDto dto = new StudenteDto();
        dto.setNome("Mario");
        dto.setCognome("Rossi");
        dto.setCodiceFiscale("AAAAAAAAAAAAAAAA");
        dto.setDataNascita(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> {
            usersService.creaStudente(dto, famiglia.getEmail());
        });
    }

    @Test
    @DisplayName("TC_GU_1_6: Registrazione corretta")
    void testRegistrazioneSuccesso() {
        Famiglia famiglia = creaFamiglia("famiglia@test.com");

        StudenteDto dto = new StudenteDto();
        dto.setNome("Mario");
        dto.setCognome("Rossi");
        dto.setCodiceFiscale("AAAAAAAAAAAAAAAA");
        dto.setDataNascita(LocalDate.of(2009, 12, 5));

        UsersService.RegistrazioneResult result = usersService.creaStudente(dto, famiglia.getEmail());

        assertNotNull(result, "Il risultato della registrazione non dovrebbe essere null");
        assertNotNull(result.email(), "L'email generata non dovrebbe essere null");
        assertNotNull(result.password(), "La password generata non dovrebbe essere null");

        assertTrue(result.email().endsWith("@studenti.ilfaro.it"));

        assertTrue(result.password().startsWith("Pass"));
    }
}
