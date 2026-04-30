package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

public class LagerVerwaltungPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public LagerVerwaltungPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Ort", "Art"};
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
        btnAdd.addActionListener(e -> addLager());
        btnEdit.addActionListener(e -> editLager());
        btnDelete.addActionListener(e -> deleteLager());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            var client = new BackendClient();
            for (var l : client.getAllLager()) {
                tableModel.addRow(new Object[]{l.getId(), l.getName(), l.getOrt(), l.getArt()});
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void addLager() {
        var txtId = new JTextField();
        var txtName = new JTextField();
        var txtOrt = new JTextField();
        var txtArt = new JTextField();
        Object[] msg = {"ID:", txtId, "Name:", txtName, "Ort:", txtOrt, "Art:", txtArt};

        if (JOptionPane.showConfirmDialog(this, msg, "Neues Lager", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                var l = new Lager(Integer.parseInt(txtId.getText().trim()), txtName.getText().trim(), txtOrt.getText().trim(), txtArt.getText().trim());
                var client = new BackendClient();
                if (client.addLager(l)) loadData();
                else showError("Fehler beim Speichern.");
                client.close();
            } catch (Exception ex) { showError("Ungültige Eingabe oder Serverfehler."); }
        }
    }

    private void editLager() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Bitte ein Lager auswählen."); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        var txtName = new JTextField(tableModel.getValueAt(row, 1).toString());
        var txtOrt = new JTextField(tableModel.getValueAt(row, 2).toString());
        var txtArt = new JTextField(tableModel.getValueAt(row, 3).toString());
        Object[] msg = {"ID: " + id, "Name:", txtName, "Ort:", txtOrt, "Art:", txtArt};

        if (JOptionPane.showConfirmDialog(this, msg, "Lager bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                var l = new Lager(id, txtName.getText().trim(), txtOrt.getText().trim(), txtArt.getText().trim());
                var client = new BackendClient();
                if (client.updateLager(l)) loadData();
                else showError("Fehler beim Aktualisieren.");
                client.close();
            } catch (Exception ex) { showError("Ungültige Eingabe oder Serverfehler."); }
        }
    }

    private void deleteLager() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Bitte ein Lager auswählen."); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Lager " + id + " löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLager(id)) loadData();
                else showError("Fehler beim Löschen.");
                client.close();
            } catch (Exception e) { showError("Serverfehler."); }
        }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE); }
}