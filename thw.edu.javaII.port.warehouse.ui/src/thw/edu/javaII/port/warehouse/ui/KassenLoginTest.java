package thw.edu.javaII.port.warehouse.ui;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class KassenLoginTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Verbinde mit dem Server...");
                BackendClient client = new BackendClient();

                System.out.println("Initialisiere Demo-Datenbank...");
                client.initDemoData(); // <--- DIESE ZEILE HINZUFÜGEN

                System.out.println("Initialisiere Demo-Datenbank...");
                client.initDemoData();

                // --- NEUER DIAGNOSE-BLOCK START ---
                System.out.println("Lade alle Produkte zur Kontrolle...");
                java.util.List<thw.edu.javaII.port.warehouse.model.Produkt> produkte = client.getAllProdukte();
                System.out.println("Anzahl Produkte in DB: " + produkte.size());
                for (thw.edu.javaII.port.warehouse.model.Produkt p : produkte) {
                    System.out.println(" -> Gefundene ID: " + p.getId() + " | Name: " + p.getName());
                }
                // --- NEUER DIAGNOSE-BLOCK ENDE ---

                System.out.println("Öffne Login-Screen...");

                System.out.println("Öffne Login-Screen...");
                KassenLoginScreen login = new KassenLoginScreen(client);
                login.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Verbindung zum Server fehlgeschlagen! Läuft Server.java?",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}