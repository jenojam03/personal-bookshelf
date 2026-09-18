package test;

import command.*;
import model.Genere;
import model.Libro;
import model.Libreria;
import model.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    private Libreria libreria;
    private Libro libro1, libro2;
    private HistoryCommandHandler handler;

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        libro1 = new Libro("Titolo1", "Autore1", "ISBN1", Genere.ROMANZO);
        libro1.setValutazione(4);
        libro1.setStatoLettura(StatoLettura.LETTO);

        libro2 = new Libro("Titolo2", "Autore2", "ISBN1", Genere.SAGGIO);
        libro2.setValutazione(5);
        libro2.setStatoLettura(StatoLettura.IN_LETTURA);

        handler = new HistoryCommandHandler();
    }

    @Test
    void testAggiungiCommand() {
        Command aggiungi = new AggiungiCommand(libreria, libro1);
        assertTrue(handler.handle(aggiungi));
        assertEquals(libro1, libreria.getLibro("ISBN1"));

        handler.undo();
        assertNull(libreria.getLibro("ISBN1"));

        handler.redo();
        assertEquals(libro1, libreria.getLibro("ISBN1"));
    }

    @Test
    void testRimuoviCommand() {
        libreria.aggiungiLibro(libro1);
        assertNotNull(libreria.getLibro("ISBN1"));

        Command rimuovi = new RimuoviCommand(libreria, "ISBN1");
        assertTrue(handler.handle(rimuovi));
        assertNull(libreria.getLibro("ISBN1"));

        handler.undo();
        assertEquals(libro1, libreria.getLibro("ISBN1"));

        handler.redo();
        assertNull(libreria.getLibro("ISBN1"));
    }

    @Test
    void testModificaCommand() {
        libreria.aggiungiLibro(libro1);
        assertEquals("Titolo1", libreria.getLibro("ISBN1").getTitolo());

        Command modifica = new ModificaCommand(libreria, "ISBN1", libro2);
        assertTrue(handler.handle(modifica));
        assertEquals("Titolo2", libreria.getLibro("ISBN1").getTitolo());

        handler.undo();
        assertEquals("Titolo1", libreria.getLibro("ISBN1").getTitolo());

        handler.redo();
        assertEquals("Titolo2", libreria.getLibro("ISBN1").getTitolo());
    }

    @Test
    void testHandleFailsOnInvalidCommand() {
        Command invalido = new RimuoviCommand(libreria, "ISBN_NOT_FOUND");

        assertFalse(handler.handle(invalido));


        handler.undo();
        assertNull(libreria.getLibro("ISBN_NOT_FOUND"));
    }
}
