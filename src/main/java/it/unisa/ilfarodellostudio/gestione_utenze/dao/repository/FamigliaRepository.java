package it.unisa.ilfarodellostudio.gestione_utenze.dao.repository;

import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Famiglia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per la gestione delle Famiglie.
 */
@Repository
public interface FamigliaRepository extends JpaRepository<Famiglia, String> {
    Optional<Famiglia> findByEmail(String email);
    long countByIsAttivoFalse();
}