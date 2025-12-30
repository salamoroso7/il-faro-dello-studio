package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "studente")
public class Studente extends UtenteRegistrato {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "famiglia_email", nullable = false)
    private Famiglia famiglia;

    @ManyToMany(mappedBy = "iscritti")
    private Set<Attivita> attivita = new HashSet<>();

    public Studente() {
        super();
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