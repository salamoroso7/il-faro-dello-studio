package it.unisa.ilfarodellostudio.activities.repository;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface AttivitaRepository extends JpaRepository<Attivita, Long> {
    // FORSE DA AGGIUNGERE PER CONTROLLO DI PIU LEZIONI STESSO MOMENTO DOCENTI
   // boolean existsByDataAndOraInizio(LocalDate data, LocalTime oraInizio);
}
