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

/**
 * Diese Klasse repräsentiert die Verwaltungsoberfläche für die Stammdaten der Produkte.
 * Sie zeigt eine Tabelle mit allen im System hinterlegten Artikeln an und bietet über
 * Schaltflächen die Möglichkeit, neue Produkte anzulegen, bestehende Eigenschaften (z. B. Preise)
 * zu bearbeiten oder Artikel komplett aus der Datenbank zu entfernen.
 * * @author juan.de.souza.leao
 */
public class ProduktVerwaltungPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;

    /**
     * Standard-Konstruktor.
     * Baut das grundlegende Layout (Tabelle in der Mitte, Buttons unten) auf.
     * Verknüpft zudem die Schaltflächen mit ihren jeweiligen Aktionen und
     * ruft abschließend die Daten vom Server ab, um die Tabelle initial zu füllen.
     */
    public ProduktVerwaltungPanel() {
        setLayout(new BorderLayout());

        // Tabelle konfigurieren
        String[] columnNames = {"ID", "Name", "Hersteller", "Preis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Direkte Bearbeitung in der Zelle deaktivieren, Bearbeitung nur über Dialog
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons konfigurieren (Aktualisieren-Button entfernt)
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnAdd = new JButton("Neu");
        var btnEdit = new JButton("Bearbeiten");
        var btnDelete = new JButton("Löschen");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listener hinzufügen
        btnAdd.addActionListener(e -> addProdukt());
        btnEdit.addActionListener(e -> editProdukt());
        btnDelete.addActionListener(e -> deleteProdukt());

        // Initiale Daten laden
        loadData();
    }

    /**
     * Lädt die aktuelle Produktliste vom Server und aktualisiert die Anzeige in der Tabelle.
     * Leert vorher die Tabelle, damit es keine doppelten Einträge gibt.
     */
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

    /**
     * Öffnet einen Eingabedialog zum Anlegen eines komplett neuen Produkts.
     * Die Produkt-ID wird nicht mehr abgefragt, sondern automatisch vom Server generiert.
     */
    private void addProdukt() {
        var txtName = new JTextField();
        var txtHersteller = new JTextField();
        var txtPreis = new JTextField();

        // Das ID-Feld wurde entfernt
        Object[] message = {"Name:", txtName, "Hersteller:", txtHersteller, "Preis (Zahl):", txtPreis};

        int option = JOptionPane.showConfirmDialog(this, message, "Neues Produkt anlegen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // Wir übergeben '0' als Platzhalter für die ID. Der Server überschreibt dies mit der generierten ID.
                var p = new Produkt(
                        0,
                        txtName.getText().trim(),
                        txtHersteller.getText().trim(),
                        Double.parseDouble(txtPreis.getText().trim().replace(",", "."))
                );

                var client = new BackendClient();
                boolean erfolgreich = client.addProdukt(p);
                client.close();

                if (erfolgreich) {
                    loadData();
                } else {
                    showError("Das Produkt konnte nicht angelegt werden.");
                }
            } catch (NumberFormatException ex) {
                showError("Ungültige Eingabe beim Preis. Bitte nur Zahlen verwenden.");
            } catch (Exception ex) {
                showError("Serverfehler: " + ex.getMessage());
            }
        }
    }

    /**
     * Öffnet einen Eingabedialog zum Bearbeiten eines bestehenden Produkts.
     * Das Produkt muss dafür vorher in der Tabelle per Mausklick markiert worden sein.
     */
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
                var p = new Produkt(id, txtName.getText().trim(), txtHersteller.getText().trim(), Double.parseDouble(txtPreis.getText().trim().replace(",", ".")));
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

    /**
     * Löscht das aktuell in der Tabelle markierte Produkt, nachdem der Nutzer
     * eine kurze Sicherheitsabfrage ("Wirklich löschen?") bestätigt hat.
     */
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

    /**
     * Hilfsmethode, um standardisierte Fehlermeldungen als Pop-up anzuzeigen.
     * @param msg Die Nachricht, die dem Benutzer angezeigt werden soll.
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}