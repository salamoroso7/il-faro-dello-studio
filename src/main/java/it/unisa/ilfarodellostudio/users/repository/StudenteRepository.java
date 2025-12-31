package it.unisa.ilfarodellostudio.users.repository;

import it.unisa.ilfarodellostudio.users.entity.Studente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudenteRepository extends JpaRepository<Studente, String> {
    Optional<Studente> findByEmail(String email);
}