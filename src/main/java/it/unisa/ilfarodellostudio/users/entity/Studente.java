package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "studente")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Studente extends UtenteRegistrato {

    @ManyToMany(mappedBy = "iscritti")
    @Builder.Default
    private Set<Attivita> attivita = new HashSet<>();

    public Set<Attivita> getAttivita() {
        if (this.attivita == null) {
            this.attivita = new HashSet<>();
        }
        return this.attivita;
    }
}