package it.unisa.ilfarodellostudio.activities.repository;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttivitaRepository extends JpaRepository<Attivita, Long> {
}
