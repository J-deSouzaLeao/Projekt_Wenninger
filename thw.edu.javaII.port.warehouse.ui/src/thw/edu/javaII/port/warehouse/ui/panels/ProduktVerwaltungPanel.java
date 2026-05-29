package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JDialog;

import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.ui.BackendClient;
import thw.edu.javaII.port.warehouse.ui.common.Session;

/**
 * Diese Klasse repräsentiert die Verwaltungsoberfläche für die Stammdaten der Produkte.
 * Beinhaltet nun eine Echtzeit-Suchfunktion (Filter) über alle Spalten.
 */
public class ProduktVerwaltungPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final Session ses;

    // NEU: Variablen für die Suche
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtSearch;

    public ProduktVerwaltungPanel(Session ses) {
        this.ses = ses;
        setLayout(new BorderLayout());

        // --- 1. Suchleiste (Kopfbereich) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel("Suchen:"));
        txtSearch = new JTextField(20);
        topPanel.add(txtSearch);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Tabelle und Datenmodell ---
        String[] columnNames = {"Bestands-ID", "Produkt-ID", "Name", "Hersteller", "Preis", "Bestand", "Lagerplatz", "Lager"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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

        btnAdd.addActionListener(e -> addProdukt());
        btnEdit.addActionListener(e -> editProdukt());
        btnDelete.addActionListener(e -> deleteProdukt());

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

    private void loadData() {
        tableModel.setRowCount(0); // Tabelle leeren
        try {
            var bestande = ses.getCommunicator().getBestand();
            for (LagerBestand b : bestande) {
                tableModel.addRow(new Object[]{
                        String.format("BST-%05d", b.getId()),
                        b.getProdukt_id().getId(),
                        b.getProdukt_id().getName(),
                        b.getProdukt_id().getHersteller(),
                        b.getProdukt_id().getPreis(),
                        b.getAnzahl(),
                        b.getLagerplatz_id().getName(),
                        b.getLagerplatz_id().getLager_id().getName()
                });
            }
        } catch (Exception e) {
            showError("Fehler beim Laden der Daten: " + e.getMessage());
        }
    }

    private void addProdukt() {
        AddProdukt ap = new AddProdukt(ses);
        ap.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ap.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        ap.setVisible(true);
        loadData();
    }

    private void editProdukt() {
        int selectedRowView = table.getSelectedRow();
        if (selectedRowView == -1) {
            showError("Bitte wählen Sie zuerst ein Produkt aus der Tabelle aus.");
            return;
        }

        // WICHTIG: Ansicht auf das Modell umrechnen, falls gefiltert wurde!
        int selectedRow = table.convertRowIndexToModel(selectedRowView);

        int produktId = (int) tableModel.getValueAt(selectedRow, 1);
        var txtName = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        var txtHersteller = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        var txtPreis = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

        Object[] message = {"Produkt-ID: " + produktId + " (nicht änderbar)", "Name:", txtName, "Hersteller:", txtHersteller, "Preis:", txtPreis};

        int option = JOptionPane.showConfirmDialog(this, message, "Produkt bearbeiten", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                var p = new Produkt(produktId, txtName.getText().trim(), txtHersteller.getText().trim(), Double.parseDouble(txtPreis.getText().trim().replace(",", ".")));
                var client = new BackendClient();
                if (client.updateProdukt(p)) {
                    loadData();
                } else {
                    showError("Fehler beim Aktualisieren auf dem Server.");
                }
                client.close();
            } catch (NumberFormatException ex) {
                showError("Ungültiger Preis. Bitte nur Zahlen verwenden.");
            } catch (Exception ex) {
                showError("Serverfehler: " + ex.getMessage());
            }
        }
    }

    private void deleteProdukt() {
        int selectedRowView = table.getSelectedRow();
        if (selectedRowView == -1) {
            showError("Bitte wählen Sie zuerst ein Produkt aus der Tabelle aus.");
            return;
        }

        // WICHTIG: Ansicht auf das Modell umrechnen!
        int selectedRow = table.convertRowIndexToModel(selectedRowView);

        int produktId = (int) tableModel.getValueAt(selectedRow, 1);
        String produktName = tableModel.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Produkt '" + produktName + "' (ID: " + produktId + ") wirklich komplett löschen?\nAlle Lagerbestände dieses Produkts werden ebenfalls gelöscht!",
                "Löschen bestätigen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                var client = new BackendClient();
                if (client.deleteProdukt(produktId)) {
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