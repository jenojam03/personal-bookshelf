# 📚 BookShelf - Personal Library Management System

A Java desktop application for managing a personal book collection, designed following object-oriented programming principles, proven software design patterns, and a modern React/Tailwind-inspired GUI.

---

## ✨ Features

- **Catalog Management**:
  - Add new books with ISBN uniqueness validation.
  - Edit reading status (`DA LEGGERE` / To Read, `IN LETTURA` / Reading, `LETTO` / Read) and star rating (0-5).
  - Delete books with confirmation dialogs.
- **Advanced Search & Filtering**:
  - Real-time search across titles, authors, and ISBN codes.
  - Combined filters by **Literary Genre** and **Reading Status**.
  - Quick reset button to display the full catalog.
- **Flexible Sorting**:
  - Alphabetical sorting by Title (A-Z, Z-A).
  - Alphabetical sorting by Author (A-Z, Z-A).
  - Rating sorting (ascending and descending).
- **Comprehensive Undo / Redo**:
  - Action history management allowing users to undo and redo additions, modifications, and deletions.
- **JSON Data Persistence**:
  - Automatic saving and loading using Jackson JSON processing.
  - Automatic directory creation if the target path does not exist.
- **Modern & Responsive UI**:
  - Clean and polished user interface inspired by modern React dashboards.
  - Responsive card grid that dynamically adapts to window resizing with vertical scrolling only.
  - Crisp vector icons (`Graphics2D`) ensuring high definition across all displays and operating systems.
  - Colored status badges and vector star ratings.

---

## 🏗️ Architecture & Design Patterns

The project follows a modular architecture leveraging several software design patterns:

- **Model-View-Controller (MVC)**: Decouples the domain data model, control logic, and graphical interface.
- **Facade Pattern (`FacadeLibreria`)**: Provides a unified interface for the GUI to interact with all subsystem features (search, filtering, sorting, command history, and persistence).
- **Observer Pattern (`ObserverIF`)**: Automatically notifies the GUI view whenever changes occur in the book collection.
- **Command Pattern (`Command`, `HistoryCommandHandler`)**: Encapsulates actions (`AggiungiCommand`, `ModificaCommand`, `RimuoviCommand`) to support Undo/Redo operations.
- **Strategy Pattern (`OrdinamentoStrategy`)**: Implements interchangeable sorting algorithms (`OrdinaPerTitolo`, `OrdinaPerAutore`, `OrdinaPerValutazione`).
- **Chain of Responsibility (`Filtro`, `FiltroPerGenere`, `FiltroPerStato`)**: Chains filtering criteria to evaluate multiple conditions seamlessly.

---

## 🛠️ Technologies Used

- **Language**: Java 21
- **Build Tool**: Apache Maven
- **GUI Framework**: Java Swing (custom components and vector `Graphics2D` icons)
- **JSON Processing**: Jackson (`jackson-databind` 2.15.2)
- **Unit Testing**: JUnit Jupiter 5.10.0

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 21 installed
- Apache Maven

### Build & Run

```bash
# Compile the project
mvn clean compile

# Run unit tests
mvn test

# Launch the application
mvn exec:java -Dexec.mainClass="org.example.Main"
```

On the first launch (if not yet configured), the application will prompt you to choose the JSON file location where your catalog data will be stored.
