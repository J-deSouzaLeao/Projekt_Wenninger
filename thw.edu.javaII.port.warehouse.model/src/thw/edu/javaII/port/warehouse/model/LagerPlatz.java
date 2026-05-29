package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Diese Klasse repräsentiert einen konkreten Lagerplatz (z. B. ein Regal oder eine Zone)
 * innerhalb eines übergeordneten Lagers. Sie definiert, wo genau Waren abgelegt werden können
 * und wie viel Platz dort maximal zur Verfügung steht.
 *
 * @author barbara.liegnitz
 */
public class LagerPlatz implements Serializable {

	@Serial
	private static final long serialVersionUID = -208247320517732519L;

	private int id;
	private String name;
	private int kapazitaet;
	private Lager lager_id;

	/**
	 * Gibt die eindeutige Identifikationsnummer des Lagerplatzes zurück.
	 *
	 * @return Die ID des Lagerplatzes.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt eine neue Identifikationsnummer für den Lagerplatz.
	 *
	 * @param id Die neue ID.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gibt die genaue Bezeichnung des Lagerplatzes zurück (z. B. "Regal A1" oder "Zone 3").
	 *
	 * @return Der Name des Lagerplatzes.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt die Bezeichnung für diesen Lagerplatz.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt die maximale Kapazität dieses Lagerplatzes zurück.
	 * Damit lässt sich prüfen, wie viele Artikel maximal hier eingelagert werden dürfen.
	 *
	 * @return Die Kapazitätsgrenze als ganze Zahl.
	 */
	public int getKapazitaet() {
		return kapazitaet;
	}

	/**
	 * Legt die maximale Kapazität für diesen Lagerplatz fest.
	 *
	 * @param kapazitaet Die neue maximale Stückzahl oder Menge.
	 */
	public void setKapazitaet(int kapazitaet) {
		this.kapazitaet = kapazitaet;
	}

	/**
	 * Gibt das übergeordnete Lager-Objekt zurück, in dem sich dieser Lagerplatz physisch befindet.
	 *
	 * @return Das dazugehörige Lager.
	 */
	public Lager getLager_id() {
		return lager_id;
	}

	/**
	 * Weist diesen Lagerplatz einem bestimmten Hauptlager zu.
	 *
	 * @param lager_id Das Lager, in dem sich der Platz befindet.
	 */
	public void setLager_id(Lager lager_id) {
		this.lager_id = lager_id;
	}

	/**
	 * Standard-Konstruktor.
	 * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
	 * über das Netzwerk zwischen Server und Client benötigt.
	 */
	public LagerPlatz() {

	}

	/**
	 * Erstellt einen neuen, vollständig konfigurierten Lagerplatz.
	 *
	 * @param id         Die eindeutige Nummer des Lagerplatzes.
	 * @param name       Die Bezeichnung (z. B. "Regal B2").
	 * @param kapazitaet Die maximale Aufnahmekapazität.
	 * @param lager_id   Das übergeordnete Lager, zu dem dieser Platz gehört.
	 */
	public LagerPlatz(int id, String name, int kapazitaet, Lager lager_id) {
		super();
		this.id = id;
		this.name = name;
		this.kapazitaet = kapazitaet;
		this.lager_id = lager_id;
	}

	/**
	 * Überschreibt die Standard-Textausgabe des Objekts.
	 * Sorgt dafür, dass in der Benutzeroberfläche (z. B. in Dropdown-Menüs)
	 * eine gut lesbare Kombination aus Lagerplatz und dem Hauptlager angezeigt wird.
	 *
	 * @return Ein formatierter String im Stil "Platzname - Lagername".
	 */
	@Override
	public String toString() {
		return getName() + " - " + getLager_id().getName();
	}

}