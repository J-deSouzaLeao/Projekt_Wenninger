package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class KassenLoginScreen extends JFrame {
    private final JTextField nrField;
    private final JPasswordField pinField;
    private final JTextField bestandField;
    private final BackendClient client;

    // NEU: Speichert, welches Feld gerade vom Benutzer ausgewählt ist
    private JTextField aktivesFeld;

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

        add(numpad, BorderLayout.CENTER);
    }

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

    private void performLogin() {
        try {
            int nr = Integer.parseInt(nrField.getText().trim());
            String pin = new String(pinField.getPassword());
            double bestand = Double.parseDouble(bestandField.getText().trim().replace(",", "."));

            Kassierer k = client.loginKassierer(nr, pin);

            if (k != null) {
                JOptionPane.showMessageDialog(this, "Willkommen, " + k.getName() + "\nBargeldbestand: " + bestand + "€ bestätigt.");
                dispose(); // Schließt das Login-Fenster

                // Neue Kassen-UI öffnen
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