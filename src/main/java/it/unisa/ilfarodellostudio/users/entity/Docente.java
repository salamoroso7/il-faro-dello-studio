package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "docente")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Docente extends UtenteRegistrato {

    // @OneToMany(mappedBy = "docente")
    // private List<Attivita> attivitaCreate;
}