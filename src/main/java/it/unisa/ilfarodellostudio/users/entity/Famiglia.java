package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.payments.entity.Pagamento; // Assumendo che esista nel package payments
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "famiglia")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Famiglia extends UtenteRegistrato {

    /* RELAZIONE 1:N con Studente
       Una famiglia può avere più figli iscritti.
       CascadeType.ALL permette di salvare/eliminare gli studenti se viene salvata/eliminata la famiglia.
    */
    @OneToMany(mappedBy = "famiglia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita loop infiniti nel toString di Lombok
    @EqualsAndHashCode.Exclude
    private List<Studente> studenti = new ArrayList<>();

    /* RELAZIONE 1:N con Pagamento (Modulo Gestione Pagamenti - RF_GP_1)
       Una famiglia effettua molti pagamenti.
    */
    @OneToMany(mappedBy = "famiglia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Pagamento> pagamenti = new ArrayList<>();

    /* Metodo helper per aggiungere uno studente mantenendo la coerenza bidirezionale
    public void addStudente(Studente studente) {
        studenti.add(studente);
        studente.setFamiglia(this);
    }*/
}