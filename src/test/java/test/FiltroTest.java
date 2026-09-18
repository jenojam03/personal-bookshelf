package test;

import chain.Filtro;
import chain.FiltroPerGenere;
import chain.FiltroPerStato;
import model.Genere;
import model.Libro;
import model.StatoLettura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FiltroTest {

    private List<Libro> libri;

    @BeforeEach
    void setup() {
        libri = new ArrayList<>();
        libri.add(new Libro("Titolo1", "Autore1", "ISBN1", Genere.ROMANZO));
        libri.get(0).setStatoLettura(StatoLettura.LETTO);
        libri.add(new Libro("Titolo2", "Autore2", "ISBN2", Genere.FANTASY));
        libri.get(1).setStatoLettura(StatoLettura.IN_LETTURA);
        libri.add(new Libro("Titolo3", "Autore3", "ISBN3", Genere.ROMANZO));
        libri.get(2).setStatoLettura(StatoLettura.DA_LEGGERE);
    }

    @Test
    void testFiltroPerGenere_Corrispondente() {
        Filtro filtro = new FiltroPerGenere(Genere.ROMANZO);
        List<Libro> filtrati = filtro.filtra(libri);
        assertEquals(2, filtrati.size());
        assertTrue(filtrati.stream().allMatch(l -> l.getGenere() == Genere.ROMANZO));
    }

    @Test
    void testFiltroPerGenere_NonCorrispondente() {
        Filtro filtro = new FiltroPerGenere(Genere.HORROR); //non presente
        List<Libro> filtrati = filtro.filtra(libri);
        assertTrue(filtrati.isEmpty());
    }

    @Test
    void testFiltroPerGenere_Null() {
        Filtro filtro = new FiltroPerGenere(null);
        List<Libro> filtrati = filtro.filtra(libri);
        assertEquals(libri.size(), filtrati.size()); //nessun filtro
    }

    @Test
    void testFiltroPerStato_Corrispondente() {
        Filtro filtro = new FiltroPerStato(StatoLettura.LETTO);
        List<Libro> filtrati = filtro.filtra(libri);
        assertEquals(1, filtrati.size());
        assertEquals(StatoLettura.LETTO, filtrati.get(0).getStatoLettura());
    }

    @Test
    void testFiltroPerStato_NonCorrispondente() {
        libri.get(2).setStatoLettura(StatoLettura.LETTO);
        Filtro filtro = new FiltroPerStato(StatoLettura.DA_LEGGERE);
        List<Libro> filtrati = filtro.filtra(libri);
        assertTrue(filtrati.isEmpty());
    }

    @Test
    void testFiltroPerStato_Null() {
        Filtro filtro = new FiltroPerStato(null);
        List<Libro> filtrati = filtro.filtra(libri);
        assertEquals(libri.size(), filtrati.size());
    }

    @Test
    void testFiltroChain_GenereEStato() {
        Filtro filtro = new FiltroPerGenere(Genere.ROMANZO);
        filtro.setNext(new FiltroPerStato(StatoLettura.LETTO));

        List<Libro> filtrati = filtro.filtra(libri);
        assertEquals(1, filtrati.size());
        assertEquals("ISBN1", filtrati.get(0).getISBN());
    }

    @Test
    void testFiltroChain_GenereEStato_SenzaRisultati() {
        Filtro filtro = new FiltroPerGenere(Genere.ROMANZO);
        filtro.setNext(new FiltroPerStato(StatoLettura.IN_LETTURA));
        List<Libro> filtrati = filtro.filtra(libri);
        assertTrue(filtrati.isEmpty());
    }

    @Test
    void testFiltroChain_SoloGenere() {
        Filtro filtro = new FiltroPerGenere(Genere.FANTASY);
        List<Libro> filtrati = filtro.filtra(libri);
        assertEquals(1, filtrati.size());
        assertEquals("ISBN2", filtrati.get(0).getISBN());
    }

    @Test
    void testFiltroConListaVuota() {
        Filtro filtro = new FiltroPerGenere(Genere.ROMANZO);
        List<Libro> filtrati = filtro.filtra(new ArrayList<>());
        assertTrue(filtrati.isEmpty());
    }
}
