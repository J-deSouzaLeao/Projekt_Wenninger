package thw.edu.javaII.port.warehouse.ui;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Diese Klasse dient als Test- und Startumgebung für das Kassen-Modul (Point of Sale).
 * Sie baut eine initiale Verbindung zum Backend-Server auf, befüllt die Datenbank
 * testweise mit Demo-Daten und führt einen kurzen Diagnose-Check (Konsolenausgabe
 * aller Produkte) durch, um sicherzustellen, dass die Datenverbindung korrekt arbeitet.
 * Anschließend wird der Kassen-Login-Bildschirm für den Benutzer geöffnet.
 * Sollte der Server nicht erreichbar sein, wird der Programmabsturz verhindert
 * und stattdessen eine saubere Fehlermeldung als Pop-up angezeigt.
 */
public class KassenLoginTest {

    /**
     * Der Haupteinstiegspunkt für den Testlauf der Kasse.
     * Sorgt dafür, dass der Netzwerkaufbau und die grafische Oberfläche sicher
     * im Event Dispatch Thread (EDT) von Java Swing ausgeführt werden.
     * * @param args Kommandozeilenargumente (werden nicht verwendet).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Verbinde mit dem Server...");
                BackendClient client = new BackendClient();

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
                KassenLoginScreen login = new KassenLoginScreen(client);
                login.setVisible(true);

            } catch (Exception e) {
                // printStackTrace() entfernt, stattdessen nur das saubere GUI-Popup
                JOptionPane.showMessageDialog(null,
                        "Verbindung zum Server fehlgeschlagen! Läuft Server.java?",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}