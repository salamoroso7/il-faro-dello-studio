package it.unisa.ilfarodellostudio.gestione_attivita.dao;


import it.unisa.ilfarodellostudio.gestione_attivita.dao.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per la gestione delle materie.
 */
@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {
    /**
     * Trova una materia per nome.
     *
     * @param nome il nome della materia
     * @return un Optional contenente la materia se trovata
     */
    Optional<Materia> findByNome(String nome);
}

