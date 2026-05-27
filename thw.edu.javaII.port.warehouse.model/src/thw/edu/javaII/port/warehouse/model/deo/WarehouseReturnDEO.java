package thw.edu.javaII.port.warehouse.model.deo;

import java.io.Serial;
import java.io.Serializable;

/**
 * Dieses Objekt dient als Standard-Antwort des Servers.
 * Immer wenn der Client eine Anfrage an den Server stellt, schickt der Server
 * ein Objekt dieser Klasse zurück. Es bündelt die eigentlichen Daten,
 * einen Erfolgsstatus und eine optionale Textnachricht (z. B. bei Fehlern) für den Netzwerk-Transport.
 * * @author juan.de.souza.leao
 */
public class WarehouseReturnDEO implements Serializable {

	@Serial
	private static final long serialVersionUID = 5607081847323905913L;

	private Object data;
	private String message;
	private Status status;

	/**
	 * Gibt die vom Server gelieferten Nutzdaten zurück.
	 * Das kann beispielsweise eine Liste von Produkten oder ein Kassierer-Objekt sein.
	 * * @return Die angefragten Daten (müssen vom Client noch passend gecastet werden).
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Gibt die Begleitnachricht des Servers zurück.
	 * Wird meistens genutzt, um dem Benutzer im Fehlerfall den genauen Grund anzuzeigen.
	 * * @return Die Nachricht als Text.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gibt den Status der Serverantwort zurück.
	 * Daran erkennt der Client sofort, ob die Operation erfolgreich war oder abgebrochen wurde.
	 * * @return Der Status (z. B. OK oder ERROR).
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Erstellt ein neues Antwort-Objekt mit allen benötigten Informationen.
	 * Wird vom Server aufgerufen, kurz bevor die Antwort an den Client gesendet wird.
	 * * @param data Die Nutzdaten, die zurückgesendet werden sollen.
	 * @param message Eine erklärende Nachricht oder Fehlermeldung.
	 * @param status Der generelle Erfolgsstatus der Aktion.
	 */
	public WarehouseReturnDEO(Object data, String message, Status status) {
		this.data = data;
		this.message = message;
		this.status = status;
	}

	/**
	 * Standard-Konstruktor.
	 * Wird als leeres Grundgerüst für die automatische Serialisierung über das Netzwerk benötigt.
	 */
	public WarehouseReturnDEO() {
	}

}