package test;

import controller.FacadeLibreria;
import model.*;
import org.junit.jupiter.api.*;
import strategy.OrdinamentoStrategy;
import strategy.OrdinaPerTitolo;
import persistence.JsonStorageManager;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FacadeLibreriaTest {

    private FacadeLibreria facade;
    private Libro libro1, libro2;

    @BeforeEach
    void setUp() throws IOException {
        JsonStorageManager.salvataggio(new HashMap<>());
        facade = new FacadeLibreria();
        facade.setRicerca(false);
        facade.setFiltroAttivo(false);
        libro1 = new Libro("Piccole donne", "Louisa May Alcott", "123456789", Genere.ROMANZO, StatoLettura.LETTO, 5);
        libro2 = new Libro("Storia del nuovo cognome", "Elena Ferrante", "123789923", Genere.ROMANZO, StatoLettura.IN_LETTURA, 5);
    }

    @Test
    void testAggiungiLibro() throws IOException {
        assertTrue(facade.aggiungiLibro(libro1));
        assertEquals(libro1, facade.getLibroByISBN("123456789"));
    }

    @Test
    void testRimuoviLibro() throws IOException {
        facade.aggiungiLibro(libro1);
        facade.rimuoviLibro("123456789");
        assertNull(facade.getLibroByISBN("123456789"));
    }

    @Test
    void testModificaLibro() throws IOException {
        facade.aggiungiLibro(libro1);
        Libro modificato = new Libro("Piccole donne crescono", "Louisa May Alcott", "123456789", Genere.ROMANZO);
        facade.modificaLibro("123456789", modificato);
        assertEquals("Piccole donne crescono", facade.getLibroByISBN("123456789").getTitolo());
    }

    @Test
    void testUndoRedo() throws IOException {
        facade.aggiungiLibro(libro1);
        facade.undo();
        assertNull(facade.getLibroByISBN("123456789"));

        facade.redo();
        assertNotNull(facade.getLibroByISBN("123456789"));
    }

    @Test
    void testOrdina() throws IOException {
        facade.aggiungiLibro(libro2);
        facade.aggiungiLibro(libro1);
        OrdinamentoStrategy strategia = new OrdinaPerTitolo();

        facade.ordina(strategia, true);
        List<Libro> ordinati = facade.getDaVisualizzare();
        assertEquals("Piccole donne", ordinati.get(0).getTitolo());
    }

    @Test
    void testFiltra() throws IOException {
        libro1.setStatoLettura(StatoLettura.LETTO);
        libro2.setStatoLettura(StatoLettura.DA_LEGGERE);

        facade.aggiungiLibro(libro1);
        facade.aggiungiLibro(libro2);

        facade.filtra(Genere.ROMANZO, StatoLettura.LETTO);

        List<Libro> filtrati = facade.getDaVisualizzare();
        assertEquals(1, filtrati.size());
        assertEquals(libro1, filtrati.get(0));
    }

    @Test
    void testRicerca() throws IOException {
        facade.aggiungiLibro(libro1);
        facade.ricerca("may");

        List<Libro> risultati = facade.getDaVisualizzare();
        assertEquals(1, risultati.size());
        assertEquals(libro1, risultati.get(0));
    }

    @Test
    void testMostraTutti() throws IOException {
        facade.aggiungiLibro(libro1);
        facade.aggiungiLibro(libro2);
        facade.ricerca("may");

        facade.mostraTutti();
        List<Libro> tutti = facade.getDaVisualizzare();
        assertEquals(2, tutti.size());
    }

    @Test
    void testGetters() throws IOException {
        facade.aggiungiLibro(libro1);
        assertEquals(libro1, facade.getLibreria().getLibro("123456789"));
        assertTrue(facade.getLibri().containsKey("123456789"));
        assertTrue(facade.getDaVisualizzare().contains(libro1));
    }
}
