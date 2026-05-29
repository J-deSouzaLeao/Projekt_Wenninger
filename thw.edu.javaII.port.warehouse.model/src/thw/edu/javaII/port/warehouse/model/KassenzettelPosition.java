package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;

/**
 * Diese Klasse repräsentiert eine einzelne Position (Zeile) auf einem Kassenzettel.
 * Sie speichert, welches Produkt gekauft wurde, in welcher Menge es auf dem Laufband lag
 * und wie hoch der errechnete Gesamtpreis für genau diese Position ist.
 *
 * @author barbara.liegnitz
 */
public class KassenzettelPosition implements Serializable {
    private int id;
    private Produkt produkt;
    private int anzahl;
    private double gesamtpreis;

    /**
     * Standard-Konstruktor.
     * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
     * über das Netzwerk zwischen Server und Client benötigt.
     */
    public KassenzettelPosition() {}

    /**
     * Gibt die interne Identifikationsnummer dieser Kassenzettel-Position zurück.
     *
     * @return Die ID der Position.
     */
    public int getId() { return id; }

    /**
     * Setzt die interne Datenbank-ID für diese Position.
     *
     * @param id Die neue ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gibt das Produkt zurück, das in dieser Zeile des Kassenzettels erfasst wurde.
     *
     * @return Das gekaufte Produkt.
     */
    public Produkt getProdukt() { return produkt; }

    /**
     * Legt das gekaufte Produkt für diese Bon-Position fest.
     *
     * @param produkt Das zugehörige Produkt.
     */
    public void setProdukt(Produkt produkt) { this.produkt = produkt; }

    /**
     * Gibt die Menge zurück, wie oft das Produkt in dieser Zeile gekauft wurde.
     *
     * @return Die gekaufte Stückzahl.
     */
    public int getAnzahl() { return anzahl; }

    /**
     * Setzt die gekaufte Menge des Produkts (z. B. wenn der Kassierer einen Artikel mehrfach scannt).
     *
     * @param anzahl Die neue Stückzahl.
     */
    public void setAnzahl(int anzahl) { this.anzahl = anzahl; }

    /**
     * Gibt den berechneten Gesamtpreis für diese Position zurück.
     * (Normalerweise entspricht das: Einzelpreis des Produkts * Anzahl).
     *
     * @return Der Gesamtpreis dieser Zeile.
     */
    public double getGesamtpreis() { return gesamtpreis; }

    /**
     * Setzt den Gesamtpreis für diese spezifische Position auf dem Kassenzettel.
     *
     * @param gesamtpreis Der neue Preis.
     */
    public void setGesamtpreis(double gesamtpreis) { this.gesamtpreis = gesamtpreis; }
}