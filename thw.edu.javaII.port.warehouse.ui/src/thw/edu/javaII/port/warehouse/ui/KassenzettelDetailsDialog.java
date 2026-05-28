package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.model.KassenzettelPosition;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ein Dialog (Pop-up), der die detaillierten Positionen (gekauften Artikel)
 * eines bestimmten Kassenzettels in einer Tabelle anzeigt.
 */
public class KassenzettelDetailsDialog extends JDialog {

    /**
     * Erstellt und öffnet den Detail-Dialog für einen Kassenzettel.
     * * @param parent Das aufrufende Hauptfenster.
     * @param zettel Der Kassenzettel, dessen Positionen angezeigt werden sollen.
     */
    public KassenzettelDetailsDialog(JFrame parent, Kassenzettel zettel) {
        // ID auch hier mit Nullen auffüllen für eine konsistente Darstellung
        super(parent, "Kassenzettel Details - Bon Nr. " + String.format("%05d", zettel.getId()), true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Kopfbereich mit Bon-Metadaten
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(new JLabel("<html><b>Datum:</b> " + zettel.getDatum() + " " + zettel.getUhrzeit() + "</html>"));
        headerPanel.add(new JLabel("<html><b>Kassierer:</b> " + zettel.getKassierer().getName() + "</html>"));
        headerPanel.add(new JLabel("<html><b>Zahlart:</b> " + zettel.getZahlart() + "</html>"));
        add(headerPanel, BorderLayout.NORTH);

        // Tabelle für die Positionen konfigurieren
        String[] columns = {"Produkt-ID", "Produktname", "Menge", "Gesamtpreis (€)"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabelle ist nur lesbar
            }
        };

        // Tabelle mit Daten füllen
        if (zettel.getPositionen() != null) {
            for (KassenzettelPosition pos : zettel.getPositionen()) {
                String produktName = pos.getProdukt() != null ? pos.getProdukt().getName() : "Unbekannt";
                int produktId = pos.getProdukt() != null ? pos.getProdukt().getId() : 0;

                tableModel.addRow(new Object[]{
                        produktId,
                        produktName,
                        pos.getAnzahl(),
                        String.format("%.2f", pos.getGesamtpreis())
                });
            }
        }

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Fußbereich mit Gesamtpreis und Schließen-Button
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblGesamt = new JLabel(String.format("Bon-Summe: %.2f €", zettel.getGesamtpreis()));
        lblGesamt.setFont(new Font("Arial", Font.BOLD, 16));
        footerPanel.add(lblGesamt, BorderLayout.WEST);

        JButton btnClose = new JButton("Schließen");
        btnClose.addActionListener(e -> dispose());
        footerPanel.add(btnClose, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }
}