package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.payments.entity.Effettua;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity che rappresenta una Famiglia (Genitore).
 * Estende UtenteRegistrato e gestisce la relazione con Studenti figli e Pagamenti effettuati.
 */
@Entity
@Table(name = "famiglia")
public class Famiglia extends UtenteRegistrato {

    @OneToMany(mappedBy = "famiglia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Studente> studenti = new ArrayList<>();

    @OneToMany(mappedBy = "famiglia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Effettua> pagamentiEffettuati = new ArrayList<>();

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

    public List<Effettua> getPagamentiEffettuati() {
        return pagamentiEffettuati;
    }

    public void setPagamentiEffettuati(List<Effettua> pagamentiEffettuati) {
        this.pagamentiEffettuati = pagamentiEffettuati;
    }

    // Metodo helper per la coerenza bidirezionale
    public void addStudente(Studente studente) {
        studenti.add(studente);
        studente.setFamiglia(this); // Ora Studente ha il metodo setFamiglia visibile
    }

    public void addPagamentoEffettuato(Effettua associazione) {
        this.pagamentiEffettuati.add(associazione);
        associazione.setFamiglia(this);
    }
}