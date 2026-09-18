# 📚 BookShelf - Libreria Personale

Un'applicazione desktop in Java per la gestione di una libreria personale, progettata seguendo i principi della programmazione orientata agli oggetti, design pattern consolidati e una GUI moderna in stile dashboard React.

---

## ✨ Funzionalità

- **Gestione Catalogo**:
  - Inserimento di nuovi libri con controllo univocità dell'ISBN.
  - Modifica dello stato di lettura (`DA LEGGERE`, `IN LETTURA`, `LETTO`) e della valutazione a stelle (0-5).
  - Rimozione di libri con richiesta di conferma.
- **Ricerca & Filtraggio Avanzato**:
  - Ricerca istantanea per titolo, autore o codice ISBN.
  - Filtri combinati per **Genere letterario** e **Stato di lettura**.
  - Possibilità di resettare e mostrare l'intero catalogo.
- **Ordinamento Flessibile**:
  - Ordinamento alfabetico per Titolo (A-Z, Z-A).
  - Ordinamento alfabetico per Autore (A-Z, Z-A).
  - Ordinamento per Valutazione (crescente e decrescente).
- **Undo / Redo Completo**:
  - Storico dei comandi con cronologia per annullare e ripristinare aggiunte, modifiche e rimozioni.
- **Persistenza Dati JSON**:
  - Salvataggio e caricamento automatico su file JSON tramite Jackson.
  - Creazione automatica della directory di destinazione se mancante.
- **UI Moderna & Responsiva**:
  - Interfaccia pulita e moderna ispirata al design React/Tailwind.
  - Griglia di card responsive che si adatta fluidamente alla dimensione della finestra (solo scroll verticale).
  - Icone vettoriali nitide su qualsiasi risoluzione/sistema operativo.
  - Badge colorati per lo stato di lettura e rating a stelle vettoriali.

---

## 🏗️ Architettura e Design Pattern

Il progetto adotta un'architettura modulare e diversi pattern di progettazione software:

- **Model-View-Controller (MVC)**: Disaccoppiamento tra il modello dei dati, la logica di controllo e l'interfaccia grafica.
- **Facade Pattern (`FacadeLibreria`)**: Punto d'accesso unificato per la GUI a tutte le funzionalità del sistema (ricerca, filtri, ordinamento, comandi e persistenza).
- **Observer Pattern (`ObserverIF`)**: Notifica automatica alla vista GUI dei cambiamenti avvenuti nel catalogo.
- **Command Pattern (`Command`, `HistoryCommandHandler`)**: Incapsulamento delle operazioni (`AggiungiCommand`, `ModificaCommand`, `RimuoviCommand`) per gestire Undo e Redo.
- **Strategy Pattern (`OrdinamentoStrategy`)**: Algoritmi di ordinamento intercambiabili (`OrdinaPerTitolo`, `OrdinaPerAutore`, `OrdinaPerValutazione`).
- **Chain of Responsibility (`Filtro`, `FiltroPerGenere`, `FiltroPerStato`)**: Catena di filtri per applicare criteri multipli ai libri.

---

## 🛠️ Tecnologie Utilizzate

- **Linguaggio**: Java 21
- **Build Tool**: Apache Maven
- **GUI Framework**: Java Swing (con componenti custom e icone vettoriali `Graphics2D`)
- **JSON Processing**: Jackson (`jackson-databind` 2.15.2)
- **Unit Testing**: JUnit Jupiter 5.10.0

---

## 🚀 Avvio dell'Applicazione

### Prerequisiti
- Java JDK 21 installato
- Maven (o wrapper)

### Compilazione ed Esecuzione

```bash
# Compilazione del progetto
mvn clean compile

# Esecuzione dei test
mvn test

# Avvio dell'applicazione
mvn exec:java -Dexec.mainClass="org.example.Main"
```

All'avvio, se non ancora configurato, l'applicazione consentirà di selezionare il file JSON in cui memorizzare il proprio catalogo.
