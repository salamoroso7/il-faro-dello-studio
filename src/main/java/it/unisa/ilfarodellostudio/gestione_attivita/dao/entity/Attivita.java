package it.unisa.ilfarodellostudio.gestione_attivita.dao.entity;

import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Docente;
import it.unisa.ilfarodellostudio.gestione_utenze.dao.entity.Studente;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity che rappresenta un'attività didattica (lezione, evento, ecc.).
 * Collegata a un docente, una materia e una lista di studenti iscritti.
 */
@Entity
@Table(name = "attivita")
public class Attivita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attivita")
    private Long idAttivita;

    @Column(nullable = false)
    private String titolo;

    @Column(length = 500)
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "ora_inizio", nullable = false)
    private LocalTime oraInizio;

    @Column(name = "ora_fine", nullable = false)
    private LocalTime oraFine;

    @Column(nullable = false)
    private int posti;

    @ManyToOne
    @JoinColumn(name = "materia_nome", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "docente_email") // Il nome della colonna nel DB
    private Docente docente; // <--- DEVE CHIAMARSI 'docente' (case-sensitive)

    @ManyToMany
    @JoinTable(
            name = "iscrizione_attivita", // Nome della tabella di giunzione
            joinColumns = @JoinColumn(name = "id_attivita"), // FK verso Attivita
            inverseJoinColumns = @JoinColumn(name = "email_studente") // FK verso Studente
    )
    private Set<Studente> iscritti = new HashSet<>();

    // === COSTRUTTORI ===
    public Attivita() {}

    // Helper method per mantenere la sincronizzazione bidirezionale
    /**
     * Aggiunge uno studente alla lista degli iscritti e sincronizza la relazione.
     *
     * @param studente lo studente da iscrivere
     */
    public void aggiungiStudente(Studente studente) {
        this.iscritti.add(studente);
        studente.getAttivita().add(this);
    }

    /**
     * Rimuove uno studente dalla lista degli iscritti e sincronizza la relazione.
     *
     * @param studente lo studente da rimuovere
     */
    public void rimuoviStudente(Studente studente) {
        this.iscritti.remove(studente);
        studente.getAttivita().remove(this);
    }

    // === GETTER E SETTER ===
    public Long getIdAttivita() {
        return idAttivita;
    }

    public void setIdAttivita(Long idAttivita) {
        this.idAttivita = idAttivita;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public LocalTime getOraFine() {
        return oraFine;
    }

    public void setOraFine(LocalTime oraFine) {
        this.oraFine = oraFine;
    }

    public int getPosti() {
        return posti;
    }

    public void setPosti(int posti) {
        this.posti = posti;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Set<Studente> getIscritti() {
        return iscritti;
    }

    public void setIscritti(Set<Studente> iscritti) {
        this.iscritti = iscritti;
    }

    // Nella classe Attivita.java
    public int getPostiRimanenti() {
        return this.posti - this.iscritti.size();
    }
}

