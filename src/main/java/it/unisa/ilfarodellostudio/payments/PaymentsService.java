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

/**
 * Service per la gestione dei pagamenti e delle tasse.
 * Gestisce la creazione delle tasse globali, la registrazione dei pagamenti da parte delle famiglie e l'aggiornamento degli stati.
 */
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
     * Crea un record 'Effettua' con stato NON_EFFETTUATO per ogni famiglia presente nel sistema.
     *
     * @param nuovoPagamento l'oggetto pagamento (tassa) da creare
     * @return il pagamento salvato
     */
    @Transactional
    public Pagamento creaPagamentoGenerale(Pagamento nuovoPagamento) {
        // 1. Salvo il pagamento (la tassa)
        Pagamento pagamentoSalvato = pagamentoRepository.save(nuovoPagamento);

        // 2. Recupero le famiglie (diventano entità "managed")
        List<Famiglia> tutteLeFamiglie = famigliaRepository.findAll();

        // 3. Colleghiamo il pagamento a ogni famiglia
        for (Famiglia famiglia : tutteLeFamiglie) {
            EffettuaId id = new EffettuaId(famiglia.getEmail(), pagamentoSalvato.getIdPagamento());

            Effettua associazione = new Effettua();
            associazione.setId(id);
            associazione.setPagamento(pagamentoSalvato);
            associazione.setStato(StatoPagamento.NON_EFFETTUATO);
            associazione.setDataPagamento(null);

            famiglia.addPagamentoEffettuato(associazione);
        }

        return pagamentoSalvato;
    }

    /**
     * Registra il pagamento effettivo di una tassa da parte di una famiglia.
     * Aggiorna lo stato a EFFETTUATO e imposta la data odierna.
     *
     * @param emailFamiglia l'email della famiglia
     * @param idPagamento l'ID della tassa pagata
     * @return il record aggiornato
     * @throws RuntimeException se il pagamento non viene trovato per la famiglia
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

    /**
     * Aggiorna automaticamente lo stato dei pagamenti non effettuati in SCADUTO se la data di scadenza è superata.
     * Questo metodo dovrebbe essere schedulato o chiamato periodicamente.
     */
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

    public void verificaSituazioneDebitoria(Famiglia famiglia) {
        boolean haPagamentiScaduti = famiglia.getPagamentiEffettuati().stream()
                .anyMatch(p -> p.getStato() == StatoPagamento.SCADUTO);

        if (haPagamentiScaduti) {
            throw new RuntimeException("Iscrizione negata: pagamenti in sospeso (Stato: SCADUTO)");
        }
    }

    /**
     * Ritorna tutti i record della tabella di giunzione 'effettua'
     * per avere accesso sia ai dettagli del pagamento che allo stato.
     */
    public List<Effettua> getAllPagamentiConStato() {
        return effettuaRepository.findAll();
    }

    public Effettua getDettaglioPagamento(String emailFamiglia, Long idPagamento) {
        EffettuaId id = new EffettuaId(emailFamiglia, idPagamento);
        return effettuaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento non trovato"));
    }
}
