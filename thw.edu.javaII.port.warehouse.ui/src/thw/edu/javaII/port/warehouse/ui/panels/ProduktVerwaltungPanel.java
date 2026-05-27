package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

public class ProduktVerwaltungPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    public ProduktVerwaltungPanel() {
        setLayout(new BorderLayout());

        // Tabelle konfigurieren
        String[] columnNames = {"ID", "Name", "Hersteller", "Preis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Direkte Bearbeitung in der Zelle deaktivieren
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons konfigurieren
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

        // Listener hinzufügen
        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> addProdukt());
        btnEdit.addActionListener(e -> editProdukt());
        btnDelete.addActionListener(e -> deleteProdukt());

        // Initiale Daten laden
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0); // Tabelle leeren
        try {
            var client = new BackendClient();
            var produkte = client.getAllProdukte();
            for (var p : produkte) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getHersteller(), p.getPreis()});
            }
            client.close();
        } catch (Exception e) {
            showError("Fehler beim Laden der Daten: " + e.getMessage());
        }
    }

    private void addProdukt() {
        var txtId = new JTextField();
        var txtName = new JTextField();
        var txtHersteller = new JTextField();
        var txtPreis = new JTextField();
        Object[] message = {"ID (Zahl):", txtId, "Name:", txtName, "Hersteller:", txtHersteller, "Preis (Zahl):", txtPreis};

        int option = JOptionPane.showConfirmDialog(this, message, "Neues Produkt anlegen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                var p = new Produkt(
                        Integer.parseInt(txtId.getText().trim()),
                        txtName.getText().trim(),
                        txtHersteller.getText().trim(),
                        Double.parseDouble(txtPreis.getText().trim())
                );
                var client = new BackendClient();
                if (client.addProdukt(p)) {
                    loadData();
                } else {
                    showError("Fehler beim Speichern auf dem Server.");
                }
                client.close();
            } catch (NumberFormatException ex) {
                showError("Ungültige Eingabe bei ID oder Preis.");
            } catch (Exception ex) {
                showError("Serverfehler: " + ex.getMessage());
            }
        }
    }

    private void editProdukt() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Bitte wählen Sie zuerst ein Produkt aus der Tabelle aus.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        var txtName = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        var txtHersteller = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        var txtPreis = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        Object[] message = {"ID: " + id + " (nicht änderbar)", "Name:", txtName, "Hersteller:", txtHersteller, "Preis:", txtPreis};

        int option = JOptionPane.showConfirmDialog(this, message, "Produkt bearbeiten", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                var p = new Produkt(id, txtName.getText().trim(), txtHersteller.getText().trim(), Double.parseDouble(txtPreis.getText().trim()));
                var client = new BackendClient();
                if (client.updateProdukt(p)) {
                    loadData();
                } else {
                    showError("Fehler beim Aktualisieren auf dem Server.");
                }
                client.close();
            } catch (NumberFormatException ex) {
                showError("Ungültiger Preis.");
            } catch (Exception ex) {
                showError("Serverfehler: " + ex.getMessage());
            }
        }
    }

    private void deleteProdukt() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showError("Bitte wählen Sie zuerst ein Produkt aus der Tabelle aus.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Produkt mit ID " + id + " wirklich löschen?", "Löschen bestätigen", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteProdukt(id)) {
                    loadData();
                } else {
                    showError("Fehler beim Löschen auf dem Server.");
                }
                client.close();
            } catch (Exception e) {
                showError("Serverfehler: " + e.getMessage());
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}