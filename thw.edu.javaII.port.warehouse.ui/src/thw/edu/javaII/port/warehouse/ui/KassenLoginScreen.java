package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Diese Klasse repräsentiert den Login-Bildschirm für das Kassen-Terminal (Point of Sale).
 * Sie ist speziell für die Touchscreen-Bedienung optimiert und enthält ein eigenes Numpad
 * (Ziffernblock) zur Eingabe der Kassierer-Nummer, der PIN und des anfänglichen Kassenbestands.
 */
public class KassenLoginScreen extends JFrame {
    private final JTextField nrField;
    private final JPasswordField pinField;
    private final JTextField bestandField;
    private final BackendClient client;

    // NEU: Speichert, welches Feld gerade vom Benutzer ausgewählt ist
    private JTextField aktivesFeld;

    /**
     * Erstellt das Login-Fenster und initialisiert die Benutzeroberfläche.
     * Baut die Eingabefelder auf und richtet einen Focus-Listener ein, der stets
     * überwacht, welches Textfeld gerade aktiv (angetippt) ist, damit das Numpad
     * die Zahlen in das richtige Feld schreibt.
     * * @param client Der BackendClient für die Serverkommunikation (Authentifizierung).
     */
    public KassenLoginScreen(BackendClient client) {
        this.client = client;
        setTitle("Kassen-Terminal Login");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Eingabefelder
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("Kassierer-Nr:"));
        nrField = new JTextField();
        nrField.setFont(new Font("Arial", Font.BOLD, 24));
        inputPanel.add(nrField);

        inputPanel.add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        pinField.setFont(new Font("Arial", Font.BOLD, 24));
        inputPanel.add(pinField);

        inputPanel.add(new JLabel("Startbestand (€):"));
        bestandField = new JTextField("0.00");
        bestandField.setFont(new Font("Arial", Font.BOLD, 24));
        inputPanel.add(bestandField);

        add(inputPanel, BorderLayout.NORTH);

        // NEU: Wir merken uns immer, welches Feld zuletzt angetippt wurde
        aktivesFeld = nrField; // Standard beim Start: Das oberste Feld
        FocusAdapter focusTracker = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                aktivesFeld = (JTextField) e.getComponent();
            }
        };
        nrField.addFocusListener(focusTracker);
        pinField.addFocusListener(focusTracker);
        bestandField.addFocusListener(focusTracker);

        // Touch Numpad (vereinfacht für Login)
        JPanel numpad = new JPanel(new GridLayout(4, 3, 5, 5));
        for (int i = 1; i <= 9; i++) {
            numpad.add(createNumButton(String.valueOf(i)));
        }
        numpad.add(createNumButton("C"));
        numpad.add(createNumButton("0"));

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(60, 179, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 20));
        loginBtn.setFocusable(false); // Verhindert Fokus-Klau beim Login-Button
        loginBtn.addActionListener(e -> performLogin());
        numpad.add(loginBtn);

        // --- NEU: PIN vergessen Button ---
        JButton pinResetBtn = new JButton("PIN vergessen?");
        pinResetBtn.setBackground(new Color(255, 140, 0));
        pinResetBtn.setForeground(Color.WHITE);
        pinResetBtn.setFont(new Font("Arial", Font.BOLD, 16));
        pinResetBtn.setFocusable(false);
        pinResetBtn.addActionListener(e -> new KassenPinResetDialog(this, client).setVisible(true));
        numpad.add(pinResetBtn); // Wird als letztes Element im Grid hinzugefügt

        add(numpad, BorderLayout.CENTER);
    }

    /**
     * Hilfsmethode zur Erstellung der einzelnen Ziffern-Buttons für das Numpad.
     * Stellt sicher, dass die Buttons nicht den Fokus stehlen (setFocusable(false)),
     * damit der Cursor im anvisierten Textfeld bleibt.
     * * @param text Die Ziffer oder "C" (Clear) für den Button.
     * @return Der fertig konfigurierte Numpad-Button.
     */
    private JButton createNumButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));

        // WICHTIG: Das Numpad darf den Textfeldern nicht den Fokus stehlen!
        btn.setFocusable(false);

        btn.addActionListener(e -> {
            if (aktivesFeld != null) {
                if (text.equals("C")) {
                    aktivesFeld.setText("");
                } else {
                    // Schreibt die Zahl an das Ende des aktuell gemerkten Feldes
                    aktivesFeld.setText(aktivesFeld.getText() + text);
                }
            }
        });
        return btn;
    }

    /**
     * Führt den eigentlichen Anmeldevorgang durch.
     * Liest Kassierer-Nummer, PIN und den deklarierten Startbestand aus.
     * Sendet diese Daten zur Überprüfung und Schichteröffnung an den Server.
     */
    private void performLogin() {
        try {
            int nr = Integer.parseInt(nrField.getText().trim());
            String pin = new String(pinField.getPassword());

            // Startbestand aus dem Textfeld auslesen
            double bestand = Double.parseDouble(bestandField.getText().trim().replace(",", "."));

            // NEU: Übergabe des 'bestand' an die aktualisierte Client-Methode
            Kassierer k = client.loginKassierer(nr, pin, bestand);

            if (k != null) {
                JOptionPane.showMessageDialog(this, "Willkommen, " + k.getName() + "\nBargeldbestand: " + bestand + "€ bestätigt.");
                dispose();

                new KassenUI(client, k).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Login fehlgeschlagen. Nummer oder PIN falsch.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen eingeben.", "Eingabefehler", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verbindungsfehler zum Server.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}