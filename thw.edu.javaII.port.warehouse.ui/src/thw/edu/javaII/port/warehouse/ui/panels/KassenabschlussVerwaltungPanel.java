package thw.edu.javaII.port.warehouse.ui.panels;

import thw.edu.javaII.port.warehouse.model.Kassenabschluss;
import thw.edu.javaII.port.warehouse.ui.common.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Ein Verwaltungs-Panel für das Hauptfenster, das alle Kassenabschlüsse auflistet.
 * Bietet eine intelligente Suchfunktion, die durch ID-Formatierung (z.B. ABS-00012)
 * präzise Ergebnisse liefert.
 * * @author juan.de.souza.leao
 */
public class KassenabschlussVerwaltungPanel extends JPanel {

    private final Session ses;
    private final DefaultTableModel tableModel;
    private List<Kassenabschluss> alleAbschluesse = new ArrayList<>();

    /**
     * Konstruktor: Baut das Layout auf und lädt sofort die Daten vom Server.
     * @param ses Die aktuelle Benutzersitzung (für den Netzwerkzugriff).
     */
    public KassenabschlussVerwaltungPanel(Session ses) {
        this.ses = ses;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Kopfbereich mit Titel und Suchleiste
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Kassenabschlüsse (Historie)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Suchen (z.B. ABS-00001 oder Datum):"));
        JTextField txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Tabellen-Setup
        String[] columns = {"Beleg-Nr.", "Datum", "Uhrzeit", "Kassierer", "Soll-Bestand", "Ist-Bestand", "Differenz"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Lade die Rohdaten vom Server
        ladeDaten();

        // Suchfunktion aktivieren (Live-Filterung beim Tippen)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { filterTable(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { filterTable(txtSearch.getText()); }
        });
    }

    /**
     * Ruft die Liste aller Abschlüsse über den Communicator ab und aktualisiert die Ansicht.
     */
    private void ladeDaten() {
        alleAbschluesse = ses.getCommunicator().getAllKassenabschluesse();
        filterTable(""); // Zeigt initial alle Daten an
    }

    /**
     * Filtert die Tabelle basierend auf einem Suchtext.
     * Formatiert die Datenbank-ID zu einer suchbaren Belegnummer (ABS-XXXXX).
     * @param search Der eingegebene Suchbegriff.
     */
    private void filterTable(String search) {
        tableModel.setRowCount(0); // Tabelle leeren
        String lowercaseSearch = search.toLowerCase();

        for (Kassenabschluss a : alleAbschluesse) {
            // FORMATIERUNG: Aus ID '7' wird 'ABS-00007' (Garantiert hohe Stellenwerte für die Suche!)
            String belegNr = String.format("ABS-%05d", a.getId());
            String kassiererName = (a.getKassierer() != null) ? a.getKassierer().getName() : "Unbekannt";

            double differenz = a.getIstBestand() - a.getSollBestand();

            // Erstelle einen riesigen String aus allen Daten, um ihn zu durchsuchen
            String suchString = (belegNr + " " + a.getDatum() + " " + a.getUhrzeit() + " " + kassiererName).toLowerCase();

            if (suchString.contains(lowercaseSearch)) {
                tableModel.addRow(new Object[]{
                        belegNr,
                        a.getDatum(),
                        a.getUhrzeit(),
                        kassiererName,
                        String.format("%.2f €", a.getSollBestand()),
                        String.format("%.2f €", a.getIstBestand()),
                        String.format("%.2f €", differenz)
                });
            }
        }
    }
}