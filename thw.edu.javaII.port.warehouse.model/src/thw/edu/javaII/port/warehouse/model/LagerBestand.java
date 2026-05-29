package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Diese Klasse repräsentiert den tatsächlichen Lagerbestand in unserem System.
 * Sie fungiert als Bindeglied und verknüpft ein bestimmtes Produkt mit einem
 * konkreten Lagerplatz und speichert die genaue Stückzahl, die dort aktuell liegt.
 *
 * @author juan.de.souza.leao
 */
public class LagerBestand implements Serializable {

	@Serial
	private static final long serialVersionUID = 5335970888396140828L;

	private int id;
	private int anzahl;
	private Produkt produkt_id;
	private LagerPlatz lagerplatz_id;

	/**
	 * Gibt die eindeutige Identifikationsnummer dieses Bestands-Eintrags zurück.
	 *
	 * @return Die ID des Bestands.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt eine neue Identifikationsnummer für diesen Bestands-Eintrag.
	 *
	 * @param id Die neue ID.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gibt die aktuell gelagerte Menge (Stückzahl) dieses Produkts auf diesem Platz zurück.
	 *
	 * @return Die aktuelle Anzahl.
	 */
	public int getAnzahl() {
		return anzahl;
	}

	/**
	 * Aktualisiert die gelagerte Menge.
	 * Wird zum Beispiel aufgerufen, wenn Ware an der Kasse verkauft oder neu eingelagert wird.
	 *
	 * @param anzahl Die neue Stückzahl.
	 */
	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	/**
	 * Gibt das Produkt-Objekt zurück, das in diesem Bestand gelagert wird.
	 *
	 * @return Das zugehörige Produkt.
	 */
	public Produkt getProdukt_id() {
		return produkt_id;
	}

	/**
	 * Weist diesem Bestandseintrag ein Produkt zu.
	 *
	 * @param produkt_id Das Produkt, das hier gelagert werden soll.
	 */
	public void setProdukt_id(Produkt produkt_id) {
		this.produkt_id = produkt_id;
	}

	/**
	 * Gibt den Lagerplatz zurück, an dem dieses Produkt aktuell liegt.
	 *
	 * @return Der zugehörige Lagerplatz.
	 */
	public LagerPlatz getLagerplatz_id() {
		return lagerplatz_id;
	}

	/**
	 * Standard-Konstruktor.
	 * Dient als leeres Grundgerüst und wird zwingend für die automatische Datenübertragung
	 * (Serialisierung) über das Netzwerk benötigt.
	 */
	public LagerBestand() {

	}

	/**
	 * Erstellt einen komplett neuen Bestands-Eintrag.
	 * Wird genutzt, wenn ein Produkt frisch auf einen bestimmten Lagerplatz gebucht wird.
	 *
	 * @param id            Die eindeutige Nummer des Eintrags.
	 * @param anzahl        Die gelagerte Stückzahl.
	 * @param produkt_id    Das Produkt, um das es geht.
	 * @param lagerplatz_id Der Ort, an dem das Produkt liegt.
	 */
	public LagerBestand(int id, int anzahl, Produkt produkt_id, LagerPlatz lagerplatz_id) {
		super();
		this.id = id;
		this.anzahl = anzahl;
		this.produkt_id = produkt_id;
		this.lagerplatz_id = lagerplatz_id;
	}

}