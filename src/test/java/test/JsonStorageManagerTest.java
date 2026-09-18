package test;

import persistence.ConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.junit.jupiter.api.*;
import persistence.JsonStorageManager;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageManagerTest {

    private static File tempFile;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static String percorsoOriginale = ConfigManager.caricaPercorso();

    @BeforeAll
    static void setupClass() throws Exception {

        String percorso = "C:\\Users\\giada\\Desktop\\libri.json";
        tempFile = new File(percorso);
        ConfigManager.salvaPercorso(percorso);
    }

    @AfterEach
    void cleanup() throws IOException {
        new PrintWriter(tempFile).close();
    }

    @Test
    void testSalvataggio() throws IOException {
        Map<String, Libro> libri = new HashMap<>();
        Libro libro = new Libro("1984", "George Orwell", "ISBN1984", Genere.FANTASCIENZA);
        libro.setValutazione(5);
        libri.put(libro.getISBN(), libro);

        JsonStorageManager.salvataggio(libri);


        Map<String, Libro> letti = JsonStorageManager.caricamento();
        assertEquals(1, letti.size());
        assertEquals("1984", letti.get("ISBN1984").getTitolo());
    }

    @Test
    void testCaricamento() throws IOException {
        //scrivo manualmente un file JSON valido
        Libro l = new Libro("1984", "George Orwell", "ISBN1984", null);
        Map<String, Libro> libri = new HashMap<>();
        libri.put("ISBN1984", l);
        JsonStorageManager.salvataggio(libri);

        Map<String, Libro> caricati = JsonStorageManager.caricamento();
        assertEquals(1, caricati.size());
        assertTrue(caricati.containsKey("ISBN1984"));
        assertEquals("George Orwell", caricati.get("ISBN1984").getAutore());
    }

    @Test
    void testCaricamentoQuandoFileNonEsiste() throws Exception {
        // Percorso fittizio verso un file che non esiste
        File nonEsistente = new File("C:\\Users\\giada\\Desktop\\libri_non_esiste.json");
        nonEsistente.delete();
        assertFalse(nonEsistente.exists(), "Il file non dovrebbe esistere");

        // Salvo il percorso nel file config
        ConfigManager.salvaPercorso(nonEsistente.getAbsolutePath());

        // Eseguo il caricamento
        Map<String, Libro> libri = JsonStorageManager.caricamento();

        // Verifico che venga restituito un dizionario vuoto
        assertNotNull(libri, "La mappa restituita non deve essere null");
        assertTrue(libri.isEmpty(), "La mappa dei libri dovrebbe essere vuota perché il file non esiste");
    }

    @AfterAll
    static void ripristinaPercorso(){
        ConfigManager.salvaPercorso(percorsoOriginale);
        tempFile.delete();
    }

}

