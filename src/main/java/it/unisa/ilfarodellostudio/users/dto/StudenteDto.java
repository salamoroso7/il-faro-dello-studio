package it.unisa.ilfarodellostudio.users.dto;

import java.time.LocalDate;

/**
 * DTO per la registrazione di uno Studente (da parte della Famiglia).
 */
public class StudenteDto {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private LocalDate dataNascita;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }
}