package thw.edu.javaII.port.warehouse.model.deo;

/**
 * Dieses Enum definiert alle möglichen Befehle (Aktionen), die der Client an den Server senden kann.
 * Anhand dieses Befehls weiß der Server genau, welche Datenbankoperation oder Logik
 * er für die gewählte Zone ausführen muss.
 * * @author juan.de.souza.leao
 */
public enum Command {

	/** Fügt einen neuen Datensatz (z. B. ein neues Produkt oder Lager) hinzu. */
	ADD,

	/** Löscht einen vorhandenen Datensatz aus der Datenbank. */
	DELETE,

	/** Aktualisiert die Informationen eines bereits bestehenden Datensatzes. */
	UPDATE,

	/** Fordert eine komplette Liste aller Datensätze eines bestimmten Typs an. */
	LIST,

	/** Startet die Initialisierung, z. B. um die Datenbank mit Testdaten zu füllen. */
	INIT,

	/** Schließt Ressourcen wie eine Datenbankverbindung. */
	CLOSE,

	/** Beendet die aktuelle Sitzung oder die Client-Server-Verbindung. */
	END,

	/** Fordert spezifische Informationen zum aktuellen Lagerbestand an. */
	BESTAND,

	/** Ruft statistische Spitzenwerte ab (z. B. die Produkte mit dem höchsten Bestand). */
	TOP,

	/** Ruft statistische Niedrigwerte ab (z. B. Produkte, die nachbestellt werden müssen). */
	LOW,

	/** Führt eine Suche nach einem bestimmten Begriff oder Kriterium aus. */
	SEARCH,

	/** Fragt einen ganz bestimmten Datensatz anhand seines Modells (z. B. über eine ID) ab. */
	GETBYMODEL,

	/** Führt die Anmeldung eines Benutzers durch (wird für den Kassierer-Login am Terminal genutzt). */
	LOGIN,

	/** Schließt einen Einkauf ab: Reduziert den Lagerbestand und speichert den Kassenbon. */
	KAUFEN,

	/** Lädt die nötigen Daten (Soll-Bestand) aus der Datenbank, um einen Kassenabschluss vorzubereiten. */
	ABSCHLUSS_LADEN,

	/** Speichert den endgültigen Tagesabschluss (Ist-Bestand und Differenz) sicher ab. */
	ABSCHLUSS_SPEICHERN,

	/** Fordert eine Historie aller bisher getätigten Käufe und Kassenbons an. */
	KASSENZETTEL_LISTE,

	/** Listet alle Mitarbeiter auf. **/
	KASSIERER_LISTE,

	/** Fügt einen Kassierer der Mitarbeiterliste hinzu. */
	KASSIERER_ADD,

	/** Löscht einen Kassierer aus der Mitarbeiterliste. */
	KASSIERER_DELETE,

	/** Bearbeitet einen Kassierer in der Mitarbeiterliste. */
	KASSIERER_UPDATE,

	KAPITAL,

	KRITISCH
}