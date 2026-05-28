package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

/**
 * Diese Klasse bildet die grafische Verwaltungsoberfläche für die Lagerbestände.
 * Hier wird das Kerngeschäft des Lagers verwaltet: Welches Produkt liegt in welcher
 * Stückzahl (Anzahl) auf welchem konkreten Lagerplatz. Die Ansicht bietet eine Übersichtstabelle
 * sowie die nötigen Dialoge, um diese Bestandsdaten anzulegen, zu ändern oder zu löschen.
 * * @author juan.de.souza.leao
 */
public class LagerBestandVerwaltungPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    /**
     * Standard-Konstruktor.
     * Baut das Panel mit der zentralen Datentabelle und den darunterliegenden
     * Aktions-Schaltflächen (Neu, Bearbeiten, Löschen, Aktualisieren) auf.
     * Lädt zudem initial die aktuellen Bestandsdaten vom Server.
     */
    public LagerBestandVerwaltungPanel() {
        setLayout(new BorderLayout());

        // EINDEUTIGE BENENNUNG: Bestands-ID vs. Produkt-ID
        String[] columnNames = {"Bestands-ID", "Anzahl", "Lagerplatz-ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Direkte Bearbeitung in der Zelle deaktivieren
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnAdd = new JButton("Neu");
        var btnEdit = new JButton("Bearbeiten");
        var btnDelete = new JButton("Löschen");
        var btnRefresh = new JButton("Aktualisieren");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> addLagerBestand());
        btnEdit.addActionListener(e -> editLagerBestand());
        btnDelete.addActionListener(e -> deleteLagerBestand());

        loadData();
    }

    /**
     * Ruft alle aktuellen Lagerbestände vom Server ab und füllt damit die Tabelle.
     * Die Tabelle wird vor dem Befüllen komplett geleert, um doppelte Zeilen zu vermeiden.
     */
    private void loadData() {
        tableModel.setRowCount(0);
        try {
            var client = new BackendClient();
            for (var lb : client.getAllLagerBestaende()) {
                // Produkt-ID wird nicht mehr für die Tabelle ausgelesen
                String platzInfo = lb.getLagerplatz_id() != null ? String.valueOf(lb.getLagerplatz_id().getId()) : "Keiner";

                // Übergibt nur noch 3 Werte: Index 0, 1 und 2
                tableModel.addRow(new Object[]{lb.getId(), lb.getAnzahl(), platzInfo});
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden: " + e.getMessage());
        }
    }

    /**
     * Öffnet einen Eingabedialog zum Anlegen eines neuen Lagerbestands.
     * Der Benutzer gibt die IDs für den Bestand, das Produkt und den Lagerplatz sowie die Menge ein.
     */
    private void addLagerBestand() {
        var txtId = new JTextField();
        var txtAnzahl = new JTextField();
        var txtProdId = new JTextField();
        var txtPlatzId = new JTextField();
        Object[] msg = {"Bestands-ID:", txtId, "Anzahl:", txtAnzahl, "Lagerplatz-ID:", txtPlatzId};

        if (JOptionPane.showConfirmDialog(this, msg, "Neuer Bestand", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                saveLagerBestand(id, txtAnzahl.getText(), txtProdId.getText(), txtPlatzId.getText(), false);
            } catch (NumberFormatException ex) {
                showError("Ungültige Eingabe für IDs oder Anzahl.");
            }
        }
    }

    /**
     * Öffnet einen Dialog zur Bearbeitung eines zuvor in der Tabelle markierten Bestands.
     * Lädt die bestehenden Werte aus der Tabelle in die Eingabefelder vor.
     */
    private void editLagerBestand() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Bitte einen Bestand auswählen."); return; }

        int id = (int) tableModel.getValueAt(row, 0); // Bestands-ID
        var txtAnzahl = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtPlatzId = new JTextField(tableModel.getValueAt(row, 2).toString()); // Ist jetzt auf Index 2!

        // Da Bestands-ID und Produkt-ID denselben Inhalt haben, nehmen wir einfach die 'id'
        var txtProdId = new JTextField(String.valueOf(id));

        Object[] msg = {"Bestands-ID: " + id, "Anzahl:", txtAnzahl, "Produkt-ID:", txtProdId, "Lagerplatz-ID:", txtPlatzId};

        if (JOptionPane.showConfirmDialog(this, msg, "Bestand bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            saveLagerBestand(id, txtAnzahl.getText(), txtProdId.getText(), txtPlatzId.getText(), true);
        }
    }

    /**
     * Zentrale Hilfsmethode, um einen neuen oder bearbeiteten Lagerbestand auf dem Server zu speichern.
     * Prüft zunächst, ob die angegebenen IDs für das Produkt und den Lagerplatz tatsächlich existieren,
     * bevor das Bestandsobjekt erstellt und via Client verschickt wird.
     * * @param id Die ID des Datensatzes.
     * @param anzahlText Die gelagerte Stückzahl als Text.
     * @param prodIdText Die ID des verknüpften Produkts als Text.
     * @param platzIdText Die ID des verknüpften Lagerplatzes als Text.
     * @param isUpdate Gibt an, ob es ein bestehender Eintrag ist (true) oder ein neuer (false).
     */
    private void saveLagerBestand(int id, String anzahlText, String prodIdText, String platzIdText, boolean isUpdate) {
        try {
            var client = new BackendClient();
            Produkt p = getProduktById(client, Integer.parseInt(prodIdText.trim()));
            LagerPlatz lp = getLagerPlatzById(client, Integer.parseInt(platzIdText.trim()));

            if (p == null || lp == null) {
                showError("Produkt oder Lagerplatz nicht gefunden.");
                client.close();
                return;
            }

            var lb = new LagerBestand(id, Integer.parseInt(anzahlText.trim()), p, lp);
            boolean success = isUpdate ? client.updateLagerBestand(lb) : client.addLagerBestand(lb);
            if (success) loadData();
            client.close();
        } catch (Exception ex) {
            showError("Ungültige Eingabe.");
        }
    }

    /**
     * Löscht den aktuell markierten Lagerbestand aus dem System.
     * Fordert zuvor eine Bestätigung vom Benutzer an, um versehentliches Löschen zu verhindern.
     */
    private void deleteLagerBestand() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Bestand " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLagerBestand(id)) loadData();
                client.close();
            } catch (Exception e) {
                showError("Serverfehler.");
            }
        }
    }

    /**
     * Sucht ein Produkt-Objekt anhand seiner ID auf dem Server.
     * * @param client Die aktive Backend-Verbindung.
     * @param id Die ID des gesuchten Produkts.
     * @return Das Produkt oder null, falls es nicht existiert.
     * @throws Exception Wenn ein Kommunikationsfehler auftritt.
     */
    private Produkt getProduktById(BackendClient client, int id) throws Exception {
        return client.getAllProdukte().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    /**
     * Sucht ein Lagerplatz-Objekt anhand seiner ID auf dem Server.
     * * @param client Die aktive Backend-Verbindung.
     * @param id Die ID des gesuchten Lagerplatzes.
     * @return Der Lagerplatz oder null, falls er nicht existiert.
     * @throws Exception Wenn ein Kommunikationsfehler auftritt.
     */
    private LagerPlatz getLagerPlatzById(BackendClient client, int id) throws Exception {
        return client.getAllLagerPlaetze().stream().filter(lp -> lp.getId() == id).findFirst().orElse(null);
    }

    /**
     * Hilfsmethode, um Fehlermeldungen standardisiert als Pop-up-Dialog anzuzeigen.
     * * @param msg Die anzuzeigende Fehlermeldung.
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}