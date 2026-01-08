package it.unisa.ilfarodellostudio.gestione_pagamenti.dao.repository;

import it.unisa.ilfarodellostudio.gestione_pagamenti.dao.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per la gestione dei pagamenti (tasse).
 */
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
