package UI;

import controller.FacadeLibreria;
import model.*;
import persistence.ConfigManager;
import strategy.OrdinaPerAutore;
import strategy.OrdinaPerTitolo;
import strategy.OrdinaPerValutazione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame implements ObserverIF {

    private FacadeLibreria facade;
    private JPanel cardsPanel;
    private List<Libro> libriVisualizzati = new ArrayList<>();
    private JButton undoButton;
    private JButton redoButton;
    private JLabel countLabel;

    // Per il filtraggio
    private Genere filtroGenere;
    private StatoLettura filtroStato;

    public GUI() {
        UITheme.applyGlobalStyles();
        try {
            if (ConfigManager.caricaPercorso() == null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleziona percorso di salvataggio");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setApproveButtonText("Salva");

                int result = fileChooser.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();

                    if (!filePath.toLowerCase().endsWith(".json")) {
                        filePath += ".json";
                    }
                    ConfigManager.salvaPercorso(filePath);
                } else {
                    JOptionPane.showMessageDialog(null, "Nessun percorso selezionato. L'app verrà chiusa.");
                    System.exit(0);
                }
            }

            this.facade = new FacadeLibreria();
            facade.getLibreria().attach(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        libriVisualizzati = facade.getDaVisualizzare();
        updateCards(libriVisualizzati);
    }

    public void costruisciInterfaccia() {
        JFrame frame = new JFrame("Personal Bookshelf");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(UITheme.BG_MAIN);

        // Header / Navbar moderna
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(16, 24, 16, 24)
        ));

        // Brand / Logo a sinistra
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);
        JLabel logoIcon = new JLabel(ModernIcons.createBookLogoIcon(24, 24));
        JLabel titleLabel = new JLabel("BookShelf");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        countLabel = new JLabel("(0 libri)");
        countLabel.setFont(UITheme.FONT_REGULAR);
        countLabel.setForeground(UITheme.TEXT_SECONDARY);

        brandPanel.add(logoIcon);
        brandPanel.add(titleLabel);
        brandPanel.add(countLabel);

        // Action controls
        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActions.setOpaque(false);

        JButton aggiungiBtn = UITheme.createButton("+ Nuovo Libro", true);
        aggiungiBtn.addActionListener(ev -> {
            mostraFinestraAggiunta();
            aggiornaBottoniUndoRedo();
        });

        topActions.add(aggiungiBtn);

        headerPanel.add(brandPanel, BorderLayout.WEST);
        headerPanel.add(topActions, BorderLayout.EAST);

        // Sub-bar di filtri, ricerca e ordinamento
        JPanel toolbarPanel = new JPanel(new BorderLayout(15, 0));
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 24, 12, 24)
        ));

        // Cerca + Reset a sinistra
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchControls.setOpaque(false);

        JTextField searchField = UITheme.createTextField(22);
        searchField.setToolTipText("Cerca per titolo, autore o ISBN...");

        JButton cercaBtn = UITheme.createButton("Cerca", ModernIcons.createSearchIcon(15, UITheme.TEXT_PRIMARY), false);
        cercaBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                facade.setRicerca(true);
                facade.ricerca(query);
            }
        });
        searchField.addActionListener(e -> cercaBtn.doClick());

        JButton indietroButton = UITheme.createButton("Tutti i libri", false);
        indietroButton.addActionListener(e -> {
            facade.mostraTutti();
            filtroStato = null;
            filtroGenere = null;
            facade.setFiltroAttivo(false);
            facade.setFiltroGenere(null);
            facade.setFiltroStato(null);
            facade.setRicerca(false);
            facade.setParola(null);
            aggiungiBtn.setEnabled(true);
            searchField.setText("");
        });

        searchControls.add(new JLabel(ModernIcons.createSearchIcon(16, UITheme.TEXT_MUTED)));
        searchControls.add(searchField);
        searchControls.add(cercaBtn);
        searchControls.add(indietroButton);

        // Filtro + Ordina a destra
        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterControls.setOpaque(false);

        JButton filtroBtn = UITheme.createButton("Filtri...", false);
        filtroBtn.addActionListener(e -> mostraFinestraFiltri());

        String[] ordini = {
                "Titolo (A-Z)", "Titolo (Z-A)",
                "Autore (A-Z)", "Autore (Z-A)",
                "Valutazione (crescente)", "Valutazione (decrescente)"
        };
        JComboBox<String> ordinaBox = new JComboBox<>(ordini);
        ordinaBox.setFont(UITheme.FONT_REGULAR);
        ordinaBox.setBackground(Color.WHITE);
        ordinaBox.setSelectedItem(null);

        ordinaBox.addActionListener(eve -> {
            String criterio = (String) ordinaBox.getSelectedItem();
            if (criterio == null) return;
            switch (criterio) {
                case "Titolo (A-Z)" -> facade.ordina(new OrdinaPerTitolo(), true);
                case "Titolo (Z-A)" -> facade.ordina(new OrdinaPerTitolo(), false);
                case "Autore (A-Z)" -> facade.ordina(new OrdinaPerAutore(), true);
                case "Autore (Z-A)" -> facade.ordina(new OrdinaPerAutore(), false);
                case "Valutazione (crescente)" -> facade.ordina(new OrdinaPerValutazione(), true);
                case "Valutazione (decrescente)" -> facade.ordina(new OrdinaPerValutazione(), false);
            }
        });

        JLabel ordinaLabel = new JLabel("Ordina per:");
        ordinaLabel.setFont(UITheme.FONT_REGULAR);
        ordinaLabel.setForeground(UITheme.TEXT_SECONDARY);

        filterControls.add(filtroBtn);
        filterControls.add(ordinaLabel);
        filterControls.add(ordinaBox);

        toolbarPanel.add(searchControls, BorderLayout.WEST);
        toolbarPanel.add(filterControls, BorderLayout.EAST);

        // Aggrego Header superiore
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(toolbarPanel, BorderLayout.SOUTH);

        // Area schede / card libri
        cardsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(UITheme.BG_MAIN);
        cardsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.BG_MAIN);
        scrollPane.getViewport().setBackground(UITheme.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Listener per garantire il reflow dinamico e immediato delle card quando la finestra viene ridimensionata
        cardsPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                cardsPanel.revalidate();
            }
        });

        // Footer in stile bottom bar con Undo/Redo
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(10, 24, 10, 24)
        ));

        undoButton = UITheme.createButton("Undo", ModernIcons.createUndoIcon(16, UITheme.TEXT_PRIMARY), false);
        redoButton = UITheme.createButton("Redo", ModernIcons.createRedoIcon(16, UITheme.TEXT_PRIMARY), false);

        undoButton.addActionListener(e -> {
            try {
                facade.undo();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            aggiornaBottoniUndoRedo();
        });

        redoButton.addActionListener(e -> {
            try {
                facade.redo();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            aggiornaBottoniUndoRedo();
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(undoButton);
        leftPanel.add(redoButton);

        JLabel tipLabel = new JLabel("Clicca su una card per visualizzare i dettagli, modificarla o eliminarla");
        tipLabel.setFont(UITheme.FONT_SMALL);
        tipLabel.setForeground(UITheme.TEXT_MUTED);

        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(tipLabel, BorderLayout.EAST);

        frame.add(northContainer, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        aggiornaBottoniUndoRedo();
        facade.mostraTutti();

        frame.setSize(1080, 750);
        frame.setMinimumSize(new Dimension(650, 500));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateCards(List<Libro> libri) {
        cardsPanel.removeAll();

        if (countLabel != null) {
            countLabel.setText("(" + libri.size() + (libri.size() == 1 ? " libro" : " libri") + ")");
        }

        if (libri.isEmpty()) {
            cardsPanel.setLayout(new GridBagLayout());

            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);

            JLabel emptyIcon = new JLabel(ModernIcons.createEmptyBookIcon(54));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyTitle = new JLabel("Nessun libro trovato");
            emptyTitle.setFont(UITheme.FONT_TITLE);
            emptyTitle.setForeground(UITheme.TEXT_PRIMARY);
            emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptySub = new JLabel("Prova a modificare i filtri o aggiungi un nuovo libro.");
            emptySub.setFont(UITheme.FONT_REGULAR);
            emptySub.setForeground(UITheme.TEXT_SECONDARY);
            emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(14));
            emptyPanel.add(emptyTitle);
            emptyPanel.add(Box.createVerticalStrut(8));
            emptyPanel.add(emptySub);

            cardsPanel.add(emptyPanel, new GridBagConstraints());
        } else {
            cardsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
            for (Libro libro : libri) {
                JPanel card = createBookCard(libro);
                cardsPanel.add(card);
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createBookCard(Libro libro) {
        JPanel card = new JPanel() {
            private boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        mostraFinestraModifica(libro);
                        aggiornaBottoniUndoRedo();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Sfondo card
                g2.setColor(hover ? UITheme.BG_CARD_HOVER : UITheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14));

                // Bordo card
                g2.setColor(hover ? UITheme.BORDER_FOCUS : UITheme.BORDER_COLOR);
                g2.setStroke(new BasicStroke(hover ? 1.5f : 1.0f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setLayout(new BorderLayout(8, 8));
        card.setPreferredSize(new Dimension(230, 160));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Top card: badge stato e valutazione a stelle
        JPanel topCard = new JPanel(new BorderLayout());
        topCard.setOpaque(false);

        JPanel badge = createBadge(libro.getStatoLettura());
        JLabel starsLabel = new JLabel(ModernIcons.createStarRatingIcon(libro.getValutazione(), 5, 12));

        topCard.add(badge, BorderLayout.WEST);
        topCard.add(starsLabel, BorderLayout.EAST);

        // Centro card: Titolo e Autore
        JPanel centerCard = new JPanel();
        centerCard.setLayout(new BoxLayout(centerCard, BoxLayout.Y_AXIS));
        centerCard.setOpaque(false);

        JLabel titoloLabel = new JLabel(libro.getTitolo());
        titoloLabel.setFont(UITheme.FONT_CARD_TITLE);
        titoloLabel.setForeground(UITheme.TEXT_PRIMARY);
        titoloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel autoreLabel = new JLabel("di " + libro.getAutore());
        autoreLabel.setFont(UITheme.FONT_REGULAR);
        autoreLabel.setForeground(UITheme.TEXT_SECONDARY);
        autoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerCard.add(Box.createVerticalStrut(4));
        centerCard.add(titoloLabel);
        centerCard.add(Box.createVerticalStrut(4));
        centerCard.add(autoreLabel);

        // Bottom card: Genere e ISBN
        JPanel bottomCard = new JPanel(new BorderLayout());
        bottomCard.setOpaque(false);

        JLabel genereLabel = new JLabel(libro.getGenere() != null ? libro.getGenere().name() : "");
        genereLabel.setFont(UITheme.FONT_SMALL);
        genereLabel.setForeground(UITheme.PRIMARY);

        JLabel isbnLabel = new JLabel("ISBN: " + libro.getISBN());
        isbnLabel.setFont(UITheme.FONT_SMALL);
        isbnLabel.setForeground(UITheme.TEXT_MUTED);

        bottomCard.add(genereLabel, BorderLayout.WEST);
        bottomCard.add(isbnLabel, BorderLayout.EAST);

        card.add(topCard, BorderLayout.NORTH);
        card.add(centerCard, BorderLayout.CENTER);
        card.add(bottomCard, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createBadge(StatoLettura stato) {
        Color bg = UITheme.BADGE_TODO_BG;
        Color text = UITheme.BADGE_TODO_TEXT;
        String label = "DA LEGGERE";

        if (stato == StatoLettura.LETTO) {
            bg = UITheme.BADGE_READ_BG;
            text = UITheme.BADGE_READ_TEXT;
            label = "LETTO";
        } else if (stato == StatoLettura.IN_LETTURA) {
            bg = UITheme.BADGE_READING_BG;
            text = UITheme.BADGE_READING_TEXT;
            label = "IN LETTURA";
        }

        final Color finalBg = bg;
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(finalBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(2, 8, 2, 8));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BADGE);
        lbl.setForeground(text);
        badge.add(lbl);
        return badge;
    }

    private String getStarsRepresentation(int rating) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= rating ? "★" : "☆");
        }
        return sb.toString();
    }

    private void mostraFinestraAggiunta() {
        JDialog dialog = new JDialog(this, "Aggiungi nuovo libro", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel content = new JPanel(new GridLayout(0, 2, 12, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField titolo = UITheme.createTextField(15);
        JTextField autore = UITheme.createTextField(15);
        JTextField isbn = UITheme.createTextField(15);
        JComboBox<Genere> genereBox = new JComboBox<>(Genere.values());
        genereBox.setFont(UITheme.FONT_REGULAR);
        genereBox.setBackground(Color.WHITE);

        content.add(createFormLabel("Titolo:"));
        content.add(titolo);
        content.add(createFormLabel("Autore:"));
        content.add(autore);
        content.add(createFormLabel("ISBN:"));
        content.add(isbn);
        content.add(createFormLabel("Genere:"));
        content.add(genereBox);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        JButton annulla = UITheme.createButton("Annulla", false);
        annulla.addActionListener(e -> dialog.dispose());

        JButton salva = UITheme.createButton("Salva libro", true);
        salva.addActionListener(e -> {
            String t = titolo.getText().trim();
            String a = autore.getText().trim();
            String i = isbn.getText().trim();

            if (t.isEmpty() || a.isEmpty() || i.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tutti i campi sono obbligatori.", "Dati mancanti", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Libro libro = new Libro(t, a, i, (Genere) genereBox.getSelectedItem());

            try {
                boolean ret = facade.aggiungiLibro(libro);
                if (!ret) {
                    JOptionPane.showMessageDialog(dialog, "Il libro inserito esiste già. Correggi l'ISBN.", "Libro duplicato", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Errore durante l'aggiunta del libro.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
        });

        btnPanel.add(annulla);
        btnPanel.add(salva);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostraFinestraModifica(Libro libro) {
        JDialog dialog = new JDialog(this, "Dettagli e Modifica Libro", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel content = new JPanel(new GridLayout(0, 2, 12, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField titolo = UITheme.createTextField(15);
        titolo.setText(libro.getTitolo());
        titolo.setEditable(false);

        JTextField autore = UITheme.createTextField(15);
        autore.setText(libro.getAutore());
        autore.setEditable(false);

        JTextField isbn = UITheme.createTextField(15);
        isbn.setText(libro.getISBN());
        isbn.setEditable(false);

        JComboBox<StatoLettura> statoBox = new JComboBox<>(StatoLettura.values());
        statoBox.setSelectedItem(libro.getStatoLettura());
        statoBox.setBackground(Color.WHITE);
        statoBox.setFont(UITheme.FONT_REGULAR);

        JSpinner valutazioneSpinner = new JSpinner(new SpinnerNumberModel(libro.getValutazione(), 0, 5, 1));
        valutazioneSpinner.setFont(UITheme.FONT_REGULAR);

        content.add(createFormLabel("Titolo:"));
        content.add(titolo);
        content.add(createFormLabel("Autore:"));
        content.add(autore);
        content.add(createFormLabel("ISBN:"));
        content.add(isbn);
        content.add(createFormLabel("Stato lettura:"));
        content.add(statoBox);
        content.add(createFormLabel("Valutazione (0-5):"));
        content.add(valutazioneSpinner);

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JButton rimuovi = UITheme.createDangerButton("Elimina Libro");
        rimuovi.addActionListener(e -> {
            int scelta = JOptionPane.showConfirmDialog(dialog, "Sei sicuro di voler rimuovere il libro?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);
            if (scelta == JOptionPane.YES_OPTION) {
                try {
                    facade.rimuoviLibro(libro.getISBN());
                    aggiornaBottoniUndoRedo();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Errore durante la rimozione.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
                dialog.dispose();
            }
        });

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);

        JButton annulla = UITheme.createButton("Annulla", false);
        annulla.addActionListener(e -> dialog.dispose());

        JButton salva = UITheme.createButton("Salva Modifiche", true);
        salva.addActionListener(e -> {
            Libro aggiornato = new Libro(
                    libro.getTitolo(),
                    libro.getAutore(),
                    libro.getISBN(),
                    libro.getGenere(),
                    (StatoLettura) statoBox.getSelectedItem(),
                    (Integer) valutazioneSpinner.getValue()
            );

            try {
                facade.modificaLibro(libro.getISBN(), aggiornato);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Errore durante la modifica.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
            aggiornaBottoniUndoRedo();
        });

        rightButtons.add(annulla);
        rightButtons.add(salva);

        btnPanel.add(rimuovi, BorderLayout.WEST);
        btnPanel.add(rightButtons, BorderLayout.EAST);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void mostraFinestraFiltri() {
        JDialog dialog = new JDialog(this, "Filtra catalogo", true);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel filtriPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        filtriPanel.setOpaque(false);
        filtriPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // GENERE
        JPanel generePanel = new JPanel();
        generePanel.setLayout(new BoxLayout(generePanel, BoxLayout.Y_AXIS));
        generePanel.setBackground(Color.WHITE);
        generePanel.setBorder(BorderFactory.createCompoundBorder(
                new UITheme.RoundedBorder(8, UITheme.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel gTitle = new JLabel("Genere");
        gTitle.setFont(UITheme.FONT_HEADER);
        generePanel.add(gTitle);
        generePanel.add(Box.createVerticalStrut(8));

        List<JToggleButton> genereButtons = new ArrayList<>();
        final JToggleButton[] selezionatoGenere = {null};

        for (Genere g : Genere.values()) {
            JToggleButton btn = new JToggleButton(g.toString());
            btn.setFont(UITheme.FONT_REGULAR);
            btn.setMaximumSize(new Dimension(200, 28));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (filtroGenere != null && g == filtroGenere) {
                btn.setSelected(true);
                selezionatoGenere[0] = btn;
            }

            btn.addActionListener(e -> {
                if (btn.equals(selezionatoGenere[0])) {
                    btn.setSelected(false);
                    selezionatoGenere[0] = null;
                } else {
                    for (JToggleButton b : genereButtons) {
                        b.setSelected(false);
                    }
                    btn.setSelected(true);
                    selezionatoGenere[0] = btn;
                }
            });

            genereButtons.add(btn);
            generePanel.add(btn);
            generePanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane genereScroll = new JScrollPane(generePanel);
        genereScroll.setBorder(null);
        genereScroll.setOpaque(false);
        genereScroll.getViewport().setOpaque(false);

        // STATO LETTURA
        JPanel statoPanel = new JPanel();
        statoPanel.setLayout(new BoxLayout(statoPanel, BoxLayout.Y_AXIS));
        statoPanel.setBackground(Color.WHITE);
        statoPanel.setBorder(BorderFactory.createCompoundBorder(
                new UITheme.RoundedBorder(8, UITheme.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel sTitle = new JLabel("Stato lettura");
        sTitle.setFont(UITheme.FONT_HEADER);
        statoPanel.add(sTitle);
        statoPanel.add(Box.createVerticalStrut(8));

        List<JToggleButton> statoButtons = new ArrayList<>();
        final JToggleButton[] selezionatoStato = {null};

        for (StatoLettura s : StatoLettura.values()) {
            JToggleButton btn = new JToggleButton(s.toString());
            btn.setFont(UITheme.FONT_REGULAR);
            btn.setMaximumSize(new Dimension(200, 28));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (filtroStato != null && s == filtroStato) {
                btn.setSelected(true);
                selezionatoStato[0] = btn;
            }

            btn.addActionListener(e -> {
                if (btn.equals(selezionatoStato[0])) {
                    btn.setSelected(false);
                    selezionatoStato[0] = null;
                } else {
                    for (JToggleButton b : statoButtons) {
                        b.setSelected(false);
                    }
                    btn.setSelected(true);
                    selezionatoStato[0] = btn;
                }
            });

            statoButtons.add(btn);
            statoPanel.add(btn);
            statoPanel.add(Box.createVerticalStrut(4));
        }

        filtriPanel.add(genereScroll);
        filtriPanel.add(statoPanel);

        // Azioni
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        JButton resetBtn = UITheme.createButton("Resetta", false);
        resetBtn.addActionListener(e -> {
            for (JToggleButton b : genereButtons) b.setSelected(false);
            for (JToggleButton b : statoButtons) b.setSelected(false);
            selezionatoGenere[0] = null;
            selezionatoStato[0] = null;
            filtroGenere = null;
            filtroStato = null;
            facade.setFiltroAttivo(false);
            facade.setFiltroGenere(null);
            facade.setFiltroStato(null);
            facade.mostraTutti();
            dialog.dispose();
        });

        JButton applicaBtn = UITheme.createButton("Applica Filtri", true);
        applicaBtn.addActionListener(e -> {
            filtroGenere = null;
            filtroStato = null;

            if (selezionatoGenere[0] != null) {
                filtroGenere = Genere.valueOf(selezionatoGenere[0].getText());
                facade.setFiltroGenere(filtroGenere);
            }

            if (selezionatoStato[0] != null) {
                filtroStato = StatoLettura.valueOf(selezionatoStato[0].getText());
                facade.setFiltroStato(filtroStato);
            }

            facade.filtra(filtroGenere, filtroStato);
            dialog.dispose();
        });

        btnPanel.add(resetBtn);
        btnPanel.add(applicaBtn);

        dialog.add(filtriPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_MEDIUM);
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
    }

    private void aggiornaBottoniUndoRedo() {
        if (undoButton != null) {
            undoButton.setEnabled(facade.canUndo());
        }
        if (redoButton != null) {
            redoButton.setEnabled(facade.canRedo());
        }
    }
}
