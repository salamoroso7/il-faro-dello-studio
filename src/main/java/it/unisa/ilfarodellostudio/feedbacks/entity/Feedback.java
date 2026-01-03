package it.unisa.ilfarodellostudio.feedbacks.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.users.entity.Studente;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity che rappresenta un feedback lasciato da uno studente per un'attività.
 */
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

    @Column(nullable = false)
    private LocalDateTime dataInserimento;

    // Relazione con lo Studente che lascia il feedback
    @ManyToOne
    @JoinColumn(name = "studente_email", nullable = false)
    private Studente studente;

    // Relazione con l'Attività recensita
    @ManyToOne
    @JoinColumn(name = "attivita_id", nullable = false)
    private Attivita attivita;

    public Feedback() {
        this.dataInserimento = LocalDateTime.now();
    }

    // === GETTER E SETTER ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getValutazione() { return valutazione; }
    public void setValutazione(int valutazione) { this.valutazione = valutazione; }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }

    public LocalDateTime getDataInserimento() { return dataInserimento; }
    public void setDataInserimento(LocalDateTime dataInserimento) { this.dataInserimento = dataInserimento; }

    public Studente getStudente() { return studente; }
    public void setStudente(Studente studente) { this.studente = studente; }

    public Attivita getAttivita() { return attivita; }
    public void setAttivita(Attivita attivita) { this.attivita = attivita; }
}