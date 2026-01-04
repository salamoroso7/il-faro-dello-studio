package it.unisa.ilfarodellostudio.feedbacks.entity;

import it.unisa.ilfarodellostudio.users.entity.Docente;
import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int valutazione; // Es. da 1 a 5 stelle

    @Column(length = 1000)
    private String commento;

    // Il destinatario è sempre un docente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_email", nullable = false)
    private Docente docente;

    // Autore può essere uno Studente...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studente_email")
    private Studente studente;

    // ...oppure una Famiglia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "famiglia_email")
    private Famiglia famiglia;

    // Logica di controllo: un feedback deve avere esattamente un autore
    @PrePersist
    @PreUpdate
    private void validate() {
        if ((studente == null && famiglia == null) || (studente != null && famiglia != null)) {
            throw new IllegalStateException("Il feedback deve essere inviato o da uno studente o da una famiglia.");
        }
    }

    public Feedback() {
    }

    // === GETTER E SETTER ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getValutazione() { return valutazione; }
    public void setValutazione(int valutazione) { this.valutazione = valutazione; }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public Studente getStudente() {
        return studente;
    }

    public void setStudente(Studente studente) {
        this.studente = studente;
    }

    public Famiglia getFamiglia() {
        return famiglia;
    }

    public void setFamiglia(Famiglia famiglia) {
        this.famiglia = famiglia;
    }
}