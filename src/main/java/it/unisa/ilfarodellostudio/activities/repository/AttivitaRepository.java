package it.unisa.ilfarodellostudio.activities.repository;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AttivitaRepository extends JpaRepository<Attivita, Long> {
    // 2. Controllo Sovrapposizione per CREAZIONE (Nuova attività)
    @Query("SELECT COUNT(a) > 0 FROM Attivita a " +
            "WHERE a.docente = :docente " +
            "AND a.data = :data " +
            "AND (a.oraInizio < :oraFine AND a.oraFine > :oraInizio)")
    boolean existsOverlappingLesson(@Param("docente") Docente docente,
                                    @Param("data") LocalDate data,
                                    @Param("oraInizio") LocalTime oraInizio,
                                    @Param("oraFine") LocalTime oraFine);

    // 3. Controllo Sovrapposizione per MODIFICA (Esclude l'ID corrente)
    @Query("SELECT COUNT(a) > 0 FROM Attivita a " +
            "WHERE a.docente = :docente " +
            "AND a.data = :data " +
            "AND (a.oraInizio < :oraFine AND a.oraFine > :oraInizio) "
    )
    boolean existsOverlappingLessonExcludingId(@Param("docente") Docente docente,
                                               @Param("data") LocalDate data,
                                               @Param("oraInizio") LocalTime oraInizio,
                                               @Param("oraFine") LocalTime oraFine,
                                               @Param("escludiId") Long escludiId);

     List<Attivita> findAllByDocenteAndDataAfter(Docente docente, LocalDate data);
}