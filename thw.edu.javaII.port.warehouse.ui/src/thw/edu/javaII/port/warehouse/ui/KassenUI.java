package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import thw.edu.javaII.port.warehouse.model.Produkt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Stellt die grafische Benutzeroberfläche für die Kasse dar.
 * Beinhaltet die Artikelanzeige (Kassenzettel), ein dynamisches Numpad zur Eingabe
 * von Artikelnummern und Mengen sowie Funktionen für Bezahlung, Storno und Kassenabschluss.
 * * @author juan.de.souza.leao
 */
public class KassenUI extends JFrame {
    private final BackendClient client;
    private final Kassierer kassierer;
    private double gesamtSumme = 0.0;

    private final DefaultTableModel tableModel;
    private final JTable artikelTabelle;
    private final JTextField txtProduktId;
    private final JTextField txtMenge;
    private final JLabel summenLabel;

    private int stornoZaehler = 0;

    /**
     * Initialisiert das Kassenfenster für den angemeldeten Kassierer.
     * Baut das Layout auf (Tabelle links, Numpad und Aktionen rechts)
     * und integriert die Mengen-Eingabe.
     * * @param client    Der BackendClient für die Serverkommunikation.
     * @param kassierer Der aktuell angemeldete Kassierer.
     */
    public KassenUI(BackendClient client, Kassierer kassierer) {
        this.client = client;
        this.kassierer = kassierer;

        setTitle("Kasse - Kassierer: " + kassierer.getName() + " (Nr. " + kassierer.getNummer() + ")");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Linker Bereich: Kassenzettel (Tabelle) ---
        JPanel zettelPanel = new JPanel(new BorderLayout());
        String[] spalten = {"Art-Nr.", "Name", "Menge", "Einzelpreis", "Gesamt"};
        tableModel = new DefaultTableModel(spalten, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabelle nicht direkt editierbar
            }
        };
        artikelTabelle = new JTable(tableModel);
        artikelTabelle.setFont(new Font("Arial", Font.PLAIN, 18));
        artikelTabelle.setRowHeight(30);
        artikelTabelle.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));

        zettelPanel.add(new JScrollPane(artikelTabelle), BorderLayout.CENTER);

        // Summen-Anzeige
        JPanel summenPanel = new JPanel(new BorderLayout());
        summenPanel.setBackground(Color.DARK_GRAY);
        summenLabel = new JLabel("Gesamt: 0.00 €", SwingConstants.RIGHT);
        summenLabel.setFont(new Font("Arial", Font.BOLD, 36));
        summenLabel.setForeground(Color.WHITE);
        summenLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summenPanel.add(summenLabel, BorderLayout.CENTER);
        zettelPanel.add(summenPanel, BorderLayout.SOUTH);

        add(zettelPanel, BorderLayout.CENTER);

        // --- Rechter Bereich: Touch-Eingabe und Numpad ---
        JPanel eingabePanel = new JPanel(new BorderLayout());
        eingabePanel.setPreferredSize(new Dimension(380, 0));
        eingabePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Obere Eingabefelder (Produkt-ID und Menge)
        JPanel textFeldPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        JLabel lblId = new JLabel("ID:");
        lblId.setFont(new Font("Arial", Font.BOLD, 22));
        textFeldPanel.add(lblId);

        txtProduktId = new JTextField();
        txtProduktId.setFont(new Font("Arial", Font.BOLD, 28));
        txtProduktId.setHorizontalAlignment(JTextField.RIGHT);
        textFeldPanel.add(txtProduktId);

        JLabel lblMenge = new JLabel("Menge:");
        lblMenge.setFont(new Font("Arial", Font.BOLD, 22));
        textFeldPanel.add(lblMenge);

        txtMenge = new JTextField("1");
        txtMenge.setFont(new Font("Arial", Font.BOLD, 28));
        txtMenge.setHorizontalAlignment(JTextField.CENTER);
        textFeldPanel.add(txtMenge);

        eingabePanel.add(textFeldPanel, BorderLayout.NORTH);

        // Numpad
        JPanel numpad = new JPanel(new GridLayout(4, 3, 5, 5));
        for (int i = 1; i <= 9; i++) {
            numpad.add(createNumButton(String.valueOf(i)));
        }
        numpad.add(createNumButton("C"));
        numpad.add(createNumButton("0"));

        JButton enterBtn = new JButton("Enter");
        enterBtn.setBackground(new Color(60, 179, 113));
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFont(new Font("Arial", Font.BOLD, 24));
        enterBtn.addActionListener(e -> artikelHinzufuegen());
        numpad.add(enterBtn);

        eingabePanel.add(numpad, BorderLayout.CENTER);

        // Aktions-Buttons
        JPanel aktionPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton bezahlenBtn = new JButton("BEZAHLEN");
        bezahlenBtn.addActionListener(e -> bezahlenVorgang());
        bezahlenBtn.setFont(new Font("Arial", Font.BOLD, 24));
        bezahlenBtn.setBackground(new Color(70, 130, 180));
        bezahlenBtn.setForeground(Color.WHITE);

        JButton stornoBtn = new JButton("STORNO");
        stornoBtn.addActionListener(e -> stornoVorgang());
        stornoBtn.setFont(new Font("Arial", Font.BOLD, 24));
        stornoBtn.setBackground(new Color(220, 20, 60));
        stornoBtn.setForeground(Color.WHITE);

        JButton abschlussBtn = new JButton("ABSCHLUSS");
        abschlussBtn.addActionListener(e -> new KassenabschlussDialog(this, client, kassierer).setVisible(true));
        abschlussBtn.setFont(new Font("Arial", Font.BOLD, 24));
        abschlussBtn.setBackground(new Color(255, 140, 0));
        abschlussBtn.setForeground(Color.WHITE);

        // Manager-Prüfung für erweiterte Optionen
        if (kassierer.isManager()) {
            aktionPanel.setLayout(new GridLayout(4, 1, 5, 5));
            JButton adminBtn = new JButton("ADMIN");
            adminBtn.setBackground(Color.DARK_GRAY);
            adminBtn.setForeground(Color.WHITE);
            adminBtn.addActionListener(e -> new AdminKassiererDialog(this, client, kassierer).setVisible(true));

            aktionPanel.add(bezahlenBtn);
            aktionPanel.add(stornoBtn);
            aktionPanel.add(abschlussBtn);
            aktionPanel.add(adminBtn);
        } else {
            aktionPanel.add(bezahlenBtn);
            aktionPanel.add(stornoBtn);
            aktionPanel.add(abschlussBtn);
        }

        eingabePanel.add(aktionPanel, BorderLayout.SOUTH);
        add(eingabePanel, BorderLayout.EAST);

        // Initialen Fokus setzen
        SwingUtilities.invokeLater(txtProduktId::requestFocus);
    }

    /**
     * Erstellt einen Button für das Numpad und deaktiviert dessen Fokus,
     * damit der Cursor im jeweiligen Textfeld (ID oder Menge) bleibt.
     * * @param text Die Beschriftung des Buttons (Zahl oder "C").
     * @return Der konfigurierte JButton.
     */
    private JButton createNumButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.setFocusable(false); // Verhindert, dass der Button den Fokus klaut
        btn.addActionListener(e -> {
            if (text.equals("C")) {
                txtProduktId.setText("");
                txtMenge.setText("1");
                txtProduktId.requestFocus();
            } else {
                appendNumpadDigit(text);
            }
        });
        return btn;
    }

    /**
     * Ein intelligenter Listener für die Numpad-Tasten.
     * Fügt die Ziffer automatisch in das Feld ein, in dem der Cursor gerade steht.
     * * @param digit Die gedrückte Ziffer.
     */
    private void appendNumpadDigit(String digit) {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

        if (focusOwner instanceof JTextField aktivesFeld) {

            // Wenn im Mengen-Feld noch die standardmäßige "1" steht, überschreiben wir sie
            if (aktivesFeld == txtMenge && aktivesFeld.getText().equals("1")) {
                aktivesFeld.setText(digit);
            } else {
                aktivesFeld.setText(aktivesFeld.getText() + digit);
            }
        } else {
            // Fallback: Wenn nichts markiert ist, schreiben wir in die Produkt-ID
            txtProduktId.setText(txtProduktId.getText() + digit);
        }
    }

    /**
     * Liest die Artikelnummer und die gewünschte Menge aus, ruft das Produkt vom Server ab
     * und fügt es dem aktuellen Kassenzettel hinzu. Setzt die Eingabefelder danach zurück.
     */
    private void artikelHinzufuegen() {
        try {
            int artNr = Integer.parseInt(txtProduktId.getText().trim());
            int menge = Integer.parseInt(txtMenge.getText().trim());

            if (menge <= 0) {
                JOptionPane.showMessageDialog(this, "Die Menge muss mindestens 1 betragen.", "Ungültige Menge", JOptionPane.WARNING_MESSAGE);
                txtMenge.setText("1");
                return;
            }

            Produkt p = client.getProduktById(artNr);

            if (p != null && p.getName() != null) {
                double gesamt = p.getPreis() * menge;

                tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        menge,
                        String.format("%.2f €", p.getPreis()),
                        String.format("%.2f €", gesamt)
                });

                gesamtSumme += gesamt;
                summenLabel.setText(String.format("Gesamt: %.2f €", gesamtSumme));

                // Felder zurücksetzen
                txtProduktId.setText("");
                txtMenge.setText("1");
                txtProduktId.requestFocus(); // Direkt bereit für den nächsten Artikel
            } else {
                JOptionPane.showMessageDialog(this, "Artikelnummer nicht gefunden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                txtProduktId.setText("");
                txtProduktId.requestFocus();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte eine gültige Artikelnummer und Menge eingeben.", "Hinweis", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verbindungsfehler zum Server.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Entfernt den in der Tabelle ausgewählten Artikel vom Kassenzettel.
     * Nach zwei Stornovorgängen wird die Autorisierung durch einen Manager erzwungen.
     */
    private void stornoVorgang() {
        int selectedRow = artikelTabelle.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Bitte einen Artikel zum Stornieren in der Tabelle auswählen.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Autorisierung prüfen nach 2 Stornos
        if (stornoZaehler >= 2) {
            boolean autorisiert = false;
            while (!autorisiert) {
                String chefNrStr = JOptionPane.showInputDialog(this, "Sicherheits-Sperre! 2 Stornos erreicht.\nManagerfreigabe erforderlich:");
                if (chefNrStr == null) return; // Abbrechen gedrückt
                String chefPin = showPasswordDialog();
                if (chefPin == null) return;

                try {
                    int chefNr = Integer.parseInt(chefNrStr);
                    if (chefNr == this.kassierer.getNummer()) {
                        JOptionPane.showMessageDialog(this, "Sie können sich nicht selbst autorisieren!", "Fehler", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    // WICHTIG: loginKassierer erfordert nun den Startbestand, für die reine Autorisierung senden wir 0.0
                    Kassierer chef = client.loginKassierer(chefNr, chefPin, 0.0);

                    if (chef != null && chef.isManager()) {
                        autorisiert = true;
                        stornoZaehler = 0; // Reset nach Autorisierung
                        JOptionPane.showMessageDialog(this, "Autorisierung durch " + chef.getName() + " erfolgreich.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Autorisierung fehlgeschlagen! Keine Manager-Rechte oder PIN falsch.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ungültige Eingabe oder Verbindungsfehler.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Artikel aus Tabelle entfernen und Summe anpassen
        String gesamtStr = (String) tableModel.getValueAt(selectedRow, 4);
        double gesamtPos = Double.parseDouble(gesamtStr.replace(" €", "").replace(",", "."));

        gesamtSumme -= gesamtPos;
        summenLabel.setText(String.format("Gesamt: %.2f €", Math.max(0, gesamtSumme)));

        tableModel.removeRow(selectedRow);
        stornoZaehler++;

        txtProduktId.requestFocus();
    }

    /**
     * Öffnet einen Dialog zur sicheren PIN-Eingabe.
     * * @return Die eingegebene PIN als String oder null bei Abbruch.
     */
    private String showPasswordDialog() {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Bitte PIN eingeben:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (okCxl == JOptionPane.OK_OPTION) {
            return new String(pf.getPassword());
        }
        return null;
    }

    /**
     * Startet den Bezahlvorgang.
     * Öffnet den Zahlungsdialog, erstellt bei Erfolg das Kassenzettel-Objekt
     * und sendet dieses zur Verbuchung an den Backend-Server. Setzt danach die Kasse zurück.
     */
    private void bezahlenVorgang() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Der Kassenzettel ist leer!", "Hinweis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ZahlungDialog dialog = new ZahlungDialog(this, gesamtSumme);
        dialog.setVisible(true);

        if (dialog.isErfolgreich()) {
            // Kassenzettel-Objekt aufbauen
            thw.edu.javaII.port.warehouse.model.Kassenzettel zettel = new thw.edu.javaII.port.warehouse.model.Kassenzettel();
            zettel.setZahlart(dialog.getGewaehlteZahlart());
            zettel.setGesamtpreis(gesamtSumme);
            zettel.setKassierer(this.kassierer);

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
            java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
            zettel.setDatum(now.format(dateFormatter));
            zettel.setUhrzeit(now.format(timeFormatter));

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                thw.edu.javaII.port.warehouse.model.KassenzettelPosition pos = new thw.edu.javaII.port.warehouse.model.KassenzettelPosition();

                int prodId = (int) tableModel.getValueAt(i, 0);
                Produkt p = new Produkt();
                p.setId(prodId);
                pos.setProdukt(p);

                pos.setAnzahl((int) tableModel.getValueAt(i, 2));

                String preisStr = (String) tableModel.getValueAt(i, 4);
                pos.setGesamtpreis(Double.parseDouble(preisStr.replace(" €", "").replace(",", ".")));

                zettel.getPositionen().add(pos);
            }

            try {
                // An Server senden
                client.kassenzettelBuchen(zettel);
                JOptionPane.showMessageDialog(this, "Bezahlung erfolgreich abgeschlossen!\nZahlart: " + dialog.getGewaehlteZahlart());

                // UI für nächsten Kunden zurücksetzen
                tableModel.setRowCount(0);
                gesamtSumme = 0.0;
                stornoZaehler = 0;
                summenLabel.setText("Gesamt: 0.00 €");

                txtProduktId.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Verbuchen: " + ex.getMessage(), "Backend-Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}