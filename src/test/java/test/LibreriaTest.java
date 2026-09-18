package test;

import model.*;
import UI.ObserverIF;
import strategy.OrdinamentoStrategy;
import strategy.OrdinaPerTitolo;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LibreriaTest {

    private Libreria libreria;
    private Libro l1, l2, l3, l4;

    static class DummyObserver implements ObserverIF {
        boolean updated = false;
        public void update() {
            updated = true;
        }
    }

    @BeforeEach
    void setUp() {
        libreria = new Libreria();
        l1 = new Libro("Piccole donne", "Louisa May Alcott", "123456789", Genere.ROMANZO, StatoLettura.LETTO, 5);
        l2 = new Libro("Storia del nuovo cognome", "Elena Ferrante", "123789923", Genere.ROMANZO, StatoLettura.IN_LETTURA, 5);
        l3 = new Libro("Il codice Da Vinci", "Dan Brown", "987654321", Genere.GIALLO, StatoLettura.LETTO, 5);
        l4 = new Libro("Storia della bambina perduta", "Elena Ferrante", "3249742314", Genere.ROMANZO, StatoLettura.LETTO, 4);

    }

    @Test
    void testAggiungiLibro() {
        assertTrue(libreria.aggiungiLibro(l1));
        assertEquals(l1, libreria.getLibro("123456789"));
        assertFalse(libreria.aggiungiLibro(l1)); //controllo duplicati
    }

    @Test
    void testRimuoviLibro() {
        libreria.aggiungiLibro(l1);
        assertEquals(1, libreria.getTuttiILibri().size());
        libreria.rimuoviLibro("123456789");
        assertNull(libreria.getLibro("123456789"));
    }

    @Test
    void testModificaLibro() {
        libreria.aggiungiLibro(l3);
        Libro modificato = new Libro("Angeli e demoni", "Dan Brown", "987654321", Genere.GIALLO);
        libreria.modificaLibro("987654321", modificato);
        assertEquals("Angeli e demoni", libreria.getLibro("987654321").getTitolo());
    }

    @Test
    void testMostraTutti() {
        libreria.aggiungiLibro(l1);
        libreria.aggiungiLibro(l2);
        libreria.aggiungiLibro(l3);
        libreria.aggiungiLibro(l4);
        libreria.mostraTutti();
        List<Libro> visualizzati = libreria.getDaVisualizzare();
        assertTrue(visualizzati.contains(l1));
        assertTrue(visualizzati.contains(l2));
        assertTrue(visualizzati.contains(l3));
        assertTrue(visualizzati.contains(l4));

    }

    @Test
    void testRicercaPerTitoloAutoreIsbn() {
        libreria.aggiungiLibro(l1);
        libreria.aggiungiLibro(l2);
        libreria.aggiungiLibro(l3);

        libreria.ricerca("piccol");
        assertTrue(libreria.getDaVisualizzare().contains(l1));

        libreria.ricerca("elena");
        assertTrue(libreria.getDaVisualizzare().contains(l2));

        libreria.ricerca("987654321");
        assertTrue(libreria.getDaVisualizzare().contains(l3));
    }

    @Test
    void testFiltra() {
        l1.setStatoLettura(StatoLettura.LETTO);
        l2.setStatoLettura(StatoLettura.DA_LEGGERE);
        l3.setStatoLettura(StatoLettura.LETTO);
        libreria.aggiungiLibro(l1);
        libreria.aggiungiLibro(l2);
        libreria.aggiungiLibro(l3);

        List<Libro> daFiltrare = new ArrayList<>(List.of(l1, l2,l3));
        libreria.filtra(Genere.GIALLO, StatoLettura.LETTO, daFiltrare);

        List<Libro> filtrati = libreria.getDaVisualizzare();
        assertEquals(1, filtrati.size());
        assertEquals(l3, filtrati.get(0));
    }

    @Test
    void testOrdina() {
        l1 = new Libro("Zeta", "AutoreZ", "ISBN003", Genere.SAGGIO);
        l2 = new Libro("Alfa", "AutoreA", "ISBN004", Genere.SAGGIO);
        List<Libro> daOrdinare = new ArrayList<>(List.of(l1, l2));

        OrdinamentoStrategy strategia = new OrdinaPerTitolo();
        libreria.ordina(strategia, daOrdinare, true);
        assertEquals("Alfa", libreria.getDaVisualizzare().get(0).getTitolo());
    }

    @Test
    void testSetLibri() {
        Map<String, Libro> nuovi = new LinkedHashMap<>();
        nuovi.put(l1.getISBN(), l1);
        nuovi.put(l2.getISBN(), l2);
        nuovi.put(l3.getISBN(), l3);
        libreria.setLibri(nuovi);
        assertEquals(3, libreria.getTuttiILibri().size());
    }

    @Test
    void testOrdineInserimentoLinkedHashMap() {
        libreria.aggiungiLibro(l2);
        libreria.aggiungiLibro(l1);
        libreria.aggiungiLibro(l3);

        List<Libro> visualizzati = libreria.getDaVisualizzare();
        assertEquals(l2, visualizzati.get(0));
        assertEquals(l1, visualizzati.get(1));
        assertEquals(l3, visualizzati.get(2));
    }

    @Test
    void testObservers() {
        DummyObserver obs = new DummyObserver();
        libreria.attach(obs);
        libreria.aggiungiLibro(l1);
        assertTrue(obs.updated);

        obs.updated = false;
        libreria.detach(obs);
        libreria.aggiungiLibro(l2);
        assertFalse(obs.updated);
    }
}

