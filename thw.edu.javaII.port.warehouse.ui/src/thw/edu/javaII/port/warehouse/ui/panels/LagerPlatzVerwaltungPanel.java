package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

/**
 * Diese Klasse stellt die grafische Verwaltungsoberfläche für die einzelnen Lagerplätze dar.
 * Sie ermöglicht es, innerhalb eines bestehenden Hauptlagers neue Plätze (z. B. Regale oder Fächer)
 * anzulegen, ihre Kapazität anzupassen oder sie komplett aus dem System zu löschen.
 * * @author juan.de.souza.leao
 */
public class LagerPlatzVerwaltungPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    /**
     * Standard-Konstruktor.
     * Baut das grundlegende Layout (Tabelle in der Mitte, Buttons für Aktionen unten) auf.
     * Verknüpft die Schaltflächen mit den jeweiligen Speicher- und Löschvorgängen und
     * ruft die initialen Daten vom Server ab.
     */
    public LagerPlatzVerwaltungPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Kapazität", "Lager (ID)"};
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
        btnAdd.addActionListener(e -> addLagerPlatz());
        btnEdit.addActionListener(e -> editLagerPlatz());
        btnDelete.addActionListener(e -> deleteLagerPlatz());

        loadData();
    }

    /**
     * Lädt die aktuelle Liste aller Lagerplätze vom Server herunter und füllt
     * damit die Anzeigetabelle. Vorhandene Daten werden vorher geleert.
     */
    private void loadData() {
        tableModel.setRowCount(0);
        try {
            var client = new BackendClient();
            for (var lp : client.getAllLagerPlaetze()) {
                // Holt sich die ID des zugehörigen Hauptlagers, falls eines verknüpft ist
                String lagerInfo = lp.getLager_id() != null ? String.valueOf(lp.getLager_id().getId()) : "Keines";
                tableModel.addRow(new Object[]{lp.getId(), lp.getName(), lp.getKapazitaet(), lagerInfo});
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden: " + e.getMessage());
        }
    }

    /**
     * Öffnet einen Dialog, um einen neuen Lagerplatz im System anzulegen.
     * Die Eingaben werden an die Methode saveLagerPlatz übergeben, die die
     * Kommunikation mit dem Server übernimmt.
     */
    private void addLagerPlatz() {
        var txtId = new JTextField();
        var txtName = new JTextField();
        var txtKapa = new JTextField();
        var txtLagerId = new JTextField();
        Object[] msg = {"ID:", txtId, "Name:", txtName, "Kapazität:", txtKapa, "Lager-ID:", txtLagerId};

        if (JOptionPane.showConfirmDialog(this, msg, "Neuer Lagerplatz", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                saveLagerPlatz(id, txtName.getText(), txtKapa.getText(), txtLagerId.getText(), false);
            } catch (NumberFormatException ex) {
                showError("Ungültige Eingabe für ID.");
            }
        }
    }

    /**
     * Öffnet einen Dialog, um die Daten eines ausgewählten Lagerplatzes zu bearbeiten.
     * Liest die aktuellen Werte aus der markierten Tabellenzeile aus und füllt sie
     * in die Eingabefelder vor.
     */
    private void editLagerPlatz() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Bitte einen Lagerplatz auswählen.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        var txtName = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtKapa = new JTextField(tableModel.getValueAt(row, 2).toString());
        var txtLagerId = new JTextField(tableModel.getValueAt(row, 3).toString());
        Object[] msg = {"ID: " + id, "Name:", txtName, "Kapazität:", txtKapa, "Lager-ID:", txtLagerId};

        if (JOptionPane.showConfirmDialog(this, msg, "Lagerplatz bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            saveLagerPlatz(id, txtName.getText(), txtKapa.getText(), txtLagerId.getText(), true);
        }
    }

    /**
     * Eine zentrale Hilfsmethode, die den Speichervorgang für neue und bearbeitete Lagerplätze bündelt.
     * Prüft insbesondere, ob das im Textfeld angegebene Hauptlager (Lager-ID) existiert,
     * bevor der Datensatz an den Server geschickt wird.
     * * @param id Die ID des Lagerplatzes.
     * @param name Der Name (z. B. "Regal A").
     * @param kapaText Die Kapazität als Text.
     * @param lagerIdText Die ID des übergeordneten Lagers als Text.
     * @param isUpdate True, wenn es sich um eine Bearbeitung handelt, False bei Neuanlage.
     */
    private void saveLagerPlatz(int id, String name, String kapaText, String lagerIdText, boolean isUpdate) {
        try {
            var client = new BackendClient();
            Lager lager = getLagerById(client, Integer.parseInt(lagerIdText.trim()));
            if (lager == null) {
                showError("Lager-ID nicht gefunden.");
                client.close();
                return;
            }

            var lp = new LagerPlatz(id, name.trim(), Integer.parseInt(kapaText.trim()), lager);
            boolean success = isUpdate ? client.updateLagerPlatz(lp) : client.addLagerPlatz(lp);
            if (success) {
                loadData();
            }
            client.close();
        } catch (Exception ex) {
            showError("Ungültige Eingabe.");
        }
    }

    /**
     * Löscht den markierten Lagerplatz nach einer kurzen Bestätigungsfrage.
     * Aktualisiert anschließend automatisch die Tabellenansicht.
     */
    private void deleteLagerPlatz() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Lagerplatz " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLagerPlatz(id)) {
                    loadData();
                }
                client.close();
            } catch (Exception e) {
                showError("Serverfehler.");
            }
        }
    }

    /**
     * Hilfsmethode, um anhand einer ID das passende Lager-Objekt vom Server zu suchen.
     * Wird genutzt, um sicherzustellen, dass ein Lagerplatz nicht einem nicht-existenten Lager zugewiesen wird.
     * * @param client Die aktive Netzwerkverbindung zum Server.
     * @param id Die gesuchte Lager-ID.
     * @return Das gefundene Lager-Objekt oder null, wenn kein Treffer vorliegt.
     * @throws Exception Falls bei der Kommunikation etwas schiefläuft.
     */
    private Lager getLagerById(BackendClient client, int id) throws Exception {
        return client.getAllLager().stream().filter(l -> l.getId() == id).findFirst().orElse(null);
    }

    /**
     * Hilfsmethode, um Fehlermeldungen in einem Dialogfenster anzuzeigen.
     * @param msg Die Nachricht.
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}