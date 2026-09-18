package model;
import UI.*;
import chain.Filtro;
import chain.FiltroPerGenere;
import chain.FiltroPerStato;
import strategy.OrdinamentoStrategy;
import java.util.*;


//classe che rappresenta il sistema
public class Libreria {


    private Map<String, Libro> libri = new LinkedHashMap<>(); //archivio di tutti i libri
    private List<ObserverIF> observers = new ArrayList<>(); //supporto al pattern Observer
    private List<Libro> daVisualizzare = new ArrayList<>(libri.values()); //vista da fornire alla GUI

    //variabili di supporto per permettere ricerche e filtraggi innestati
    private boolean ricerca, filtro;
    private String parola;
    private Genere filtroGenere;
    private StatoLettura filtroStato;
    private List<Libro> risultatiFiltro;
    private List<Libro> risultatiRicerca;

    public Libro getLibro(String isbn) {
        return libri.get(isbn);
    }

    public Map<String,Libro> getTuttiILibri() {
        return libri;
    }

    public boolean aggiungiLibro(Libro libro) {
        //prevenzione duplicati
        if(libri.containsKey(libro.getISBN())){
            return false;
        }
        libri.put(libro.getISBN(), libro);
        visualizza();
        /*daVisualizzare.add(libro);
        notifyObservers();*/
        return true;
    }




    public void rimuoviLibro(String isbn) {
        Libro l = libri.remove(isbn);
        daVisualizzare.remove(l);
        notifyObservers();
    }

    private void visualizza(){
        if(ricerca) {
            daVisualizzare.clear();
            List<Libro> lista = new ArrayList<>(libri.values());
            for (Libro l : lista) {
                if (l.getTitolo().toLowerCase().contains(parola.trim().toLowerCase()) ||
                        l.getAutore().toLowerCase().contains(parola.trim().toLowerCase()) ||
                        l.getISBN().contains(parola.trim().toLowerCase())) {
                    daVisualizzare.add(l);
                }
                risultatiRicerca = daVisualizzare;
            }
            if(filtro){
                filtra(filtroGenere, filtroStato,risultatiRicerca);
            }
        }else {
            daVisualizzare = new ArrayList<>(libri.values());
            if (filtro)
                filtra(filtroGenere, filtroStato, daVisualizzare);
        }

        notifyObservers();
    }

    public void modificaLibro(String isbn, Libro libroAggiornato) {
        libri.put(isbn, libroAggiornato);
        visualizza();
    }



    public void mostraTutti(){
        daVisualizzare = new ArrayList<>(libri.values());
        risultatiFiltro = daVisualizzare;
        ricerca = false;
        filtro = false;
        notifyObservers();
    }

    public List<Libro> getDaVisualizzare() {
        return daVisualizzare;
    }

    public void setLibri(Map<String, Libro> libris) {
        libri.clear();
        libri.putAll(libris);
        daVisualizzare = new ArrayList<>(libri.values());
        notifyObservers();
    }


    //RICERCA
    public void ricerca(String criterio) {
        Set<Libro> setTemporaneo = new LinkedHashSet<>();
        parola = criterio;
        Collection<Libro> lista;

        //se c'è un filtro attivo, la ricerca avviene filtrando i libri restituiti dal filtro
        if(filtro)
            lista = risultatiFiltro;
        else
            lista = libri.values();

        for (Libro l : lista) {
            if (l.getTitolo().toLowerCase().contains(criterio.trim().toLowerCase()) ||
                    l.getAutore().toLowerCase().contains(criterio.trim().toLowerCase()) ||
            l.getISBN().contains(criterio.trim().toLowerCase())) {
                setTemporaneo.add(l);
            }
        }


        daVisualizzare = new ArrayList<>(setTemporaneo);
        risultatiRicerca = daVisualizzare;
        notifyObservers();

    }




    //FILTRAGGIO
    public void filtra(Genere genere, StatoLettura stato, List<Libro> daFiltrare) {

        filtro =true;
        Filtro filtro = new FiltroPerGenere(genere);
        filtro.setNext(new FiltroPerStato(stato));
        daVisualizzare = filtro.filtra(daFiltrare);

        //se non è già attiva una ricerca, i risultati restituiti dal filtraggio costituiranno
        //la base per effettuare una nuova ricerca
        if(!ricerca)
            risultatiFiltro = daVisualizzare;
        notifyObservers();

    }



    //ORDINAMENTO
    public void ordina(OrdinamentoStrategy strategia, List<Libro> libri, boolean crescente) {
        strategia.ordina(libri, crescente);
        daVisualizzare = libri;
        notifyObservers();
    }


    //METODI PER OBSERVER
    public void attach(ObserverIF o){
        observers.add(o);
    }

    public void detach(ObserverIF o){
        observers.remove(o);
    }

    public void notifyObservers(){
        for(ObserverIF o : observers){
            o.update();
        }
    }


    public boolean isRicerca() {
        return ricerca;
    }

    public void setRicerca(boolean ricerca) {
        this.ricerca = ricerca;
    }


    public void setFiltro(boolean filtro) {
        this.filtro = filtro;
    }

    public void setFiltroGenere(Genere filtroGenere) {
        this.filtroGenere = filtroGenere;
    }

    public void setFiltroStato(StatoLettura statoLettura) {
        this.filtroStato = statoLettura;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public List<Libro> getRisultatiRicerca() {
        return risultatiRicerca;
    }
}