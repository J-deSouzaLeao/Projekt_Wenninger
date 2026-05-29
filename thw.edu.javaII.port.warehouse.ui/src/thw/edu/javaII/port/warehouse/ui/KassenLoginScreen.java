package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Diese Klasse repräsentiert den Login-Bildschirm für das Kassen-Terminal (Point of Sale).
 * Sie ist speziell für die Touchscreen-Bedienung optimiert und enthält ein eigenes Numpad
 * zur Eingabe der Kassierer-Nummer, der PIN und des anfänglichen Kassenbestands.
 * Beinhaltet eine Sicherheitsfunktion, die bei wiederholten Fehlversuchen eine Managerfreigabe erzwingt.
 *
 * @author barbara.liegnitz
 */
public class KassenLoginScreen extends JFrame {
    private final JTextField nrField;
    private final JPasswordField pinField;
    private final JTextField bestandField;
    private final BackendClient client;

    /** Speichert, welches Feld gerade vom Benutzer ausgewählt ist. */
    private JTextField aktivesFeld;

    /** Speichert die aktuelle Anzahl aufeinanderfolgender Fehlversuche. */
    private int fehlversuche = 0;

    /** Definiert das Limit, ab dem eine Managerfreigabe erzwungen wird. */
    private static final int MAX_FEHLVERSUCHE = 3;

    /**
     * Erstellt das Login-Fenster und initialisiert die Benutzeroberfläche.
     * Baut die Eingabefelder auf und richtet einen Focus-Listener ein.
     *
     * @param client Der BackendClient für die Serverkommunikation (Authentifizierung).
     */
    public KassenLoginScreen(BackendClient client) {
        this.client = client;
        setTitle("Kassen-Terminal Login");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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

        aktivesFeld = nrField;
        FocusAdapter focusTracker = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                aktivesFeld = (JTextField) e.getComponent();
            }
        };
        nrField.addFocusListener(focusTracker);
        pinField.addFocusListener(focusTracker);
        bestandField.addFocusListener(focusTracker);

        JPanel numpad = new JPanel(new GridLayout(0, 3, 5, 5));
        for (int i = 1; i <= 9; i++) {
            numpad.add(createNumButton(String.valueOf(i)));
        }
        numpad.add(createNumButton("C"));
        numpad.add(createNumButton("0"));

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(60, 179, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 20));
        loginBtn.setFocusable(false);
        loginBtn.addActionListener(e -> performLogin());
        numpad.add(loginBtn);

        JButton pinResetBtn = new JButton("<html><center>PIN<br>vergessen?</center></html>");
        pinResetBtn.setMargin(new Insets(2, 2, 2, 2));
        pinResetBtn.setBackground(new Color(255, 140, 0));
        pinResetBtn.setForeground(Color.WHITE);
        pinResetBtn.setFont(new Font("Arial", Font.BOLD, 16));
        pinResetBtn.setFocusable(false);
        pinResetBtn.addActionListener(e -> new KassenPinResetDialog(this, client).setVisible(true));

        numpad.add(new JLabel(""));
        numpad.add(pinResetBtn);

        add(numpad, BorderLayout.CENTER);
    }

    /**
     * Hilfsmethode zur Erstellung der einzelnen Ziffern-Buttons für das Numpad.
     *
     * @param text Die Ziffer oder "C" (Clear) für den Button.
     * @return Der fertig konfigurierte Numpad-Button.
     */
    private JButton createNumButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setFocusable(false);

        btn.addActionListener(e -> {
            if (aktivesFeld != null) {
                if (text.equals("C")) {
                    aktivesFeld.setText("");
                } else {
                    aktivesFeld.setText(aktivesFeld.getText() + text);
                }
            }
        });
        return btn;
    }

    /**
     * Führt den Anmeldevorgang durch. Zählt Fehlversuche und sperrt das Terminal
     * bei Erreichen des definierten Limits.
     */
    private void performLogin() {
        if (fehlversuche >= MAX_FEHLVERSUCHE) {
            erzwingeManagerFreigabe();
            return;
        }

        try {
            int nr = Integer.parseInt(nrField.getText().trim());
            String pin = new String(pinField.getPassword());
            double bestand = Double.parseDouble(bestandField.getText().trim().replace(",", "."));

            Kassierer k = client.loginKassierer(nr, pin, bestand);

            if (k != null) {
                fehlversuche = 0;
                JOptionPane.showMessageDialog(this, "Willkommen, " + k.getName() + "\nBargeldbestand: " + bestand + "€ bestätigt.");
                dispose();
                new KassenUI(client, k).setVisible(true);
            } else {
                fehlversuche++;
                if (fehlversuche >= MAX_FEHLVERSUCHE) {
                    JOptionPane.showMessageDialog(this, "Zu viele Fehlversuche! Das Terminal ist gesperrt. Managerfreigabe erforderlich.", "System gesperrt", JOptionPane.ERROR_MESSAGE);
                    erzwingeManagerFreigabe();
                } else {
                    JOptionPane.showMessageDialog(this, "Login fehlgeschlagen. Nummer oder PIN falsch.\nVersuch " + fehlversuche + " von " + MAX_FEHLVERSUCHE, "Fehler", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen eingeben.", "Eingabefehler", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verbindungsfehler zum Server.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet einen modalen Dialog zur Eingabe von Manager-Logindaten.
     * Prüft die Berechtigung und setzt bei Erfolg den Fehlerzähler zurück.
     * Bricht der Nutzer ab, wird die Anwendung aus Sicherheitsgründen geschlossen.
     */
    private void erzwingeManagerFreigabe() {
        JTextField txtManagerNummer = new JTextField();
        JPasswordField txtManagerPin = new JPasswordField();
        Object[] msg = {
                "Manager-Personalnummer:", txtManagerNummer,
                "Manager-PIN:", txtManagerPin
        };

        int option = JOptionPane.showConfirmDialog(this, msg, "Managerfreigabe erforderlich",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int mgrNummer = Integer.parseInt(txtManagerNummer.getText().trim());
                String mgrPin = new String(txtManagerPin.getPassword());

                Kassierer manager = client.getAllKassierer().stream()
                        .filter(k -> k.getNummer() == mgrNummer)
                        .findFirst()
                        .orElse(null);

                if (manager != null && manager.isManager() && manager.getPin().equals(mgrPin)) {
                    fehlversuche = 0;
                    JOptionPane.showMessageDialog(this, "Freigabe durch Manager '" + manager.getName() + "' erteilt. System entsperrt.", "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Ungültige Managerdaten. Terminal bleibt gesperrt.", "Freigabe abgelehnt", JOptionPane.ERROR_MESSAGE);
                    erzwingeManagerFreigabe();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehlerhafte Eingabe. Terminal bleibt gesperrt.", "Fehler", JOptionPane.ERROR_MESSAGE);
                erzwingeManagerFreigabe();
            }
        } else {
            System.exit(0);
        }
    }
}