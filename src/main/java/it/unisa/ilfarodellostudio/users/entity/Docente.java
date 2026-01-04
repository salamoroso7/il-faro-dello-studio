package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.activities.entity.Materia;
import it.unisa.ilfarodellostudio.feedbacks.entity.Feedback;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "docente")
public class Docente extends UtenteRegistrato {

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
    private List<Attivita> attivitaCreate = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "insegna", // Nome della tabella di giunzione
            joinColumns = @JoinColumn(name = "docente_email"), // FK verso Docente (ID è email)
            inverseJoinColumns = @JoinColumn(name = "materia_nome") // FK verso Materia (ID è nome)
    )
    private Set<Materia> materieInsegnate = new HashSet<>();

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Feedback> feedbackRicevuti = new ArrayList<>();

    public Docente() {
        super();
    }

    public List<Attivita> getAttivitaCreate() {
        return attivitaCreate;
    }

    public void setAttivitaCreate(List<Attivita> attivitaCreate) {
        this.attivitaCreate = attivitaCreate;
    }

    public Set<Materia> getMaterieInsegnate() {
        return materieInsegnate;
    }

    public void setMaterieInsegnate(Set<Materia> materieInsegnate) {
        this.materieInsegnate = materieInsegnate;
    }

    public List<Feedback> getFeedbackRicevuti() {
        return feedbackRicevuti;
    }

    public void setFeedbackRicevuti(List<Feedback> feedbackRicevuti) {
        this.feedbackRicevuti = feedbackRicevuti;
    }
}