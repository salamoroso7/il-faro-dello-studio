package it.unisa.ilfarodellostudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * La classe principale dell'applicazione Il Faro dello Studio.
 * Questa classe avvia l'applicazione Spring Boot.
 */
@SpringBootApplication
public class IlFaroDelloStudioApplication {

    /**
     * Il metodo principale che avvia l'applicazione.
     *
     * @param args argomenti della riga di comando passati all'applicazione
     */
    public static void main(String[] args) {
        SpringApplication.run(IlFaroDelloStudioApplication.class, args);
    }

}
