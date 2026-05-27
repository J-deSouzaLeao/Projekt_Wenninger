package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Starter extends Thread {

	private static final Logger LOGGER = Logger.getLogger(Starter.class.getName());
	private final Session ses;

	public Starter(Session ses) {
		this.ses = ses;
	}

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