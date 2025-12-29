package it.unisa.ilfarodellostudio.activities.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "materia")
public class Materia {

    @Id
    private String nome;

    public Materia() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
