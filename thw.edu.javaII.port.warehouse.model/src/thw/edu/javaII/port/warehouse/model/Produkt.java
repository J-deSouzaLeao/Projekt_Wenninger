package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Diese Klasse repräsentiert ein einzelnes Produkt im System.
 * Sie speichert alle wichtigen Stammdaten wie die Artikelnummer, den Namen,
 * den Hersteller und den Preis, die für den Verkauf und die Lagerverwaltung benötigt werden.
 * * @author juan.de.souza.leao
 */
public class Produkt implements Serializable {

	@Serial
	private static final long serialVersionUID = -5308799831441367738L;

	private int id;
	private String name;
	private String hersteller;
	private double preis;

	/**
	 * Gibt die eindeutige Identifikationsnummer (Artikelnummer) des Produkts zurück.
	 * * @return Die ID des Produkts.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setzt eine neue Identifikationsnummer für das Produkt.
	 * * @param id Die neue Artikelnummer.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gibt den Namen oder die genaue Bezeichnung des Produkts zurück.
	 * * @return Der Produktname.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen oder die Bezeichnung des Produkts.
	 * * @param name Der neue Produktname.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt den Hersteller oder die Marke des Produkts zurück.
	 * * @return Der Name des Herstellers.
	 */
	public String getHersteller() {
		return hersteller;
	}

	/**
	 * Setzt den Hersteller oder die Marke des Produkts.
	 * * @param hersteller Der neue Hersteller.
	 */
	public void setHersteller(String hersteller) {
		this.hersteller = hersteller;
	}

	/**
	 * Gibt den aktuellen Verkaufspreis des Produkts zurück.
	 * * @return Der Preis (als Dezimalzahl).
	 */
	public double getPreis() {
		return preis;
	}

	/**
	 * Legt den Verkaufspreis für das Produkt fest.
	 * * @param preis Der neue Preis.
	 */
	public void setPreis(double preis) {
		this.preis = preis;
	}

	/**
	 * Standard-Konstruktor.
	 * Wird zwingend als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
	 * über das Netzwerk zwischen Server und Client benötigt.
	 */
	public Produkt() {

	}

	/**
	 * Erstellt ein neues, vollständig ausgefülltes Produkt.
	 * Wird genutzt, wenn ein komplett neuer Artikel im System oder in der Datenbank angelegt wird.
	 * * @param id         Die eindeutige Artikelnummer.
	 * @param name       Die Bezeichnung des Produkts.
	 * @param hersteller Der Hersteller des Produkts.
	 * @param preis      Der Verkaufspreis des Produkts.
	 */
	public Produkt(int id, String name, String hersteller, double preis) {
		super();
		this.id = id;
		this.name = name;
		this.hersteller = hersteller;
		this.preis = preis;
	}

}