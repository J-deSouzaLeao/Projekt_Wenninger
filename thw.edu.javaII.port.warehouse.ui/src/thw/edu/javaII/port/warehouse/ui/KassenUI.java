package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import thw.edu.javaII.port.warehouse.model.Produkt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Stellt die grafische Benutzeroberfläche für die Kasse dar.
 * Beinhaltet die Artikelanzeige (Kassenzettel), ein Numpad zur Eingabe
 * von Artikelnummern sowie Funktionen für Bezahlung, Storno und Kassenabschluss.
 */
public class KassenUI extends JFrame {
    private final BackendClient client;
    private final Kassierer kassierer;
    private double gesamtSumme = 0.0;

    private final DefaultTableModel tableModel;
    private final JTable artikelTabelle;
    private final JTextField eingabeFeld;
    private final JLabel summenLabel;

    private int stornoZaehler = 0;

    /**
     * Initialisiert das Kassenfenster für den angemeldeten Kassierer.
     * Baut das Layout auf (Tabelle links, Numpad und Aktionen rechts).
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
        eingabePanel.setPreferredSize(new Dimension(350, 0));
        eingabePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        eingabeFeld = new JTextField();
        eingabeFeld.setFont(new Font("Arial", Font.BOLD, 36));
        eingabeFeld.setHorizontalAlignment(JTextField.RIGHT);
        eingabePanel.add(eingabeFeld, BorderLayout.NORTH);

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

        // Aktions-Buttons (Vorbereitung für Phase 4)
        JPanel aktionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
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

        aktionPanel.add(bezahlenBtn);
        aktionPanel.add(stornoBtn);
        eingabePanel.add(aktionPanel, BorderLayout.SOUTH);

        add(eingabePanel, BorderLayout.EAST);

        JButton abschlussBtn = new JButton("ABSCHLUSS");
        abschlussBtn.setFont(new Font("Arial", Font.BOLD, 24));
        abschlussBtn.setBackground(new Color(255, 140, 0));
        abschlussBtn.setForeground(Color.WHITE);
        abschlussBtn.addActionListener(e -> new KassenabschlussDialog(this, client, kassierer).setVisible(true));

        // NEU: Manager-Prüfung
        if (kassierer.isManager()) {
            aktionPanel.setLayout(new GridLayout(4, 1, 5, 5)); // 4 Zeilen statt 3
            JButton adminBtn = new JButton("ADMIN");
            adminBtn.setBackground(Color.DARK_GRAY);
            adminBtn.setForeground(Color.WHITE);
            adminBtn.addActionListener(e -> new AdminKassiererDialog(this, client, kassierer).setVisible(true));

            aktionPanel.add(bezahlenBtn);
            aktionPanel.add(stornoBtn);
            aktionPanel.add(abschlussBtn);
            aktionPanel.add(adminBtn); // Extra Button für den Manager
        } else {
            aktionPanel.setLayout(new GridLayout(3, 1, 5, 5));
            aktionPanel.add(bezahlenBtn);
            aktionPanel.add(stornoBtn);
            aktionPanel.add(abschlussBtn);
        }

        // Raster anpassen von 2 Zeilen auf 3 Zeilen
        aktionPanel.setLayout(new GridLayout(3, 1, 5, 5));
        aktionPanel.add(bezahlenBtn);
        aktionPanel.add(stornoBtn);
        aktionPanel.add(abschlussBtn); // NEU
    }

    /**
     * Erstellt einen Button für das Numpad.
     * * @param text Die Beschriftung des Buttons (Zahl oder "C").
     * @return Der konfigurierte JButton.
     */
    private JButton createNumButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.addActionListener(e -> {
            if (text.equals("C")) {
                eingabeFeld.setText("");
            } else {
                eingabeFeld.setText(eingabeFeld.getText() + text);
            }
        });
        return btn;
    }

    /**
     * Liest die Artikelnummer aus dem Eingabefeld, ruft das Produkt vom Server ab
     * und fügt es dem aktuellen Kassenzettel (Tabelle) hinzu.
     */
    private void artikelHinzufuegen() {
        try {
            int artNr = Integer.parseInt(eingabeFeld.getText().trim());
            Produkt p = client.getProduktById(artNr);

            if (p != null && p.getName() != null) {
                // Menge ist hier standardmäßig 1. (Könnte für Phase 4 noch um Multiplikatoren ergänzt werden)
                int menge = 1;
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
                eingabeFeld.setText(""); // Feld nach erfolgreicher Eingabe leeren
            } else {
                JOptionPane.showMessageDialog(this, "Artikelnummer nicht gefunden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                eingabeFeld.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte eine gültige Artikelnummer eingeben.", "Hinweis", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verbindungsfehler zum Server.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Entfernt den in der Tabelle ausgewählten Artikel vom Kassenzettel.
     * Nach zwei Stornovorgängen wird die Autorisierung durch einen anderen Kassierer erzwungen.
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
                    Kassierer chef = client.loginKassierer(chefNr, chefPin);
                    if (chef != null) {
                        autorisiert = true;
                        stornoZaehler = 0; // Reset nach Autorisierung
                        JOptionPane.showMessageDialog(this, "Autorisierung durch " + chef.getName() + " erfolgreich.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Autorisierung fehlgeschlagen!", "Fehler", JOptionPane.ERROR_MESSAGE);
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

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler beim Verbuchen: " + ex.getMessage(), "Backend-Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}