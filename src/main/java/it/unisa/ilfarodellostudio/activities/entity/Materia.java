package it.unisa.ilfarodellostudio.activities.entity;
import it.unisa.ilfarodellostudio.users.entity.Docente;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "materia")
public class Materia {

    @Id
    private String nome;

    @ManyToMany(mappedBy = "materieInsegnate")
    private Set<Docente> docenti = new HashSet<>();

    public Materia() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Docente> getDocenti() {
        return docenti;
    }

    public void setDocenti(Set<Docente> docenti) {
        this.docenti = docenti;
    }
}
