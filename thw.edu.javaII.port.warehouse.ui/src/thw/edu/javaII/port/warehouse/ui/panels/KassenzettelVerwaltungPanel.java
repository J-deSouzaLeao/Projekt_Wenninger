package thw.edu.javaII.port.warehouse.ui.panels;

import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.ui.BackendClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class KassenzettelVerwaltungPanel extends JPanel {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(KassenzettelVerwaltungPanel.class.getName());
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;

    public KassenzettelVerwaltungPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Such- und Filterbereich
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Suchen / Filtern:"), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // Tabelle
        String[] columns = {"ID", "Datum", "Uhrzeit", "Zahlart", "Kassierer", "Gesamtpreis (€)"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Dynamischer Filter-Listener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // (?i) macht es Case-Insensitive
                }
            }
        });

        loadData();
    }

    private void loadData() {
        try {
            BackendClient client = new BackendClient();
            List<Kassenzettel> list = client.getAllKassenzettel();
            for (Kassenzettel z : list) {
                String kassiererName = z.getKassierer() != null ? z.getKassierer().getName() : "Unbekannt";
                model.addRow(new Object[]{
                        z.getId(),
                        z.getDatum(),
                        z.getUhrzeit(),
                        z.getZahlart(),
                        kassiererName,
                        String.format("%.2f", z.getGesamtpreis())
                });
            }
            client.close();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Ein Fehler ist beim Laden der Daten aufgetreten", e);
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Kassenzettel.");
        }
    }
}