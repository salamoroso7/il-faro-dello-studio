package it.unisa.ilfarodellostudio.users.dto;

import java.util.List;

/**
 * DTO per la registrazione di un Docente.
 * Contiene i dati anagrafici e la lista delle materie insegnate.
 */
public class DocenteDto {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private List<String> materie;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getMaterie() {
        return materie;
    }

    public void setMaterie(List<String> materie) {
        this.materie = materie;
    }
}