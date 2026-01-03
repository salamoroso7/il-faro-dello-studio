package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity che rappresenta uno Studente.
 * Estende UtenteRegistrato e include dati specifici come Codice Fiscale, Data di Nascita e riferimenti a Famiglia e Attività.
 */
@Entity
@Table(name = "studente")
public class Studente extends UtenteRegistrato {

    @Column(name = "codice_fiscale", nullable = false, length = 16)
    private String codiceFiscale;

    @Column(name = "data_nascita", nullable = false)
    private LocalDate dataNascita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "famiglia_email", nullable = false)
    private Famiglia famiglia;

    @ManyToMany(mappedBy = "iscritti")
    private Set<Attivita> attivita = new HashSet<>();

    public Studente() {
        super();
    }

    public Studente(String codiceFiscale, LocalDate dataNascita) {
        super();
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public Famiglia getFamiglia() {
        return famiglia;
    }

    public void setFamiglia(Famiglia famiglia) {
        this.famiglia = famiglia;
    }

    public Set<Attivita> getAttivita() {
        return attivita;
    }

    public void setAttivita(Set<Attivita> attivita) {
        this.attivita = attivita;
    }
}