package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Der Administrationsdialog für Manager an der Kasse.
 * Bietet eine tabellarische Übersicht aller Kassierer sowie Funktionen
 * zum Anlegen, Bearbeiten und Löschen von Personal.
 * Enthält einen Selbstschutz, damit sich Administratoren nicht selbst löschen können.
 */
public class AdminKassiererDialog extends JDialog {
    private final BackendClient client;
    private final Kassierer aktiverManager; // Speichert den eingeloggten Benutzer
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Kassierer> aktuelleKassiererListe;

    /**
     * Erstellt den Dialog und baut die Verwaltungsoberfläche auf.
     * @param parent Das aufrufende Kassen-Hauptfenster.
     * @param client Die aktive Backend-Verbindung.
     * @param aktiverManager Der aktuell eingeloggte Manager, der diesen Dialog bedient.
     */
    public AdminKassiererDialog(JFrame parent, BackendClient client, Kassierer aktiverManager) {
        super(parent, "Kassiererverwaltung (Admin-Bereich)", true);
        this.client = client;
        this.aktiverManager = aktiverManager;

        setSize(650, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Tabelle konfigurieren
        String[] columns = {"Nummer", "Name", "Ist Manager"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons konfigurieren
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnNeu = new JButton("Neu");
        btnNeu.addActionListener(e -> anlegen());

        JButton btnBearbeiten = new JButton("Bearbeiten");
        btnBearbeiten.addActionListener(e -> bearbeiten());

        JButton btnLoeschen = new JButton("Löschen");
        btnLoeschen.addActionListener(e -> loeschen());

        buttonPanel.add(btnNeu);
        buttonPanel.add(btnBearbeiten);
        buttonPanel.add(btnLoeschen);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    /**
     * Lädt die Kassierer-Liste vom Server und aktualisiert die Tabelle.
     */
    private void loadData() {
        tableModel.setRowCount(0);
        try {
            aktuelleKassiererListe = client.getAllKassierer();
            for (Kassierer k : aktuelleKassiererListe) {
                tableModel.addRow(new Object[]{ k.getNummer(), k.getName(), k.isManager() ? "Ja" : "Nein" });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet einen Eingabedialog zum Erstellen eines neuen Kassierers.
     * Prüft die Server-Antwort und gibt bei Duplikaten der Personalnummer eine Warnung aus.
     */
    private void anlegen() {
        JTextField txtNr = new JTextField();
        JTextField txtName = new JTextField();
        JPasswordField txtPin = new JPasswordField();
        JCheckBox chkManager = new JCheckBox("Ist Manager?");

        Object[] msg = {"Nummer:", txtNr, "Name:", txtName, "PIN:", txtPin, chkManager};

        if (JOptionPane.showConfirmDialog(this, msg, "Neuer Kassierer", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Kassierer k = new Kassierer();
                k.setNummer(Integer.parseInt(txtNr.getText().trim()));
                k.setName(txtName.getText().trim());
                k.setPin(new String(txtPin.getPassword()));
                k.setManager(chkManager.isSelected());

                // --- BUGFIX: Abfrage mit else-Block für die Fehlermeldung ---
                boolean erfolgreich = client.addKassierer(k);

                if (erfolgreich) {
                    loadData(); // Alles hat geklappt, Tabelle aktualisieren
                } else {
                    // Server meldet Fehler (z.B. ID schon vergeben)
                    JOptionPane.showMessageDialog(this,
                            "Der Mitarbeiter konnte nicht angelegt werden.\nMöglicherweise ist die Personalnummer bereits vergeben.",
                            "Eingabefehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Anlegen: Bitte gültige Daten (Zahlen für Nummer) eingeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Öffnet einen Eingabedialog zum Bearbeiten des ausgewählten Kassierers.
     */
    private void bearbeiten() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bitte einen Kassierer zum Bearbeiten auswählen.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int nummer = (int) tableModel.getValueAt(row, 0);
        Kassierer ziel = aktuelleKassiererListe.stream().filter(k -> k.getNummer() == nummer).findFirst().orElse(null);

        if (ziel == null) return;

        JTextField txtName = new JTextField(ziel.getName());
        JPasswordField txtPin = new JPasswordField(ziel.getPin());
        JCheckBox chkManager = new JCheckBox("Ist Manager?", ziel.isManager());

        Object[] msg = {
                "Nummer: " + ziel.getNummer() + " (nicht änderbar)",
                "Name:", txtName,
                "PIN:", txtPin,
                chkManager
        };

        if (JOptionPane.showConfirmDialog(this, msg, "Kassierer bearbeiten", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                ziel.setName(txtName.getText().trim());
                ziel.setPin(new String(txtPin.getPassword()));
                ziel.setManager(chkManager.isSelected());

                if (client.updateKassierer(ziel)) {
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Aktualisieren.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Löscht den in der Tabelle ausgewählten Kassierer nach einer Bestätigung.
     * Verhindert, dass der aktuell eingeloggte Manager sich selbst löscht.
     */
    private void loeschen() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bitte einen Kassierer auswählen.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int nummer = (int) tableModel.getValueAt(row, 0);

        // Schutzfunktion vor Selbstlöschung
        if (nummer == aktiverManager.getNummer()) {
            JOptionPane.showMessageDialog(this, "Aktion verweigert: Du kannst deinen eigenen Account nicht löschen!", "Sicherheitswarnung", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Kassierer " + nummer + " wirklich löschen?") == JOptionPane.YES_OPTION) {
            try {
                if (client.deleteKassierer(nummer)) {
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Löschen.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}