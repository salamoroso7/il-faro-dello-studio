package it.unisa.ilfarodellostudio.payments.repository;

import it.unisa.ilfarodellostudio.payments.entity.Effettua;
import it.unisa.ilfarodellostudio.payments.entity.EffettuaId;
import it.unisa.ilfarodellostudio.payments.entity.StatoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository per la gestione dell'associazione Effettua (Famiglia-Pagamento).
 */
public interface EffettuaRepository extends JpaRepository<Effettua, EffettuaId> {
    /**
     * Trova tutti i pagamenti di una specifica famiglia tramite la sua email.
     *
     * @param emailFamiglia email della famiglia
     * @return lista di pagamenti (Effettua)
     */
    List<Effettua> findByIdEmailFamiglia(String emailFamiglia);

    /**
     * Trova tutte le famiglie associate a un determinato pagamento.
     *
     * @param idPagamento ID del pagamento
     * @return lista di associazioni
     */
    List<Effettua> findByIdIdPagamento(Long idPagamento);

    /**
     * Trova i pagamenti in base allo stato.
     *
     * @param stato lo stato richiesto (es. SCADUTO)
     * @return lista di pagamenti con quello stato
     */
    List<Effettua> findByStato(StatoPagamento stato);

    /**
     * Trova i pagamenti di una famiglia filtrati per stato.
     *
     * @param emailFamiglia email della famiglia
     * @param stato stato richiesto
     * @return lista di pagamenti
     */
    List<Effettua> findByIdEmailFamigliaAndStato(String emailFamiglia, StatoPagamento stato);
}
