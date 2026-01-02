package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "famiglia")
public class Famiglia extends UtenteRegistrato {

    @OneToMany(mappedBy = "famiglia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Studente> studenti = new ArrayList<>();

    public Famiglia() {
        super();
    }

    public Famiglia(String email, String nome, String cognome, String password) {
        super(email, nome, cognome, password);
    }

    public List<Studente> getStudenti() {
        return studenti;
    }

    public void setStudenti(List<Studente> studenti) {
        this.studenti = studenti;
    }

    // Metodo helper per la coerenza bidirezionale
    public void addStudente(Studente studente) {
        studenti.add(studente);
        studente.setFamiglia(this); // Ora Studente ha il metodo setFamiglia visibile
    }
}