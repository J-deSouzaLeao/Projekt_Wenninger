package thw.edu.javaII.port.warehouse.model.deo;

import java.io.Serial;
import java.io.Serializable;

/**
 * Dieses Objekt dient als Standard-Anfrage (Datenpaket), die der Client an den Server schickt.
 * Es enthält alle nötigen Informationen für den Server: Welcher Bereich angesprochen wird (Zone),
 * was genau getan werden soll (Command) und welche Daten eventuell dafür nötig sind.
 * * @author juan.de.souza.leao
 */
public class WarehouseDEO implements Serializable {

	@Serial
	private static final long serialVersionUID = 4403645819092074274L;

	private Object data;
	private Zone zone;
	private Command command;

	/**
	 * Gibt die mitgesendeten Nutzdaten der Anfrage zurück.
	 * Das kann zum Beispiel ein neu angelegtes Produkt-Objekt sein, das der Server speichern soll.
	 * * @return Die angehängten Daten.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Fügt der Anfrage Nutzdaten hinzu.
	 * * @param data Die Daten, die an den Server übermittelt werden sollen.
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Gibt den logischen Bereich zurück, an den sich die Anfrage richtet.
	 * * @return Die Ziel-Zone (z. B. LAGER oder KASSE).
	 */
	public Zone getZone() {
		return zone;
	}

	/**
	 * Legt fest, für welchen Bereich des Systems diese Anfrage gedacht ist.
	 * * @param zone Die entsprechende Zone aus dem Enum.
	 */
	public void setZone(Zone zone) {
		this.zone = zone;
	}

	/**
	 * Gibt den auszuführenden Befehl zurück, den der Server abarbeiten soll.
	 * * @return Die gewünschte Aktion (z. B. Hinzufügen oder Löschen).
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * Setzt die gewünschte Aktion, die der Server mit dieser Anfrage ausführen soll.
	 * * @param command Der Befehl aus dem Command-Enum.
	 */
	public void setCommand(Command command) {
		this.command = command;
	}

	/**
	 * Standard-Konstruktor.
	 * Wird zwingend als leeres Grundgerüst für den Versand (Serialisierung) über das Netzwerk benötigt.
	 */
	public WarehouseDEO() {

	}

}