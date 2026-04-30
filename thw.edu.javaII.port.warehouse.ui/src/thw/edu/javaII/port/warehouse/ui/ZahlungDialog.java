package thw.edu.javaII.port.warehouse.ui;

import javax.swing.*;
import java.awt.*;

public class ZahlungDialog extends JDialog {
    private double zuZahlen;
    private boolean erfolgreich = false;
    private String gewaehlteZahlart = "";
    private JTextField gegebenFeld;
    private JLabel rueckgeldLabel;

    public ZahlungDialog(JFrame parent, double zuZahlenSumme) {
        super(parent, "Bezahlvorgang", true);
        this.zuZahlen = zuZahlenSumme;

        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Kopfbereich
        JLabel summenLabel = new JLabel(String.format("Zu zahlen: %.2f €", zuZahlen), SwingConstants.CENTER);
        summenLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(summenLabel, BorderLayout.NORTH);

        // Mittelbereich (Eingabe)
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(new JLabel("Gegeben (€):"));
        gegebenFeld = new JTextField();
        gegebenFeld.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(gegebenFeld);

        centerPanel.add(new JLabel("Rückgeld (€):"));
        rueckgeldLabel = new JLabel("0.00", SwingConstants.RIGHT);
        rueckgeldLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rueckgeldLabel.setForeground(Color.RED);
        centerPanel.add(rueckgeldLabel);

        JButton berechnenBtn = new JButton("Rückgeld berechnen");
        berechnenBtn.addActionListener(e -> berechneRueckgeld());
        centerPanel.add(new JLabel("")); // Platzhalter
        centerPanel.add(berechnenBtn);

        add(centerPanel, BorderLayout.CENTER);

        // Aktionsbereich (Zahlarten)
        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton barBtn = new JButton("BAR ZAHLEN");
        barBtn.setBackground(new Color(60, 179, 113));
        barBtn.setForeground(Color.WHITE);
        barBtn.addActionListener(e -> schliesseErfolgreich("Bar"));

        JButton ecBtn = new JButton("EC-KARTE (SIMULATION)");
        ecBtn.setBackground(new Color(70, 130, 180));
        ecBtn.setForeground(Color.WHITE);
        ecBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "EC-Zahlung wird verarbeitet...\nZahlung erfolgreich!", "EC-Terminal", JOptionPane.INFORMATION_MESSAGE);
            schliesseErfolgreich("EC-Karte");
        });

        actionPanel.add(barBtn);
        actionPanel.add(ecBtn);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void berechneRueckgeld() {
        try {
            double gegeben = Double.parseDouble(gegebenFeld.getText().replace(",", "."));
            if (gegeben >= zuZahlen) {
                double rueck = gegeben - zuZahlen;
                rueckgeldLabel.setText(String.format("%.2f", rueck));
                rueckgeldLabel.setForeground(new Color(60, 179, 113));
            } else {
                rueckgeldLabel.setText("Zu wenig!");
                rueckgeldLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ungültiger Betrag!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void schliesseErfolgreich(String zahlart) {
        if (zahlart.equals("Bar")) {
            try {
                double gegeben = Double.parseDouble(gegebenFeld.getText().replace(",", "."));
                if (gegeben < zuZahlen) {
                    JOptionPane.showMessageDialog(this, "Gegebener Betrag reicht nicht aus!", "Fehler", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Bitte zuerst Barbetrag eingeben!", "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        this.gewaehlteZahlart = zahlart;
        this.erfolgreich = true;
        dispose();
    }

    public boolean isErfolgreich() { return erfolgreich; }
    public String getGewaehlteZahlart() { return gewaehlteZahlart; }
}