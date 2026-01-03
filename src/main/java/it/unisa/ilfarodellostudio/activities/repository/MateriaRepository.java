package it.unisa.ilfarodellostudio.activities.repository;


import it.unisa.ilfarodellostudio.activities.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {
    Optional<Materia> findByNome(String nome);
}

