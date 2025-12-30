package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import it.unisa.ilfarodellostudio.feedback.entity.Feedback;   // Assumendo esista nel package feedback
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "docente")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Docente extends UtenteRegistrato {

    @Column(nullable = false)
    private String materia; // Come da DocenteDto

    @Column(columnDefinition = "TEXT")
    private String biografia;

    /* RELAZIONE 1:N con Attivita (Modulo Gestione Attività - RF_GA_1)
       Un docente crea/gestisce molte attività.
    */
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Attivita> attivitaCreate = new ArrayList<>();

    /* RELAZIONE 1:N con Feedback (Modulo Gestione Feedback - RF_GFB_1)
       Un docente RICEVE molti feedback dagli studenti.
    */
    @OneToMany(mappedBy = "docenteDestinatario", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Feedback> feedbackRicevuti = new ArrayList<>();
}