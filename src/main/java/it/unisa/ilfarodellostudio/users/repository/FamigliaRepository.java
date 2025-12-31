package it.unisa.ilfarodellostudio.users.repository;

import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamigliaRepository extends JpaRepository<Famiglia, String> {
    Optional<Famiglia> findByEmail(String email);
}