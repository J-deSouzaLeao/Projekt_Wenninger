package thw.edu.javaII.port.warehouse.ui;

/**
 * Dieses Enum definiert alle verfügbaren Befehle (Action Commands) für die Navigation
 * über die Hauptmenüleiste der grafischen Benutzeroberfläche.
 * Durch die Verwendung eines Enums anstelle von einfachen Text-Strings (Strings)
 * wird die Fehleranfälligkeit (z. B. durch Tippfehler) verringert und die Verarbeitung
 * der Klicks im {@link LagerUIHandler} deutlich sicherer und übersichtlicher.
 *
 * @author barbara.liegnitz
 */
public enum MenuActionCommands {

	/**
	 * Befehl zum Wechseln auf die Startseite (Willkommensbildschirm).
	 */
	STARTSEITE,

	/**
	 * Befehl zum regulären Beenden der Client-Anwendung.
	 */
	BEENDEN,

	/**
	 * Befehl, der nicht nur den Client schließt, sondern auch ein Signal an den
	 * Backend-Server sendet, damit dieser komplett heruntergefahren wird.
	 */
	SERVERBEENDEN,

	/**
	 * Befehl zum Wechseln auf die reine Bestandsübersicht (Lese-Ansicht).
	 */
	BESTAND,

	/**
	 * Befehl zum Wechseln auf die Statistikseite (Top 10 / Low 10).
	 */
	STATISTIK,

	/**
	 * Befehl zum Wechseln auf die Such- und Bearbeitungsseite für Lagerbestände.
	 */
	SUCHEN,

	/**
	 * Befehl zum Anzeigen der Programminformationen (Version, Ersteller etc.).
	 */
	INFO
}