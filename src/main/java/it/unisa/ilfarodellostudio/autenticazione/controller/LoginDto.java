package it.unisa.ilfarodellostudio.autenticazione.controller;

/**
 * Data Transfer Object (DTO) per il login.
 * Contiene le credenziali (username e password) inviate dal client.
 */
public class LoginDto {
    /**
     * Lo username dell'utente (email).
     */
    private String username;
    /**
     * La password dell'utente.
     */
    private String password;

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