package it.unisa.ilfarodellostudio.payments.entity;

import it.unisa.ilfarodellostudio.users.entity.Famiglia;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entity che rappresenta l'associazione tra una Famiglia e un Pagamento (Tassa).
 * Tiene traccia dello stato del pagamento (EFFETTUATO, NON_EFFETTUATO, SCADUTO) e della data.
 */
@Entity
@Table(name = "effettua")
public class Effettua {

    @EmbeddedId
    private EffettuaId id = new EffettuaId();

    @ManyToOne
    @MapsId("emailFamiglia")
    @JoinColumn(name = "email_famiglia")
    private Famiglia famiglia;

    @ManyToOne
    @MapsId("idPagamento")
    @JoinColumn(name = "id_pagamento")
    private Pagamento pagamento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoPagamento stato;

    public Effettua() {
    }

    public Effettua(Famiglia famiglia, Pagamento pagamento, LocalDate dataPagamento, StatoPagamento stato) {
        this.famiglia = famiglia;
        this.pagamento = pagamento;
        this.dataPagamento = dataPagamento;
        this.stato = stato;
    }

    public EffettuaId getId() {
        return id;
    }

    public void setId(EffettuaId id) {
        this.id = id;
    }

    public Famiglia getFamiglia() {
        return famiglia;
    }

    public void setFamiglia(Famiglia famiglia) {
        this.famiglia = famiglia;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public StatoPagamento getStato() {
        return stato;
    }

    public void setStato(StatoPagamento stato) {
        this.stato = stato;
    }
}
