package it.unisa.ilfarodellostudio.payments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    @GetMapping("/famiglia/lista-pagamenti")
    public String listaPagamenti() {
        return "famiglia/lista-pagamenti";
    }

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
