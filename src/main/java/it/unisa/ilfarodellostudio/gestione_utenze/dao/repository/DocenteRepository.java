package it.unisa.ilfarodellostudio.gestione_utenze.dao.repository;

import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per la gestione dei Docenti.
 */
@Repository
public interface DocenteRepository extends JpaRepository<Docente, String> {
    Optional<Docente> findByEmail(String email);
    long countByIsAttivoFalse();
}