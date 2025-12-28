package it.unisa.ilfarodellostudio.users.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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
}