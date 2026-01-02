package it.unisa.ilfarodellostudio.payments;

import it.unisa.ilfarodellostudio.payments.entity.Effettua;
import it.unisa.ilfarodellostudio.payments.entity.EffettuaId;
import it.unisa.ilfarodellostudio.payments.entity.Pagamento;
import it.unisa.ilfarodellostudio.payments.entity.StatoPagamento;
import it.unisa.ilfarodellostudio.payments.repository.EffettuaRepository;
import it.unisa.ilfarodellostudio.payments.repository.PagamentoRepository;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.repository.FamigliaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentsService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private FamigliaRepository famigliaRepository;

    @Autowired
    private EffettuaRepository effettuaRepository;

    /**
     * Crea una nuova tassa e la assegna automaticamente a TUTTE le famiglie.
     */
    @Transactional
    public Pagamento creaPagamentoGenerale(Pagamento nuovoPagamento) {
        // 1. Salvo l'entità Pagamento (la tassa comune)
        Pagamento pagamentoSalvato = pagamentoRepository.save(nuovoPagamento);

        // 2. Recupero tutte le famiglie presenti nel sistema
        List<Famiglia> tutteLeFamiglie = famigliaRepository.findAll();

        // 3. Per ogni famiglia, creo un record nella tabella di giunzione "effettua"
        List<Effettua> associazioni = tutteLeFamiglie.stream().map(famiglia -> {
            EffettuaId id = new EffettuaId(famiglia.getEmail(), pagamentoSalvato.getIdPagamento());

            Effettua associazione = new Effettua();
            associazione.setId(id);
            associazione.setFamiglia(famiglia);
            associazione.setPagamento(pagamentoSalvato);
            associazione.setStato(StatoPagamento.NON_EFFETTUATO);
            associazione.setDataPagamento(null); // Non ancora pagato

            return associazione;
        }).collect(Collectors.toList());

        // 4. Salvo massivamente tutte le associazioni
        effettuaRepository.saveAll(associazioni);

        return pagamentoSalvato;
    }

    /**
     * Registra il pagamento effettivo da parte di una famiglia.
     */
    @Transactional
    public Effettua effettuaPagamento(String emailFamiglia, Long idPagamento) {
        // 1. Creo l'ID composto per la ricerca
        EffettuaId id = new EffettuaId(emailFamiglia, idPagamento);

        // 2. Cerco il record esistente
        Effettua record = effettuaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scadenza non trovata per questa famiglia"));

        // 3. Aggiorno i dati
        record.setStato(StatoPagamento.EFFETTUATO);
        record.setDataPagamento(LocalDate.now());

        // 4. Salvo l'aggiornamento
        return effettuaRepository.save(record);
    }

    @Transactional
    public void aggiornaStatiScaduti() {
        List<Effettua> nonPagati = effettuaRepository.findByStato(StatoPagamento.NON_EFFETTUATO);
        LocalDate oggi = LocalDate.now();

        for (Effettua e : nonPagati) {
            if (e.getPagamento().getDataScadenza().isBefore(oggi)) {
                e.setStato(StatoPagamento.SCADUTO);
            }
        }
        effettuaRepository.saveAll(nonPagati);
    }
}
