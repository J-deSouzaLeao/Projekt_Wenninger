package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.ui.BackendClient;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.JTextField;
import javax.swing.JLabel;

/**
 * Diese Klasse repräsentiert die Verwaltungsoberfläche für die Hauptlager (Standorte).
 * Sie zeigt eine tabellarische Übersicht aller im System registrierten Lager an und bietet
 * eine Echtzeit-Suchfunktion sowie die Möglichkeit, Standorte zu verwalten.
 * * @author juan.de.souza.leao
 */
public class LagerVerwaltungPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;

    // NEU: Variablen für die Suche
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtSearch;

    /**
     * Standard-Konstruktor.
     * Baut das grundlegende Layout auf (Suchleiste oben, Tabelle Mitte, Buttons unten).
     */
    public LagerVerwaltungPanel() {
        setLayout(new BorderLayout());

        // --- 1. Suchleiste (Kopfbereich) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel("Suchen:"));
        txtSearch = new JTextField(20);
        topPanel.add(txtSearch);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Tabelle und Datenmodell ---
        String[] columnNames = {"ID", "Name", "Ort", "Art"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Direkte Bearbeitung in der Zelle deaktivieren
            }
        };
        table = new JTable(tableModel);

        // --- 3. Sorter an die Tabelle binden ---
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 4. Live-Filter Logik an das Textfeld hängen ---
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // --- Buttons (Fußbereich) ---
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnAdd = new JButton("Neu");
        var btnEdit = new JButton("Bearbeiten");
        var btnDelete = new JButton("Löschen");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addLager());
        btnEdit.addActionListener(e -> editLager());
        btnDelete.addActionListener(e -> deleteLager());

        loadData();
    }

    /**
     * Wendet den Text aus dem Suchfeld als Filter auf die Tabelle an.
     * Ignoriert Groß-/Kleinschreibung durch den Regex-Präfix "(?i)".
     */
    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    /**
     * Lädt die Liste aller verfügbaren Lager vom Server herunter und aktualisiert
     * die Anzeige in der Tabelle.
     */
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

    /**
     * Öffnet einen Eingabedialog zum Anlegen eines komplett neuen Lagers.
     */
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
            } catch (Exception ex) {
                showError("Ungültige Eingabe oder Serverfehler.");
            }
        }
    }

    /**
     * Öffnet einen Eingabedialog zum Bearbeiten eines bestehenden Lagers.
     */
    private void editLager() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            showError("Bitte ein Lager auswählen.");
            return;
        }

        // WICHTIG: Ansicht auf das Modell umrechnen, falls gefiltert wurde!
        int row = table.convertRowIndexToModel(viewRow);

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
            } catch (Exception ex) {
                showError("Ungültige Eingabe oder Serverfehler.");
            }
        }
    }

    /**
     * Entfernt das aktuell in der Tabelle markierte Lager aus dem System.
     */
    private void deleteLager() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            showError("Bitte ein Lager auswählen.");
            return;
        }

        // WICHTIG: Ansicht auf das Modell umrechnen, falls gefiltert wurde!
        int row = table.convertRowIndexToModel(viewRow);

        int id = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1).toString();

        if (JOptionPane.showConfirmDialog(this, "Lager '" + name + "' (ID: " + id + ") wirklich löschen?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteLager(id)) loadData();
                else showError("Fehler beim Löschen.");
                client.close();
            } catch (Exception e) {
                showError("Serverfehler.");
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}