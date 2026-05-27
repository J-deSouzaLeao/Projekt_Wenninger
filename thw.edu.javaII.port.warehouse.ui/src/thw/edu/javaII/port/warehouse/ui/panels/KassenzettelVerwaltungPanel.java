package thw.edu.javaII.port.warehouse.ui.panels;

import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Diese Klasse bildet die grafische Benutzeroberfläche zur Ansicht der Kassen-Historie.
 * Sie zeigt eine tabellarische Übersicht aller gespeicherten Kassenzettel (Bons) an.
 * Das Besondere an diesem Panel ist die integrierte Echtzeit-Suchfunktion:
 * Der Benutzer kann direkt nach Datum, Zahlart oder Kassierer filtern,
 * ohne extra auf einen "Suchen"-Button klicken zu müssen.
 * * @author juan.de.souza.leao
 */
public class KassenzettelVerwaltungPanel extends JPanel {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(KassenzettelVerwaltungPanel.class.getName());
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;

    /**
     * Standard-Konstruktor.
     * Baut das Panel auf: Oben ein Suchfeld, in der Mitte die Tabelle.
     * Hier wird außerdem der sogenannte "TableRowSorter" verknüpft, der dafür sorgt,
     * dass die Tabelle bei jedem Tastendruck im Suchfeld sofort live gefiltert wird.
     * Abschließend werden die Daten direkt vom Server geladen.
     */
    public KassenzettelVerwaltungPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Such- und Filterbereich
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Suchen / Filtern:"), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // Tabelle
        String[] columns = {"ID", "Datum", "Uhrzeit", "Zahlart", "Kassierer", "Gesamtpreis (€)"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Manuelles Ändern der Bons in der Tabelle ist verboten
            }
        };

        JTable table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Dynamischer Filter-Listener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null); // Filter aufheben, wenn Suchfeld leer ist
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // (?i) macht es Case-Insensitive (Groß-/Kleinschreibung wird ignoriert)
                }
            }
        });

        loadData();
    }

    /**
     * Lädt die komplette Historie aller Kassenzettel vom Server herunter
     * und fügt sie Zeile für Zeile in das Tabellenmodell ein.
     * Gleichzeitig wird der Preis hier sauber auf zwei Nachkommastellen (z. B. 12,50 €) formatiert.
     */
    private void loadData() {
        try {
            BackendClient client = new BackendClient();
            List<Kassenzettel> list = client.getAllKassenzettel();
            for (Kassenzettel z : list) {
                String kassiererName = z.getKassierer() != null ? z.getKassierer().getName() : "Unbekannt";
                model.addRow(new Object[]{
                        z.getId(),
                        z.getDatum(),
                        z.getUhrzeit(),
                        z.getZahlart(),
                        kassiererName,
                        String.format("%.2f", z.getGesamtpreis()) // Preis wird für die Ansicht formatiert
                });
            }
            client.close();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Ein Fehler ist beim Laden der Daten aufgetreten", e);
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Kassenzettel.");
        }
    }
}