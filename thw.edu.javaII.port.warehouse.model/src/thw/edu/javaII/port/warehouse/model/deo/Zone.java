package thw.edu.javaII.port.warehouse.model.deo;

/**
 * Dieses Enum definiert die verschiedenen logischen Bereiche (Zonen) des Systems.
 * Es wird bei der Netzwerkkommunikation genutzt, um Anfragen zwischen dem Client
 * und dem Server direkt dem richtigen Modul zuzuordnen.
 * * @author juan.de.souza.leao
 */
public enum Zone {

	/** Bereich für die Verwaltung der Produktstammdaten. */
	PRODUKT,

	/** Bereich für die Anlage und Verwaltung von Lagern. */
	LAGER,

	/** Bereich für die detaillierte Verwaltung einzelner Lagerplätze. */
	LAGERPLATZ,

	/** Bereich für die Buchung und Abfrage von Lagerbeständen. */
	LAGERBESTAND,

	/** Spezieller Bereich zum Initialisieren der Demo-Datenbank. */
	INIT,

	/** Bereich zum Abrufen von Kennzahlen und Auswertungen. */
	STATISTIK,

	/** Allgemeiner Bereich für systemweite Basis-Anfragen. */
	GENERAL,

	/** Bereich für Test- und Demonstrationszwecke. */
	DEMO,

	/** Bereich für das Touch-Terminal, Kassierer und Kassenbons. */
	KASSE
}