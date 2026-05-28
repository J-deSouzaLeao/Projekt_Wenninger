package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog zum Zurücksetzen einer vergessenen PIN.
 * Ein Manager muss sich mit seiner Nummer und PIN autorisieren,
 * bevor einem bestehenden Kassierer ein neues Passwort zugewiesen werden kann.
 */
public class KassenPinResetDialog extends JDialog {

    private final BackendClient client;

    /**
     * Erstellt den Dialog für den PIN-Reset.
     * @param parent Das aufrufende Hauptfenster (Login-Screen).
     * @param client Die Netzwerkverbindung zum Server.
     */
    public KassenPinResetDialog(JFrame parent, BackendClient client) {
        super(parent, "PIN vergessen - Manager Autorisierung", true);
        this.client = client;

        setSize(450, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Eingabefelder
        inputPanel.add(new JLabel("Kassierer-Nr (der die PIN vergessen hat):"));
        JTextField txtZielKassiererNr = new JTextField();
        inputPanel.add(txtZielKassiererNr);

        inputPanel.add(new JLabel("Neue PIN für diesen Kassierer:"));
        JPasswordField txtNeuePin = new JPasswordField();
        inputPanel.add(txtNeuePin);

        inputPanel.add(new JLabel("Manager-Nr (zur Freigabe):"));
        JTextField txtManagerNr = new JTextField();
        inputPanel.add(txtManagerNr);

        inputPanel.add(new JLabel("Manager-PIN:"));
        JPasswordField txtManagerPin = new JPasswordField();
        inputPanel.add(txtManagerPin);

        add(inputPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAbbrechen = new JButton("Abbrechen");
        btnAbbrechen.addActionListener(e -> dispose());

        JButton btnSpeichern = new JButton("Autorisieren & PIN ändern");
        btnSpeichern.setBackground(new Color(60, 179, 113));
        btnSpeichern.setForeground(Color.WHITE);
        btnSpeichern.addActionListener(e -> resetPin(txtZielKassiererNr, txtNeuePin, txtManagerNr, txtManagerPin));

        buttonPanel.add(btnAbbrechen);
        buttonPanel.add(btnSpeichern);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Prüft die Manager-Autorisierung und überschreibt anschließend die PIN des Ziel-Kassierers.
     */
    private void resetPin(JTextField txtZielKassiererNr, JPasswordField txtNeuePin, JTextField txtManagerNr, JPasswordField txtManagerPin) {
        try {
            int zielNr = Integer.parseInt(txtZielKassiererNr.getText().trim());
            int managerNr = Integer.parseInt(txtManagerNr.getText().trim());
            String managerPin = new String(txtManagerPin.getPassword());
            String neuePin = new String(txtNeuePin.getPassword());

            if (neuePin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Die neue PIN darf nicht leer sein.", "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Prüfen, ob der Manager echte Manager-Rechte hat
            Kassierer authUser = client.loginKassierer(managerNr, managerPin, 0.0);
            if (authUser == null || !authUser.isManager()) {
                JOptionPane.showMessageDialog(this, "Autorisierung fehlgeschlagen! Ungültige Manager-Daten.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Den Ziel-Kassierer aus der Datenbank suchen
            List<Kassierer> alleKassierer = client.getAllKassierer();
            Kassierer zielKassierer = alleKassierer.stream().filter(k -> k.getNummer() == zielNr).findFirst().orElse(null);

            if (zielKassierer == null) {
                JOptionPane.showMessageDialog(this, "Der angegebene Kassierer existiert nicht.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Neue PIN setzen und an den Server senden
            zielKassierer.setPin(neuePin);
            if (client.updateKassierer(zielKassierer)) {
                JOptionPane.showMessageDialog(this, "PIN erfolgreich geändert! Der Kassierer kann sich nun einloggen.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Fehler beim Speichern der neuen PIN auf dem Server.", "Serverfehler", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte gültige Nummern eingeben.", "Eingabefehler", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Netzwerkfehler: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}