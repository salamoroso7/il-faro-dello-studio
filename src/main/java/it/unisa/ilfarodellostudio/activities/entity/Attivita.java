package it.unisa.ilfarodellostudio.activities.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attivita")
public class Attivita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAttivita;

    @Column(nullable = false)
    private String titolo;

    @Column(length = 500)
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime oraInizio;

  //  @Column(nullable = false)
  //  private LocalTime oraFine;

    @ManyToOne
    @JoinColumn(name = "materia_nome", nullable = false)
    private Materia materia;

    // === COSTRUTTORI ===
    public Attivita() {}

    // === GETTER E SETTER ===
    public Long getIdAttivita() {
        return idAttivita;
    }

    public void setIdAttivita(Long idAttivita) {
        this.idAttivita = idAttivita;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(LocalTime oraInizio) {
        this.oraInizio = oraInizio;
    }
/*
    public LocalTime getOraFine() {
        return oraFine;
    }

    public void setOraFine(LocalTime oraFine) {
        this.oraFine = oraFine;
    }
*/
    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }
}

