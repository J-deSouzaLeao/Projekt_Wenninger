package thw.edu.javaII.port.warehouse.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import thw.edu.javaII.port.warehouse.model.common.Info;

/**
 * Diese Klasse ist der Hauptstartpunkt (Entry Point) für das gesamte Server-Backend der Lagerverwaltung.
 * Sie öffnet einen zentralen Netzwerk-Port und wartet in einer Endlosschleife auf eingehende
 * Verbindungen von Clients (wie dem Kassenterminal oder dem Verwaltungs-PC).
 * Für jeden neu verbundenen Client wird sofort ein eigener Betreuer-Prozess (Service-Thread) gestartet.
 *
 * @author juan.de.souza.leao
 */
public class Server {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Server.class.getName());

    /**
     * Eine globale Steuervariable, die bestimmt, ob der Server weiterlaufen soll.
     * Wird sie (z. B. durch einen speziellen Client-Befehl) auf false gesetzt,
     * nimmt der Server keine neuen Verbindungen mehr an und fährt herunter.
     */
    public static boolean run = true;

    /**
     * Die Hauptmethode, die beim Start des Programms ausgeführt wird.
     * Sie richtet den Server-Socket auf dem vorkonfigurierten Port ein, gibt eine Startmeldung aus
     * und blockiert dann, bis sich ein neuer Client anmeldet, um ihn an einen Service-Thread zu übergeben.
     *
     * @param args Kommandozeilenargumente (werden hier nicht verwendet).
     */
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(Info.PORT_SERVER)) {
            System.out.println("Lagerverwaltungsserver läuft");

            // Solange der Server laufen soll, warte auf neue Clients
            while (run) {
                Socket sock = server.accept();
                new Service(sock).start();
            }
        } catch (IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Fehler im Server aufgetreten", e);
        }
    }

}