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

/**
 * Repository per la gestione della persistenza delle attività.
 */
@Repository
public interface AttivitaRepository extends JpaRepository<Attivita, Long> {
    /**
     * Verifica se esiste una lezione sovrapposta per lo stesso docente.
     *
     * @param docente il docente
     * @param data la data
     * @param oraInizio ora inizio
     * @param oraFine ora fine
     * @return true se esiste sovrapposizione
     */
    @Query("SELECT COUNT(a) > 0 FROM Attivita a " +
            "WHERE a.docente = :docente " +
            "AND a.data = :data " +
            "AND (a.oraInizio < :oraFine AND a.oraFine > :oraInizio)")
    boolean existsOverlappingLesson(@Param("docente") Docente docente,
                                    @Param("data") LocalDate data,
                                    @Param("oraInizio") LocalTime oraInizio,
                                    @Param("oraFine") LocalTime oraFine);

    /**
     * Verifica sovrapposizione escludendo un ID specifico (per modifiche).
     *
     * @param docente docente
     * @param data data
     * @param oraInizio ora inizio
     * @param oraFine ora fine
     * @param escludiId ID da escludere
     * @return true se esiste sovrapposizione
     */
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

    /**
     * Trova tutte le attività di un docente successive a una certa data.
     *
     * @param docente il docente
     * @param data la data di partenza
     * @return lista di attività
     */
     List<Attivita> findAllByDocenteAndDataAfter(Docente docente, LocalDate data);


    @Query("SELECT a FROM Attivita a JOIN a.iscritti s " +
            "WHERE s.email = :email " +
            "AND (a.data > :oggi OR (a.data = :oggi AND a.oraInizio > :ora)) " +
            "ORDER BY a.data ASC, a.oraInizio ASC")
    List<Attivita> findUpcomingByStudenteEmail(@Param("email") String email,
                                               @Param("oggi") LocalDate oggi,
                                               @Param("ora") LocalTime ora);


    @Query("SELECT a FROM Attivita a " +
            "WHERE (a.data > :oggi OR (a.data = :oggi AND a.oraInizio > :ora)) " +
            "AND NOT EXISTS (SELECT s FROM a.iscritti s WHERE s.email = :emailStudente) " +
            "ORDER BY a.data ASC, a.oraInizio ASC")
    List<Attivita> findDisponibiliUpcoming(@Param("emailStudente") String email,
                                           @Param("oggi") LocalDate oggi,
                                           @Param("ora") LocalTime ora);
}