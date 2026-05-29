package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

/**
 * Verwaltungsoberfläche für Lagerbestände.
 * Inklusive Echtzeit-Suchfilter und ID-Formatierung.
 *
 * @author juan.de.souza.leao
 */
public class LagerBestandVerwaltungPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Such-Komponenten
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtSearch;

    public LagerBestandVerwaltungPanel() {
        setLayout(new BorderLayout());

        // --- Kopfbereich mit Suche ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel("Suchen:"));
        txtSearch = new JTextField(20);
        topPanel.add(txtSearch);
        add(topPanel, BorderLayout.NORTH);

        // --- Tabelle ---
        String[] columnNames = {"Bestands-ID", "Anzahl", "Lagerplatz-ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        // Sorter aktivieren
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Filter-Logik
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // --- Buttons ---
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnAdd = new JButton("Neu");
        var btnEdit = new JButton("Bearbeiten");
        var btnDelete = new JButton("Löschen");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addLagerBestand());
        btnEdit.addActionListener(e -> editLagerBestand());
        btnDelete.addActionListener(e -> deleteLagerBestand());

        loadData();
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            var client = new BackendClient();
            for (var lb : client.getAllLagerBestaende()) {
                // Hier erfolgt die Formatierung auf BST-XXXXX
                tableModel.addRow(new Object[]{
                        String.format("BST-%05d", lb.getId()),
                        lb.getAnzahl(),
                        lb.getLagerplatz_id() != null ? lb.getLagerplatz_id().getId() : "Keiner"
                });
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void addLagerBestand() {
        var txtAnzahl = new JTextField();
        var txtProdId = new JTextField();
        var txtPlatzId = new JTextField();
        Object[] msg = {"Anzahl:", txtAnzahl, "Produkt-ID:", txtProdId, "Lagerplatz-ID:", txtPlatzId};

        if (JOptionPane.showConfirmDialog(this, msg, "Neuer Bestand", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                // ID 0, da der Server die Generierung übernimmt
                saveLagerBestand(0, txtAnzahl.getText(), txtProdId.getText(), txtPlatzId.getText(), false);
            } catch (Exception ex) { showError("Ungültige Eingabe."); }
        }
    }

    private void editLagerBestand() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) { showError("Bitte einen Bestand auswählen."); return; }

        int row = table.convertRowIndexToModel(viewRow);
        // ID aus formatiertem String extrahieren (BST-00001 -> 1)
        String idString = tableModel.getValueAt(row, 0).toString().replace("BST-", "");
        int id = Integer.parseInt(idString);

        var txtAnzahl = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtPlatzId = new JTextField(tableModel.getValueAt(row, 2).toString());
        var txtProdId = new JTextField(); // Hier müsste man ggf. die ProduktID aus dem Objekt laden

        Object[] msg = {"Bestands-ID: " + id, "Anzahl:", txtAnzahl, "Lagerplatz-ID:", txtPlatzId};
        if (JOptionPane.showConfirmDialog(this, msg, "Bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            saveLagerBestand(id, txtAnzahl.getText(), txtProdId.getText(), txtPlatzId.getText(), true);
        }
    }

    private void saveLagerBestand(int id, String anzahl, String prodId, String platzId, boolean isUpdate) {
        try {
            var client = new BackendClient();
            var lb = new LagerBestand(id, Integer.parseInt(anzahl), getProduktById(client, Integer.parseInt(prodId)), getLagerPlatzById(client, Integer.parseInt(platzId)));
            if (isUpdate ? client.updateLagerBestand(lb) : client.addLagerBestand(lb)) loadData();
            client.close();
        } catch (Exception ex) { showError("Speichern fehlgeschlagen: " + ex.getMessage()); }
    }

    private void deleteLagerBestand() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;

        int row = table.convertRowIndexToModel(viewRow);
        String idString = tableModel.getValueAt(row, 0).toString().replace("BST-", "");
        int id = Integer.parseInt(idString);

        if (JOptionPane.showConfirmDialog(this, "Bestand " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLagerBestand(id)) loadData();
                client.close();
            } catch (Exception e) { showError("Fehler."); }
        }
    }

    private Produkt getProduktById(BackendClient client, int id) throws Exception {
        return client.getAllProdukte().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    private LagerPlatz getLagerPlatzById(BackendClient client, int id) throws Exception {
        return client.getAllLagerPlaetze().stream().filter(lp -> lp.getId() == id).findFirst().orElse(null);
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE); }
}