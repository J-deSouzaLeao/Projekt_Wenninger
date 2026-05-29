package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

/**
 * Verwaltungsoberfläche für Lagerplätze.
 * Inklusive Echtzeit-Suchfilter und sicherer Zeilennummerierung.
 *
 * @author barbara.liegnitz
 */
public class LagerPlatzVerwaltungPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Such-Komponenten
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtSearch;

    public LagerPlatzVerwaltungPanel() {
        setLayout(new BorderLayout());

        // --- Kopfbereich mit Suche ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel("Suchen:"));
        txtSearch = new JTextField(20);
        topPanel.add(txtSearch);
        add(topPanel, BorderLayout.NORTH);

        // --- Tabelle ---
        String[] columnNames = {"Lagerplatz-ID", "Name", "Kapazität", "Lager-ID"};
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

        btnAdd.addActionListener(e -> addLagerPlatz());
        btnEdit.addActionListener(e -> editLagerPlatz());
        btnDelete.addActionListener(e -> deleteLagerPlatz());

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
            for (var lp : client.getAllLagerPlaetze()) {
                String lagerInfo = lp.getLager_id() != null ? String.valueOf(lp.getLager_id().getId()) : "Keines";
                tableModel.addRow(new Object[]{lp.getId(), lp.getName(), lp.getKapazitaet(), lagerInfo});
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void addLagerPlatz() {
        var txtId = new JTextField();
        var txtName = new JTextField();
        var txtKapa = new JTextField();
        var txtLagerId = new JTextField();
        Object[] msg = {"ID:", txtId, "Name:", txtName, "Kapazität:", txtKapa, "Lager-ID:", txtLagerId};

        if (JOptionPane.showConfirmDialog(this, msg, "Neuer Lagerplatz", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                saveLagerPlatz(Integer.parseInt(txtId.getText().trim()), txtName.getText(), txtKapa.getText(), txtLagerId.getText(), false);
            } catch (NumberFormatException ex) { showError("Ungültige ID-Eingabe."); }
        }
    }

    private void editLagerPlatz() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) { showError("Bitte einen Lagerplatz auswählen."); return; }

        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(row, 0);
        var txtName = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtKapa = new JTextField(tableModel.getValueAt(row, 2).toString());
        var txtLagerId = new JTextField(tableModel.getValueAt(row, 3).toString());

        Object[] msg = {"ID: " + id, "Name:", txtName, "Kapazität:", txtKapa, "Lager-ID:", txtLagerId};
        if (JOptionPane.showConfirmDialog(this, msg, "Bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            saveLagerPlatz(id, txtName.getText(), txtKapa.getText(), txtLagerId.getText(), true);
        }
    }

    private void saveLagerPlatz(int id, String name, String kapa, String lagerId, boolean isUpdate) {
        try {
            var client = new BackendClient();
            Lager lager = client.getAllLager().stream().filter(l -> l.getId() == Integer.parseInt(lagerId.trim())).findFirst().orElse(null);
            if (lager == null) { showError("Lager-ID nicht gefunden."); client.close(); return; }

            var lp = new LagerPlatz(id, name.trim(), Integer.parseInt(kapa.trim()), lager);
            if (isUpdate ? client.updateLagerPlatz(lp) : client.addLagerPlatz(lp)) loadData();
            client.close();
        } catch (Exception ex) { showError("Ungültige Eingabe."); }
    }

    private void deleteLagerPlatz() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;

        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Lagerplatz " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLagerPlatz(id)) loadData();
                client.close();
            } catch (Exception e) { showError("Serverfehler."); }
        }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE); }
}