package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;

@MappedSuperclass
public abstract class UtenteRegistrato {

    @Id
    @Column(nullable = false, length = 300)
    private String email;

    @Column(nullable = false, length = 40)
    private String nome;

    @Column(nullable = false, length = 40)
    private String cognome;

    @Column(nullable = false, length = 40, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    public UtenteRegistrato() {
    }

    public UtenteRegistrato(String email, String nome, String cognome, String username, String password) {
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}