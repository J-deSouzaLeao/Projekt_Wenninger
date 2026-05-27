package thw.edu.javaII.port.warehouse.ui;

import thw.edu.javaII.port.warehouse.ui.common.Session;

/**
 * Hauptklasse und Startpunkt für die grafische Client-Anwendung (Benutzeroberfläche).
 * Diese Klasse initialisiert die zentrale Benutzersitzung (Session), öffnet das
 * Anmeldefenster (LoginScreen) und startet den Hintergrundprozess (Starter),
 * der den erfolgreichen Login überwacht, um anschließend das Hauptfenster zu laden.
 * * @author juan.de.souza.leao
 */
public class Obeflaeche {

	/**
	 * Der Haupteinstiegspunkt des Client-Programms.
	 * * @param args Kommandozeilenargumente (werden hier nicht verwendet).
	 */
	public static void main(String[] args) {
		Session ses = new Session();

		LoginScreen fenster = new LoginScreen(ses);
		fenster.setVisible(true);
		fenster.setResizable(false);

		Starter start = new Starter(ses);
		start.start();
	}

}