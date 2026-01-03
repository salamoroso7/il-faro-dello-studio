package it.unisa.ilfarodellostudio.payments.repository;

import it.unisa.ilfarodellostudio.payments.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per la gestione dei pagamenti (tasse).
 */
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
