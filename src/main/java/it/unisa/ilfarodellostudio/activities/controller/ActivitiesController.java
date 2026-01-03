package it.unisa.ilfarodellostudio.activities.controller;

import it.unisa.ilfarodellostudio.activities.ActivitiesService;
import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.activities.repository.MateriaRepository;
import it.unisa.ilfarodellostudio.users.UsersService;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Controller per la gestione delle attività didattiche.
 * Gestisce le richieste web relative alle attività da parte di docenti e studenti.
 */
@Controller
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private UsersService usersService;

    /**
     * Mostra la pagina di dettaglio generica (stub) per lo studente.
     *
     * @return la vista di dettaglio attività
     */
    @GetMapping("/studente/dettaglio-attivita")
    public String mostraDettagliAttivita() {
        return "studente/dettaglio-attivita";
    }

    // --- LISTA ATTIVITÀ ---
    /**
     * Gestisce la visualizzazione della lista delle attività per il docente autenticato.
     * Mostra solo le attività assegnate al docente corrente.
     *
     * @param model il modello per la vista
     * @param principal info sull'utente autenticato
     * @return la vista di gestione attività
     */
    @GetMapping("/docente/gestione-attivita")
    public String gestioneAttivita(Model model, Principal principal) {
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));

        model.addAttribute("attivitaDocente", activitiesService.dammiTutteLeAttivita(docente));
        return "docente/gestione-attivita";
    }

    // --- FORM CREA ---
    /**
     * Mostra il form per la creazione di una nuova attività.
     *
     * @param model il modello per la vista
     * @param principal info sull'utente autenticato
     * @return la vista del form di creazione
     */
    @GetMapping("/docente/crea-attivita")
    public String creaNuovaAttivita(Model model, Principal principal) {
        String emailDocente = principal.getName();
        Docente docente = usersService.cercaDocente(emailDocente)
                .orElseThrow(() -> new RuntimeException("Docente non trovato"));
        model.addAttribute("materieDocente", docente.getMaterieInsegnate());
        return "docente/crea-attivita";
    }

    // --- POST CREA (GESTIONE ERRORI TRAMITE CATCH) ---
    /**
     * Gestisce la sottomissione del form di creazione attività.
     *
     * @param nome titolo dell'attività
     * @param descrizione descrizione dell'attività
     * @param materia nome della materia
     * @param data data dell'attività
     * @param oraInizio ora di inizio
     * @param oraFine ora di fine
     * @param posti numero di posti disponibili
     * @param principal info sull'utente autenticato
     * @param redirectAttributes attributi per il redirect (flash attributes)
     * @return redirect alla pagina di gestione o al form in caso di errore
     */
    @PostMapping("/attivita/crea")
    public String creaAttivita(@RequestParam String nome,
                               @RequestParam String descrizione,
                               @RequestParam String materia,
                               @RequestParam LocalDate data,
                               @RequestParam LocalTime oraInizio,
                               @RequestParam LocalTime oraFine,
                               @RequestParam Integer posti,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            // 1. Recupero Dati
            String emailDocente = principal.getName();
            Docente docente = usersService.cercaDocente(emailDocente)
                    .orElseThrow(() -> new RuntimeException("Docente non trovato"));

            // 2. Costruzione Oggetti
            Materia materiaObj = new Materia();
            materiaObj.setNome(materia);

            Attivita attivita = new Attivita();
            attivita.setTitolo(nome);
            attivita.setDescrizione(descrizione);
            attivita.setData(data);
            attivita.setOraInizio(oraInizio);
            attivita.setOraFine(oraFine);
            attivita.setPosti(posti);
            attivita.setMateria(materiaObj);
            attivita.setDocente(docente);

            // 3. CHIAMATA AL SERVICE (Qui avvengono le validazioni)
            activitiesService.creaAttivita(attivita);

            // 4. Successo
            redirectAttributes.addFlashAttribute("success", "Attività creata con successo!");
            return "redirect:/docente/gestione-attivita";

        } catch (RuntimeException e) {
            // 5. ERRORE CATTURATO (Data passata, sovrapposizione, ecc.)
            // Mostriamo il messaggio nell'HTML senza far crashare il sito
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/docente/gestione-attivita";
        }
    }

    // --- FORM MODIFICA ---
    /**
     * Mostra il form per la modifica di un'attività esistente.
     * Controlla che il docente sia autorizzato a modificare l'attività.
     *
     * @param id ID dell'attività da modificare
     * @param model modello per la vista
     * @param principal info sull'utente autenticato
     * @return vista di modifica o redirect in caso di errore
     */
    @GetMapping("/attivita/modifica/{id}")
    public String mostraFormModifica(@PathVariable Long id, Model model, Principal principal) {
        Attivita attivita = activitiesService.visualizzaAttivita(id);
        String emailDocente = principal.getName();

        if (attivita == null || !attivita.getDocente().getEmail().equals(emailDocente)) {
            return "redirect:/docente/gestione-attivita?error=Non autorizzato";
        }

        Docente docente = usersService.cercaDocente(emailDocente).orElseThrow();
        model.addAttribute("materieDocente", docente.getMaterieInsegnate());
        model.addAttribute("attivita", attivita);
        return "docente/modifica-attivita";
    }

    // --- POST MODIFICA ---
    /**
     * Gestisce la sottomissione del form di modifica attività.
     *
     * @param id ID dell'attività
     * @param nome nuovo titolo
     * @param descrizione nuova descrizione
     * @param materia nuova materia
     * @param data nuova data
     * @param oraInizio nuova ora inizio
     * @param oraFine nuova ora fine
     * @param posti nuovi posti
     * @param principal info utente
     * @param redirectAttributes attributi per redirect
     * @return redirect alla gestione attività
     */
    @PostMapping("/attivita/modifica")
    public String modificaAttivita(@RequestParam Long id,
                                   @RequestParam String nome,
                                   @RequestParam String descrizione,
                                   @RequestParam String materia,
                                   @RequestParam LocalDate data,
                                   @RequestParam LocalTime oraInizio,
                                   @RequestParam LocalTime oraFine,
                                   @RequestParam Integer posti,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        Attivita attivita = activitiesService.visualizzaAttivita(id);
        String emailDocente = principal.getName();

        if (attivita == null || !attivita.getDocente().getEmail().equals(emailDocente)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Attività non trovata o non autorizzata.");
            return "redirect:/docente/gestione-attivita";
        }

        // Qui replichiamo i controlli base per sicurezza anche in modifica
        if (LocalDateTime.of(data, oraInizio).isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Errore: Non puoi spostare lezioni nel passato.");
            return "redirect:/attivita/modifica/" + id;
        }

        // Verifica sovrapposizione escluso se stesso
        if (activitiesService.verificaSovrapposizioneEsclusoId(attivita.getDocente(), data, oraInizio, oraFine, id)) {
            redirectAttributes.addFlashAttribute("error", "Errore: Sovrapposizione con un'altra lezione.");
            return "redirect:/attivita/modifica/" + id;
        }

        // Aggiornamento
        attivita.setTitolo(nome);
        attivita.setDescrizione(descrizione);
        attivita.setData(data);
        attivita.setOraInizio(oraInizio);
        attivita.setOraFine(oraFine);
        attivita.setPosti(posti);

        // Gestione materia cambio nome
        if(!attivita.getMateria().getNome().equals(materia)) {
            Materia m = new Materia();
            m.setNome(materia);
            // Il cascade o logica di salvataggio gestirà la nuova materia se necessario
            // Oppure recuperala come nel create
            Materia materiaDb = materiaRepository.findByNome(materia)
                    .orElseGet(() -> materiaRepository.save(m));
            attivita.setMateria(materiaDb);
        }

        activitiesService.modificaAttivita(attivita);

        redirectAttributes.addFlashAttribute("success", "Attività modificata con successo!");
        return "redirect:/docente/gestione-attivita";
    }

    // --- CANCELLA ---
    /**
     * Elimina un'attività.
     *
     * @param id ID dell'attività da eliminare
     * @param principal info utente
     * @param redirectAttributes attributi per redirect
     * @return redirect alla gestione attività
     */
    @GetMapping("/attivita/cancella/{id}")
    public String cancellaAttivita(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Attivita attivita = activitiesService.visualizzaAttivita(id);
            String emailDocente = principal.getName();

            if (attivita != null && attivita.getDocente().getEmail().equals(emailDocente)) {
                activitiesService.eliminaAttivita(id);
                redirectAttributes.addFlashAttribute("success", "Attività eliminata.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Errore: Non autorizzato.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore durante l'eliminazione.");
        }
        return "redirect:/docente/gestione-attivita";
    }
}