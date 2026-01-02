package it.unisa.ilfarodellostudio.payments.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPagamento;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataScadenza;

    @Column(nullable = false)
    private double importo;

    public Pagamento() {
    }

    public Pagamento(String nome, LocalDate dataScadenza, double importo) {
        this.nome = nome;
        this.dataScadenza = dataScadenza;
        this.importo = importo;
    }

    public Long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }
}
