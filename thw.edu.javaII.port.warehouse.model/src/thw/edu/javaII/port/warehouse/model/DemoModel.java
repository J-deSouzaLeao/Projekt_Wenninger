package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Diese Klasse ist ein einfaches Demo-Modell für Testzwecke.
 * Sie wird hauptsächlich verwendet, um die grundsätzliche Netzwerkverbindung
 * oder einfache Datenbankabfragen zu überprüfen, bevor die komplexeren Klassen zum Einsatz kommen.
 * * @author juan.de.souza.leao
 */
public class DemoModel implements Serializable {

	@Serial
	private static final long serialVersionUID = -474679583809542535L;

	private int id;
	private String name;

	/**
	 * Standard-Konstruktor.
	 * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
	 * über das Netzwerk zwischen Server und Client benötigt.
	 */
	public DemoModel() {

	}

	/**
	 * Erstellt ein neues Demo-Objekt mit einer ID und einem Namen.
	 * Wird genutzt, um Testdaten (wie "Hallo" oder "Welt") schnell anzulegen.
	 * * @param id   Die eindeutige Test-ID.
	 * @param name Der Test-Text oder Name.
	 */
	public DemoModel(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Gibt die interne Identifikationsnummer des Demo-Objekts zurück.
	 * * @return Die ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt die Identifikationsnummer für dieses Demo-Objekt.
	 * * @param id Die neue ID.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gibt den Namen oder den Text dieses Demo-Objekts zurück.
	 * * @return Der Name als Text.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Legt den Namen oder den Text für dieses Demo-Objekt fest.
	 * * @param name Der neue Name.
	 */
	public void setName(String name) {
		this.name = name;
	}

}