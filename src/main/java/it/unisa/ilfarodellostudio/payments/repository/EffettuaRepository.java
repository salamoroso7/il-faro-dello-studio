package it.unisa.ilfarodellostudio.payments.repository;

import it.unisa.ilfarodellostudio.payments.entity.Effettua;
import it.unisa.ilfarodellostudio.payments.entity.EffettuaId;
import it.unisa.ilfarodellostudio.payments.entity.StatoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EffettuaRepository extends JpaRepository<Effettua, EffettuaId> {
    // Trova tutti i pagamenti di una specifica famiglia tramite la sua email
    List<Effettua> findByIdEmailFamiglia(String emailFamiglia);

    // Trova tutte le famiglie associate a un determinato pagamento
    List<Effettua> findByIdIdPagamento(Long idPagamento);

    // Trova i pagamenti in base allo stato (es. tutti i "NON_EFFETTUATO")
    List<Effettua> findByStato(StatoPagamento stato);

    // Trova i pagamenti di una famiglia filtrati per stato
    List<Effettua> findByIdEmailFamigliaAndStato(String emailFamiglia, StatoPagamento stato);
}
