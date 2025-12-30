package it.unisa.ilfarodellostudio.activities.entity;

import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "attivita")
public class Attivita {

    public static final int MAX_POSTI = 25;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAttivita;

    @Column(nullable = false)
    private String titolo;

    @Column(length = 500)
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime oraInizio;

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
    public void aggiungiStudente(Studente studente) {
        this.iscritti.add(studente);
        studente.getAttivita().add(this);
    }

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
}

