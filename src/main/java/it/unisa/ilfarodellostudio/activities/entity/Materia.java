package it.unisa.ilfarodellostudio.activities.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "materia")
public class Materia {

    @Id
    private String nome;

    @Column(length = 300)
    private String descrizione;

    public Materia() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
