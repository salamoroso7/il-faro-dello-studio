package it.unisa.ilfarodellostudio.users.entity;

import it.unisa.ilfarodellostudio.activities.entity.Attivita;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "docente")
public class Docente extends UtenteRegistrato {

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
    private List<Attivita> attivitaCreate = new ArrayList<>();

    public Docente() {
        super();
    }

    public List<Attivita> getAttivitaCreate() {
        return attivitaCreate;
    }

    public void setAttivitaCreate(List<Attivita> attivitaCreate) {
        this.attivitaCreate = attivitaCreate;
    }
}