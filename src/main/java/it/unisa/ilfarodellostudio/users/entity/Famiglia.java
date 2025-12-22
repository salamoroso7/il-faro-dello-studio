package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "famiglia")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Famiglia extends UtenteRegistrato {

    // Qui andranno le relazioni con i Pagamenti e gli Studenti
    // Esempio:
    // @OneToMany(mappedBy = "famiglia")
    // private List<Pagamento> pagamenti;
}