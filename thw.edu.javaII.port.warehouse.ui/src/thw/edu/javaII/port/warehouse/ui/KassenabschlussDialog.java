package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassenabschluss;
import thw.edu.javaII.port.warehouse.model.Kassierer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Diese Klasse repräsentiert den Dialog zur Durchführung eines Kassenabschlusses (Tagesabschluss).
 * Sie stellt eine Benutzeroberfläche bereit, in der der Kassierer das vorhandene Bargeld
 * anhand einer Stückelung (Anzahl der jeweiligen Münzen und Scheine) zählen kann.
 * Der ermittelte Ist-Bestand wird live mit dem vom System berechneten Soll-Bestand verglichen.
 * Der Abschluss lässt sich nur speichern, wenn die Differenz exakt 0,00 € beträgt.
 */
public class KassenabschlussDialog extends JDialog {
    private final BackendClient client;
    private final Kassierer kassierer;
    private double sollBestand;
    private double ermittelterIstBestand = 0.0;

    private final JLabel istLabel;
    private final JLabel differenzLabel;
    private final JButton abschlussBtn;

    // Stückelungen: Wert in Euro -> Eingabefeld (z. B. 50.0 -> Textfeld für die Anzahl der 50€-Scheine)
    private final Map<Double, JTextField> stueckelungFields = new LinkedHashMap<>();

    /**
     * Erstellt den Dialog für den Kassenabschluss.
     * Lädt den aktuellen Soll-Bestand vom Server, baut die Benutzeroberfläche
     * für die Zählung der Stückelungen auf und richtet die Live-Berechnung ein.
     * * @param parent    Das aufrufende Hauptfenster (für die modale Blockierung).
     * @param client    Die Netzwerkverbindung für den Datenabruf und das Speichern.
     * @param kassierer Der Kassierer, der diesen Abschluss durchführt.
     */
    public KassenabschlussDialog(JFrame parent, BackendClient client, Kassierer kassierer) {
        super(parent, "Kassenabschluss durchführen", true);
        this.client = client;
        this.kassierer = kassierer;

        setSize(500, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        ladeSollBestand();

        // Header: Anzeige von Soll, Ist und Differenz
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel sollLabel = new JLabel(String.format("Sollbestand: %.2f €", sollBestand), SwingConstants.CENTER);
        sollLabel.setFont(new Font("Arial", Font.BOLD, 22));
        istLabel = new JLabel("Istbestand: 0.00 €", SwingConstants.CENTER);
        istLabel.setFont(new Font("Arial", Font.BOLD, 22));
        differenzLabel = new JLabel("Differenz: 0.00 €", SwingConstants.CENTER);
        differenzLabel.setFont(new Font("Arial", Font.BOLD, 20));

        headerPanel.add(sollLabel);
        headerPanel.add(istLabel);
        headerPanel.add(differenzLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Zähl-Grid (Münzen und Scheine)
        JPanel zaehlPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        zaehlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        double[] werte = {200.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.0, 1.0, 0.50, 0.20, 0.10, 0.05, 0.02, 0.01};
        for (double w : werte) {
            zaehlPanel.add(new JLabel(String.format(w >= 5 ? "%.0f € Schein:" : "%.2f € Münze:", w)));
            JTextField tf = new JTextField("0");
            tf.setHorizontalAlignment(JTextField.RIGHT);
            tf.getDocument().addDocumentListener(new InputBerechner());
            stueckelungFields.put(w, tf);
            zaehlPanel.add(tf);
        }
        add(new JScrollPane(zaehlPanel), BorderLayout.CENTER);

        // Button: Speichern
        abschlussBtn = new JButton("ABSCHLUSS SPEICHERN");
        abschlussBtn.setBackground(new Color(60, 179, 113));
        abschlussBtn.setForeground(Color.WHITE);
        abschlussBtn.setFont(new Font("Arial", Font.BOLD, 20));
        abschlussBtn.setEnabled(false); // Erst freigeben, wenn Differenz == 0
        abschlussBtn.addActionListener(e -> abschlussSpeichern());
        add(abschlussBtn, BorderLayout.SOUTH);

        berechneIst(); // Initial kalkulieren
    }

    /**
     * Ruft die für den Soll-Bestand nötigen Daten vom Server ab.
     * Die Berechnung ergibt sich aus dem Bargeldbestand des letzten Abschlusses
     * zuzüglich aller neuen Bareinnahmen seitdem.
     */
    private void ladeSollBestand() {
        try {
            double[] daten = client.getAbschlussDaten();
            double letzterIst = daten[0];
            double neuBar = daten[1];
            this.sollBestand = letzterIst + neuBar;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Abschlussdaten.", "Fehler", JOptionPane.ERROR_MESSAGE);
            this.sollBestand = 0.0;
        }
    }

    /**
     * Rechnet die Eingaben aus allen Stückelungs-Textfeldern zusammen,
     * aktualisiert den angezeigten Ist-Bestand sowie die Differenz.
     * Schaltet zudem den Speichern-Button frei, sobald die Differenz 0 beträgt.
     */
    private void berechneIst() {
        ermittelterIstBestand = 0.0;
        for (Map.Entry<Double, JTextField> entry : stueckelungFields.entrySet()) {
            try {
                String text = entry.getValue().getText().trim();
                int anzahl = text.isEmpty() ? 0 : Integer.parseInt(text);
                ermittelterIstBestand += (anzahl * entry.getKey());
            } catch (NumberFormatException ignored) {}
        }

        istLabel.setText(String.format("Istbestand: %.2f €", ermittelterIstBestand));
        double differenz = ermittelterIstBestand - sollBestand;
        differenzLabel.setText(String.format("Differenz: %.2f €", differenz));

        // Abgleich auf den Cent genau (umgehung von Fließkomma-Ungenauigkeiten)
        if (Math.abs(differenz) < 0.01) {
            differenzLabel.setForeground(new Color(60, 179, 113));
            abschlussBtn.setEnabled(true);
        } else {
            differenzLabel.setForeground(Color.RED);
            abschlussBtn.setEnabled(false);
        }
    }

    /**
     * Erstellt ein Kassenabschluss-Objekt aus den berechneten Daten,
     * versieht es mit dem aktuellen Zeitstempel und sendet es an den Server.
     * Bei Erfolg wird das Programmfenster komplett geschlossen (Ende der Schicht).
     */
    private void abschlussSpeichern() {
        try {
            Kassenabschluss a = new Kassenabschluss();
            a.setKassierer(kassierer);
            a.setSollBestand(sollBestand);
            a.setIstBestand(ermittelterIstBestand);

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            a.setDatum(now.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            a.setUhrzeit(now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));

            client.saveKassenabschluss(a);
            JOptionPane.showMessageDialog(this, "Kassenabschluss erfolgreich gespeichert!");
            dispose();
            System.exit(0); // Beendet das Kassen-Terminal nach Abschluss
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern des Abschlusses: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Eine interne Hilfsklasse (Listener), die sofort reagiert, wenn der Benutzer
     * etwas in eines der Zähl-Textfelder eintippt oder löscht. Sie löst daraufhin
     * direkt eine Neuberechnung des Ist-Bestandes aus.
     */
    private class InputBerechner implements DocumentListener {
        public void insertUpdate(DocumentEvent e) { berechneIst(); }
        public void removeUpdate(DocumentEvent e) { berechneIst(); }
        public void changedUpdate(DocumentEvent e) { berechneIst(); }
    }
}