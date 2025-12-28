package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "studente")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Studente extends UtenteRegistrato {

    // Qui andranno aggiunte le relazioni (es. @ManyToMany per le attività)
    // Esempio:
    // @ManyToMany(mappedBy = "studentiIscritti")
    // private List<Attivita> attivita;
}