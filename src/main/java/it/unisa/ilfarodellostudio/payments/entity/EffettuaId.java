package it.unisa.ilfarodellostudio.payments.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EffettuaId implements Serializable {
    private String emailFamiglia;
    private Long idPagamento;

    public EffettuaId() {
    }

    public EffettuaId(String emailFamiglia, Long idPagamento) {
        this.emailFamiglia = emailFamiglia;
        this.idPagamento = idPagamento;
    }

    // Metodo equals: confronta se due oggetti chiave sono identici
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Se è lo stesso oggetto in memoria
        if (o == null || getClass() != o.getClass()) return false; // Controllo null e classe

        EffettuaId that = (EffettuaId) o;

        // Confronta i singoli campi
        return Objects.equals(emailFamiglia, that.emailFamiglia) &&
                Objects.equals(idPagamento, that.idPagamento);
    }

    // Metodo hashCode: genera un numero intero basato sui campi della chiave
    @Override
    public int hashCode() {
        return Objects.hash(emailFamiglia, idPagamento);
    }

    public String getEmailFamiglia() {
        return emailFamiglia;
    }

    public void setEmailFamiglia(String emailFamiglia) {
        this.emailFamiglia = emailFamiglia;
    }

    public Long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }
}
