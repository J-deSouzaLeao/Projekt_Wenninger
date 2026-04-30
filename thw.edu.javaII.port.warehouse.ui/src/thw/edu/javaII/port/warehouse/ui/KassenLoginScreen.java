package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import javax.swing.*;
import java.awt.*;

public class KassenLoginScreen extends JFrame {
    private JTextField nrField;
    private JPasswordField pinField;
    private JTextField bestandField;
    private BackendClient client;

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
        loginBtn.addActionListener(e -> performLogin());
        numpad.add(loginBtn);

        add(numpad, BorderLayout.CENTER);
    }

    private JButton createNumButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.addActionListener(e -> {
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner instanceof JTextField) {
                JTextField field = (JTextField) focusOwner;
                if (text.equals("C")) {
                    field.setText("");
                } else {
                    field.setText(field.getText() + text);
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
                new KassenUI(client, k, bestand).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Login fehlgeschlagen. Nummer oder PIN falsch.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen eingeben.", "Eingabefehler", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Verbindungsfehler zum Server.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}