package it.unisa.ilfarodellostudio.users.repository;

import it.unisa.ilfarodellostudio.users.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, String> {
    Optional<Docente> findByEmail(String email);
    long countByIsAttivoFalse();
}