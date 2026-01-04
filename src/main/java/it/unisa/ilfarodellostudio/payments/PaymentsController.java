package it.unisa.ilfarodellostudio.payments;

import it.unisa.ilfarodellostudio.payments.entity.Effettua;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controller per la gestione dei pagamenti lato famiglia.
 */
@Controller
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    /**
     * Mostra la lista dei pagamenti della famiglia loggata.
     *
     * @return la vista della lista pagamenti
     */
    @GetMapping("/famiglia/lista-pagamenti")
    public String listaPagamenti(Model model) {
        List<Effettua> pagamenti = paymentsService.getAllPagamentiConStato();
        model.addAttribute("listaPagamenti", pagamenti);
        return "famiglia/lista-pagamenti";
    }

    /**
     * Mostra la pagina di checkout per il pagamento.
     *
     * @return la vista del checkout
     */
    @GetMapping("/famiglia/checkout-pagamento")
    public String checkoutPagamento(@RequestParam Long idPagamento, Model model, Authentication authentication) {
        String emailFamiglia = authentication.getName();

        Effettua dettaglio = paymentsService.getDettaglioPagamento(emailFamiglia, idPagamento);

        model.addAttribute("dettaglio", dettaglio);
        return "famiglia/checkout-pagamento";
    }

    @PostMapping("/famiglia/conferma-pagamento")
    public String confermaPagamento(@RequestParam Long idPagamento,
                                    @RequestParam String numeroCarta,
                                    @RequestParam String scadenza,
                                    @RequestParam String cvv,
                                    Model model,
                                    Authentication authentication) {

        String emailFamiglia = authentication.getName();
        String errore = null;

        // 1. Validazione Numero Carta (16 cifre)
        if (numeroCarta == null || !numeroCarta.matches("\\d{16}")) {
            errore = "Il numero della carta deve essere composto da 16 cifre.";
        }
        // 2. Validazione CVV (3 cifre)
        else if (cvv == null || !cvv.matches("\\d{3}")) {
            errore = "Il CVV deve essere di 3 cifre.";
        }
        // 3. Validazione Scadenza (MM/AA e non passata)
        else {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yy");
                YearMonth scadenzaCarta = YearMonth.parse(scadenza, fmt);
                if (scadenzaCarta.isBefore(YearMonth.now())) {
                    errore = "La carta è scaduta.";
                }
            } catch (DateTimeParseException e) {
                errore = "Formato scadenza non valido (usa MM/AA).";
            }
        }

        // Se c'è un errore, torna al checkout con il messaggio
        if (errore != null) {
            Effettua dettaglio = paymentsService.getDettaglioPagamento(emailFamiglia, idPagamento);
            model.addAttribute("dettaglio", dettaglio);
            model.addAttribute("error", errore);
            return "famiglia/checkout-pagamento";
        }

        // Se tutto è OK, procedi con il pagamento
        try {
            paymentsService.effettuaPagamento(emailFamiglia, idPagamento);
            return "redirect:/famiglia/lista-pagamenti?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Errore durante il processamento del pagamento.");
            return "famiglia/checkout-pagamento";
        }
    }
}
