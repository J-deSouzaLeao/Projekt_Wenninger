package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import thw.edu.javaII.port.warehouse.model.Produkt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KassenUI extends JFrame {
    private BackendClient client;
    private Kassierer kassierer;
    private double startBestand;
    private double gesamtSumme = 0.0;

    private DefaultTableModel tableModel;
    private JTable artikelTabelle;
    private JTextField eingabeFeld;
    private JLabel summenLabel;

    public KassenUI(BackendClient client, Kassierer kassierer, double startBestand) {
        this.client = client;
        this.kassierer = kassierer;
        this.startBestand = startBestand;

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
        bezahlenBtn.setFont(new Font("Arial", Font.BOLD, 24));
        bezahlenBtn.setBackground(new Color(70, 130, 180));
        bezahlenBtn.setForeground(Color.WHITE);

        JButton stornoBtn = new JButton("STORNO");
        stornoBtn.setFont(new Font("Arial", Font.BOLD, 24));
        stornoBtn.setBackground(new Color(220, 20, 60));
        stornoBtn.setForeground(Color.WHITE);

        aktionPanel.add(bezahlenBtn);
        aktionPanel.add(stornoBtn);
        eingabePanel.add(aktionPanel, BorderLayout.SOUTH);

        add(eingabePanel, BorderLayout.EAST);
    }

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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Verbindungsfehler zum Server.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}