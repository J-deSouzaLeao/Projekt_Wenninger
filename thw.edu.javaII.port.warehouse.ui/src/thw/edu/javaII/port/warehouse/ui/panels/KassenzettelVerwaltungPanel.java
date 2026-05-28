package thw.edu.javaII.port.warehouse.ui.panels;

import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.ui.BackendClient;
import thw.edu.javaII.port.warehouse.ui.KassenzettelDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse bildet die grafische Benutzeroberfläche zur Ansicht der Kassen-Historie.
 * Sie zeigt eine tabellarische Übersicht aller gespeicherten Kassenzettel (Bons) an.
 * Das Panel bietet eine fortschrittliche Filterfunktion: Textsuche und Zahlart-Dropdown
 * können kombiniert werden, um Bons in Echtzeit zu filtern.
 * Zudem lassen sich per Knopfdruck die einzelnen Positionen (Artikel) eines Bons detailliert anzeigen.
 * * @author juan.de.souza.leao
 */
public class KassenzettelVerwaltungPanel extends JPanel {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(KassenzettelVerwaltungPanel.class.getName());

    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTable table;

    // UI-Elemente für die Filterung
    private final JTextField searchField;
    private final JComboBox<String> zahlartComboBox;

    /**
     * Speichert die aktuell vom Server geladene Liste an Kassenzetteln zwischen,
     * damit beim Klick auf "Details" direkt auf das richtige Objekt zugegriffen werden kann.
     */
    private List<Kassenzettel> aktuelleZettelListe;

    /**
     * Standard-Konstruktor.
     * Baut das Panel auf: Oben die kombinierten Filter (Text & Dropdown),
     * in der Mitte die Tabelle und unten die Aktions-Buttons.
     * Verknüpft die Suchfelder mit dem TableRowSorter für Live-Aktualisierungen.
     */
    public KassenzettelVerwaltungPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Such- und Filterbereich (Oben) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        filterPanel.add(new JLabel("Suchbegriff (Datum, Kassierer, ID):"));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Zahlart:"));
        zahlartComboBox = new JComboBox<>(new String[]{"Alle", "Bar", "Karte"});
        zahlartComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        filterPanel.add(zahlartComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // --- Tabellen-Konfiguration (Mitte) ---
        String[] columns = {"ID", "Datum", "Uhrzeit", "Zahlart", "Kassierer", "Gesamtpreis (€)"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Manuelles Ändern der Bons in der Tabelle ist verboten
            }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Listener für Live-Updates ---
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        zahlartComboBox.addActionListener(e -> applyFilters());

        // --- Aktionsbereich mit Buttons (Unten) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDetails = new JButton("Positionen anzeigen");
        btnDetails.addActionListener(e -> zeigeDetails());
        buttonPanel.add(btnDetails);
        add(buttonPanel, BorderLayout.SOUTH);

        // Daten initial vom Server laden
        loadData();
    }

    /**
     * Liest die aktuellen Werte aus dem Suchfeld und dem Dropdown-Menü aus
     * und kombiniert sie zu einem logischen UND-Filter (AND).
     * Die Tabelle wird in Echtzeit aktualisiert und zeigt nur noch Zeilen,
     * die alle Filterkriterien erfüllen.
     */
    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Text-Filter: Sucht global in allen Spalten nach dem eingegebenen Begriff
        String text = searchField.getText().trim();
        if (!text.isEmpty()) {
            // (?i) macht die Suche unabhängig von Groß- und Kleinschreibung
            filters.add(RowFilter.regexFilter("(?i)" + text));
        }

        // 2. Dropdown-Filter: Sucht exakt in Spalte 3 (Zahlart)
        String selectedZahlart = (String) zahlartComboBox.getSelectedItem();
        if (selectedZahlart != null && !selectedZahlart.equals("Alle")) {
            // ^ und $ stellen sicher, dass exakt das Wort "Bar" oder "Karte" in der Zelle steht
            filters.add(RowFilter.regexFilter("(?i)^" + selectedZahlart + "$", 3));
        }

        // 3. Filter kombinieren und auf den Sorter anwenden
        if (filters.isEmpty()) {
            sorter.setRowFilter(null); // Kein Filter aktiv -> Gesamte Tabelle anzeigen
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters)); // Zeigt nur Ergebnisse, die BEIDE Kriterien erfüllen
        }
    }

    /**
     * Lädt die komplette Historie aller Kassenzettel vom Server herunter.
     * Um eine eindeutige Suche zu garantieren, wird die Datenbank-ID optisch
     * mit führenden Nullen auf 5 Stellen formatiert (z. B. "00001" statt "1").
     */
    private void loadData() {
        model.setRowCount(0);
        try {
            BackendClient client = new BackendClient();
            aktuelleZettelListe = client.getAllKassenzettel();

            for (Kassenzettel z : aktuelleZettelListe) {
                String kassiererName = z.getKassierer() != null ? z.getKassierer().getName() : "Unbekannt";

                // NEU: ID-Formatierung für eindeutige Suchergebnisse
                String formatierteId = String.format("%05d", z.getId());

                model.addRow(new Object[]{
                        formatierteId,
                        z.getDatum(),
                        z.getUhrzeit(),
                        z.getZahlart(),
                        kassiererName,
                        String.format("%.2f", z.getGesamtpreis())
                });
            }
            client.close();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Ein Fehler ist beim Laden der Daten aufgetreten", e);
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Kassenzettel.", "Ladefehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet den Detail-Dialog für den ausgewählten Kassenzettel.
     * Wandelt die formatierte String-ID (z. B. "00001") zurück in einen Integer,
     * um das korrekte Objekt im Zwischenspeicher zu identifizieren.
     */
    private void zeigeDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Bitte wählen Sie zuerst einen Kassenzettel aus der Tabelle aus.",
                    "Hinweis",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        // NEU: Die formatierte ID als Text auslesen und in eine normale Zahl (int) umwandeln
        String idText = (String) model.getValueAt(modelRow, 0);
        int zettelId = Integer.parseInt(idText);

        Kassenzettel ausgewaehlterZettel = null;
        for (Kassenzettel z : aktuelleZettelListe) {
            if (z.getId() == zettelId) {
                ausgewaehlterZettel = z;
                break;
            }
        }

        if (ausgewaehlterZettel != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            new KassenzettelDetailsDialog(parentFrame, ausgewaehlterZettel).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Der Kassenzettel konnte nicht gefunden werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}