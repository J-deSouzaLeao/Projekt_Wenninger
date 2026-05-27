package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse stellt einen Hintergrundprozess (Thread) dar, der den Anmeldevorgang überwacht.
 * Sie wartet in einer Schleife, bis der Benutzer sich erfolgreich am Login-Screen angemeldet hat
 * (der Login-Status in der Session auf "true" wechselt), und startet anschließend
 * automatisch das Hauptfenster (LagerUI) der Anwendung.
 * * @author juan.de.souza.leao
 */
public class Starter extends Thread {

	private static final Logger LOGGER = Logger.getLogger(Starter.class.getName());
	private final Session ses;

	/**
	 * Konstruktor für den Starter-Thread.
	 * * @param ses Die zentrale Benutzersitzung, deren Login-Status überwacht werden soll.
	 */
	public Starter(Session ses) {
		this.ses = ses;
	}

	/**
	 * Die Hauptlogik des Hintergrund-Threads.
	 * Prüft im Sekundentakt (Polling), ob der Benutzer erfolgreich eingeloggt ist.
	 * Sobald der Login bestätigt wird, verlässt der Thread die Warteschleife und öffnet
	 * die grafische Hauptoberfläche der Lagerverwaltung.
	 */
	@SuppressWarnings("BusyWait") // Bewusste Entscheidung für simples Polling in diesem Rahmen
	@Override
	public void run() {
		while(!ses.isLogin()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Starter-Thread wurde unterbrochen", e);
				// Best Practice: Interrupt-Status des Threads wiederherstellen
				Thread.currentThread().interrupt();
				// Schleife abbrechen, da der Thread beendet werden soll
				break;
			}
		}

		// Nur starten, wenn der Login auch wirklich erfolgreich war
		if (ses.isLogin()) {
			LagerUI.run(ses);
		}
	}
}