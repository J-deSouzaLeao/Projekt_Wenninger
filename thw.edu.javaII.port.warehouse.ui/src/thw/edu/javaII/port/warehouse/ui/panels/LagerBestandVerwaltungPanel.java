package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

public class LagerBestandVerwaltungPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public LagerBestandVerwaltungPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Anzahl", "Produkt (ID)", "Lagerplatz (ID)"};
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
        btnAdd.addActionListener(e -> addLagerBestand());
        btnEdit.addActionListener(e -> editLagerBestand());
        btnDelete.addActionListener(e -> deleteLagerBestand());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            var client = new BackendClient();
            for (var lb : client.getAllLagerBestaende()) {
                String prodInfo = lb.getProdukt_id() != null ? String.valueOf(lb.getProdukt_id().getId()) : "Keines";
                String platzInfo = lb.getLagerplatz_id() != null ? String.valueOf(lb.getLagerplatz_id().getId()) : "Keiner";
                tableModel.addRow(new Object[]{lb.getId(), lb.getAnzahl(), prodInfo, platzInfo});
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void addLagerBestand() {
        var txtId = new JTextField();
        var txtAnzahl = new JTextField();
        var txtProdId = new JTextField();
        var txtPlatzId = new JTextField();
        Object[] msg = {"ID:", txtId, "Anzahl:", txtAnzahl, "Produkt-ID:", txtProdId, "Lagerplatz-ID:", txtPlatzId};

        if (JOptionPane.showConfirmDialog(this, msg, "Neuer Bestand", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                var client = new BackendClient();
                Produkt p = getProduktById(client, Integer.parseInt(txtProdId.getText().trim()));
                LagerPlatz lp = getLagerPlatzById(client, Integer.parseInt(txtPlatzId.getText().trim()));

                if (p == null || lp == null) { showError("Produkt oder Lagerplatz nicht gefunden."); client.close(); return; }

                var lb = new LagerBestand(Integer.parseInt(txtId.getText().trim()), Integer.parseInt(txtAnzahl.getText().trim()), p, lp);
                if (client.addLagerBestand(lb)) loadData();
                client.close();
            } catch (Exception ex) { showError("Ungültige Eingabe."); }
        }
    }

    private void editLagerBestand() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Bitte einen Bestand auswählen."); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        var txtAnzahl = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtProdId = new JTextField(tableModel.getValueAt(row, 2).toString());
        var txtPlatzId = new JTextField(tableModel.getValueAt(row, 3).toString());
        Object[] msg = {"ID: " + id, "Anzahl:", txtAnzahl, "Produkt-ID:", txtProdId, "Lagerplatz-ID:", txtPlatzId};

        if (JOptionPane.showConfirmDialog(this, msg, "Bestand bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                var client = new BackendClient();
                Produkt p = getProduktById(client, Integer.parseInt(txtProdId.getText().trim()));
                LagerPlatz lp = getLagerPlatzById(client, Integer.parseInt(txtPlatzId.getText().trim()));

                if (p == null || lp == null) { showError("Produkt oder Lagerplatz nicht gefunden."); client.close(); return; }

                var lb = new LagerBestand(id, Integer.parseInt(txtAnzahl.getText().trim()), p, lp);
                if (client.updateLagerBestand(lb)) loadData();
                client.close();
            } catch (Exception ex) { showError("Ungültige Eingabe."); }
        }
    }

    private void deleteLagerBestand() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Bestand " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLagerBestand(id)) loadData();
                client.close();
            } catch (Exception e) { showError("Serverfehler."); }
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