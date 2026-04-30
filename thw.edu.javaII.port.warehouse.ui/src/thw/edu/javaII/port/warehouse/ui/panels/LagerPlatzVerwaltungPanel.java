package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

public class LagerPlatzVerwaltungPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public LagerPlatzVerwaltungPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Kapazität", "Lager (ID)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
                var client = new BackendClient();
                Lager lager = getLagerById(client, Integer.parseInt(txtLagerId.getText().trim()));
                if (lager == null) { showError("Lager-ID nicht gefunden."); client.close(); return; }

                var lp = new LagerPlatz(Integer.parseInt(txtId.getText().trim()), txtName.getText().trim(), Integer.parseInt(txtKapa.getText().trim()), lager);
                if (client.addLagerPlatz(lp)) loadData();
                client.close();
            } catch (Exception ex) { showError("Ungültige Eingabe."); }
        }
    }

    private void editLagerPlatz() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Bitte einen Lagerplatz auswählen."); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        var txtName = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtKapa = new JTextField(tableModel.getValueAt(row, 2).toString());
        var txtLagerId = new JTextField(tableModel.getValueAt(row, 3).toString());
        Object[] msg = {"ID: " + id, "Name:", txtName, "Kapazität:", txtKapa, "Lager-ID:", txtLagerId};

        if (JOptionPane.showConfirmDialog(this, msg, "Lagerplatz bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                var client = new BackendClient();
                Lager lager = getLagerById(client, Integer.parseInt(txtLagerId.getText().trim()));
                if (lager == null) { showError("Lager-ID nicht gefunden."); client.close(); return; }

                var lp = new LagerPlatz(id, txtName.getText().trim(), Integer.parseInt(txtKapa.getText().trim()), lager);
                if (client.updateLagerPlatz(lp)) loadData();
                client.close();
            } catch (Exception ex) { showError("Ungültige Eingabe."); }
        }
    }

    private void deleteLagerPlatz() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Lagerplatz " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLagerPlatz(id)) loadData();
                client.close();
            } catch (Exception e) { showError("Serverfehler."); }
        }
    }

    private Lager getLagerById(BackendClient client, int id) throws Exception {
        return client.getAllLager().stream().filter(l -> l.getId() == id).findFirst().orElse(null);
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE); }
}