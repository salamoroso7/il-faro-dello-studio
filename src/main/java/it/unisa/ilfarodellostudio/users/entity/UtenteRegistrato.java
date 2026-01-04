package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;

/**
 * Classe base astratta per tutti gli utenti registrati.
 * Contiene i campi comuni come email, nome, cognome, password e stato.
 */
@MappedSuperclass
public abstract class UtenteRegistrato {

    @Id
    @Column(nullable = false, length = 300)
    private String email;

    @Column(nullable = false, length = 40)
    private String nome;

    @Column(nullable = false, length = 40)
    private String cognome;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_attivo", nullable = false)
    private boolean isAttivo = true;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    public UtenteRegistrato() {
    }

    public UtenteRegistrato(String email, String nome, String cognome, String password) {
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAttivo() {
        return isAttivo;
    }

    public void setAttivo(boolean attivo) {
        isAttivo = attivo;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}