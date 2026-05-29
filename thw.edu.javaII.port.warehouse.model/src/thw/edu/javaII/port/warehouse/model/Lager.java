package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Diese Klasse repräsentiert ein physisches Lager oder einen Standort im System.
 * Ein Lager ist die oberste organisatorische Einheit (z. B. eine Halle oder ein Freigelände),
 * in der sich wiederum verschiedene einzelne Lagerplätze befinden können.
 *
 * @author barbara.liegnitz
 */
public class Lager implements Serializable {

	@Serial
	private static final long serialVersionUID = 8230932661185246836L;

	private int id;
	private String name;
	private String ort;
	private String art;

	/**
	 * Gibt die eindeutige Identifikationsnummer des Lagers zurück.
	 *
	 * @return Die ID des Lagers.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt eine neue Identifikationsnummer für dieses Lager.
	 *
	 * @param id Die neue ID.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gibt den Namen oder die Bezeichnung des Lagers zurück (z. B. "Hauptlager").
	 *
	 * @return Der Name des Lagers.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Legt den Namen für das Lager fest.
	 *
	 * @param name Der neue Name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt den geografischen Standort des Lagers zurück (z. B. die Stadt).
	 *
	 * @return Der Ort des Lagers.
	 */
	public String getOrt() {
		return ort;
	}

	/**
	 * Setzt den Standort für dieses Lager.
	 *
	 * @param ort Der neue Ort (z. B. "Stuttgart").
	 */
	public void setOrt(String ort) {
		this.ort = ort;
	}

	/**
	 * Gibt die Art des Lagers zurück.
	 * Das beschreibt die bauliche Beschaffenheit, z. B. "Hochregallager" oder "Kühlhaus".
	 *
	 * @return Die Lagerart.
	 */
	public String getArt() {
		return art;
	}

	/**
	 * Legt die Art des Lagers fest.
	 *
	 * @param art Die neue Lagerart.
	 */
	public void setArt(String art) {
		this.art = art;
	}

	/**
	 * Standard-Konstruktor.
	 * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
	 * über das Netzwerk zwischen Server und Client benötigt.
	 */
	public Lager() {

	}

	/**
	 * Erstellt ein neues, vollständig konfiguriertes Lager.
	 * Wird genutzt, wenn ein komplett neuer Standort in das System aufgenommen wird.
	 *
	 * @param id   Die eindeutige Nummer des Lagers.
	 * @param name Die Bezeichnung des Lagers.
	 * @param ort  Die Stadt oder der Standort.
	 * @param art  Die bauliche Art (z. B. "Halle").
	 */
	public Lager(int id, String name, String ort, String art) {
		super();
		this.id = id;
		this.name = name;
		this.ort = ort;
		this.art = art;
	}

}