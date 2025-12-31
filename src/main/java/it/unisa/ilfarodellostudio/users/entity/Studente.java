package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "studente")
public class Studente extends UtenteRegistrato {

    @Column(nullable = false, length = 11)
    private String codiceFiscale;

    @Column(nullable = false)
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