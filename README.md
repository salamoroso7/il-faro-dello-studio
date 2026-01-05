# 📚 Il Faro dello Studio

> Piattaforma web per la gestione delle attività di doposcuola.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

## 📖 Descrizione del Progetto
**Il Faro dello Studio** è una web application sviluppata per semplificare l'organizzazione e la fruizione dei servizi di doposcuola. Il sistema mette in comunicazione docenti, studenti e famiglie, gestendo il ciclo di vita delle attività.

Il progetto è stato realizzato come elaborato finale per il corso di **Ingegneria del Software** (A.A. 2025/2026), Università degli Studi di Salerno.

### Funzionalità Principali
* **Gestione Attività:** I docenti possono creare e pianificare lezioni e ripetizioni.
* **Iscrizioni alle Attività:** Gli studenti possono iscriversi alle attività disponibili.
* **Gestione Familiare:** Le famiglie gestiscono gli account degli studenti e i pagamenti.

---

## 📂 Documentazione
Tutta la documentazione tecnica e progettuale è disponibile nella cartella [`docs/`](./docs) di questa repository.

| Documento | Descrizione |
| :--- | :--- |
| 📘 **[RAD](./docs/NC15_RAD_v2.0.pdf)** | *Requirements Analysis Document*: Specifica dei requisiti, casi d'uso e attori. |
| 📐 **[SDD](./docs/NC15_SDD_v2.0.pdf)** | *System Design Document*: Architettura del sistema e design pattern. |
| 📊 **[Matrice di Tracciabilità](./docs/NC15_Matrice_Di_Tracciabilità_v2.0.pdf)** | Matrice di tracciabilità tra requisiti e implementazione. |

### 🧪 Testing
I dettagli relativi alle attività di test sono disponibili nella sottocartella [`docs/testing/`](./docs/testing/):

* **[Test Plan (TP)](./docs/testing/NC15_TP_v2.0.pdf)**: Strategia e piano di test.
* **[Test Case Specifications (TCS)](./docs/testing/NC15_TCS_v2.0.pdf)**: Definizione dei casi di test.
* **[Test Incident Report (TIR)](./docs/testing/NC15_TIR_v1.0.pdf)**: Documentazione degli incidenti emersi.
* **[Test Incident Report Tracking (TIRT)](./docs/testing/NC15_TIRT_v1.0.pdf)**: Tracciamento della risoluzione bug.
* **[Test Summary Report (TSR)](./docs/testing/NC15_TSR_v1.0.pdf)**: Risultati finali della fase di test.

---

## 🛠️ Tecnologie Utilizzate
* **Backend:** Java, Spring Boot
* **Build Tool:** Maven
* **Database:** MySQL
* **Frontend:** HTML/CSS/JS, Thymeleaf

---

## 🚀 Istruzioni per l'Installazione

### Prerequisiti
* Java JDK 17+
* Maven
* MySQL Server

### Avvio
1.  Clona la repository:
    ```bash
    git clone [https://github.com/salamoroso7/il-faro-dello-studio.git](https://github.com/salamoroso7/il-faro-dello-studio.git)
    cd il-faro-dello-studio
    ```
2.  Compila il progetto:
    ```bash
    mvn clean install
    ```
3.  Avvia l'applicazione Spring Boot:
    ```bash
    mvn spring-boot:run
    ```

---

## 👥 Il Team (Gruppo NC15)
* **Alfano Daniele**
* **Ambrunzo Aniello Cristiano**
* **Amoroso Salvatore**
* **Della Monica Cristian**
