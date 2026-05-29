package thw.edu.javaII.port.warehouse.model.deo;

/**
 * Dieses Enum definiert die möglichen Zustände einer Serverantwort.
 * Es wird genutzt, um dem Client schnell und eindeutig mitzuteilen,
 * ob seine Anfrage erfolgreich war oder ob ein Problem aufgetreten ist.
 *
 * @author juan.de.souza.leao
 */
public enum Status {

	/**
	 * Signalisiert, dass die Anfrage vom Server erfolgreich und ohne Fehler verarbeitet wurde.
	 */
	OK,

	/**
	 * Steht für eine allgemeine Information oder einen Hinweis vom Server,
	 * ohne dass ein direkter Fehler vorliegt.
	 */
	INFO,

	/**
	 * Zeigt an, dass bei der Verarbeitung der Anfrage ein Fehler aufgetreten ist
	 * (z. B. fehlende Berechtigung, Absturz oder Datenbankfehler).
	 */
	ERROR
}