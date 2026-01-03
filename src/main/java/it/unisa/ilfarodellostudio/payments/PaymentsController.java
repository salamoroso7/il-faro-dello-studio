package it.unisa.ilfarodellostudio.payments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String listaPagamenti() {
        return "famiglia/lista-pagamenti";
    }

    /**
     * Mostra la pagina di checkout per il pagamento.
     *
     * @return la vista del checkout
     */
    @GetMapping("/famiglia/checkout-pagamento")
    public String checkout() {
        return "famiglia/checkout-pagamento";
    }

    /*
    @GetMapping("/checkout-pagamento")
    public String checkout(@RequestParam Long idPagamento, Model model) {
        return "famiglia/checkout";
    }

     */
}
