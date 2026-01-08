package it.unisa.ilfarodellostudio.gestione_utenze.dao.repository;

import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Studente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per la gestione degli Studenti.
 */
@Repository
public interface StudenteRepository extends JpaRepository<Studente, String> {
    Optional<Studente> findByEmail(String email);
    long countByIsAttivoFalse();
    boolean existsByCodiceFiscale(String codiceFiscale);
}