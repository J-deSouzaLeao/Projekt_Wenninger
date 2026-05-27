package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repräsentiert einen vollständigen Kassenzettel (Bon),
 * der nach einem erfolgreichen Einkauf an der Kasse erstellt und gespeichert wird.
 * Sie bündelt alle Rahmeninformationen (wie Datum und Kassierer) und hält eine Liste
 * aller gekauften Artikel (die Positionen).
 * * @author juan.de.souza.leao
 */
public class Kassenzettel implements Serializable {
    private int id;
    private String datum;
    private String uhrzeit;
    private String zahlart;
    private Kassierer kassierer;
    private double gesamtpreis;
    private final List<KassenzettelPosition> positionen = new ArrayList<>();

    /**
     * Standard-Konstruktor.
     * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
     * über das Netzwerk zwischen Server und Client benötigt.
     */
    public Kassenzettel() {}

    /**
     * Gibt die eindeutige Belegnummer (ID) dieses Kassenzettels zurück.
     * * @return Die ID des Bons.
     */
    public int getId() { return id; }

    /**
     * Setzt die interne Belegnummer für diesen Kassenzettel.
     * * @param id Die neue Bon-ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gibt das Datum zurück, an dem der Einkauf getätigt wurde (z. B. "27.05.2026").
     * * @return Das Einkaufsdatum als Text.
     */
    public String getDatum() { return datum; }

    /**
     * Setzt das Datum für diesen Kassenzettel.
     * * @param datum Das neue Datum.
     */
    public void setDatum(String datum) { this.datum = datum; }

    /**
     * Gibt die genaue Uhrzeit zurück, zu der der Kassenbon gedruckt wurde.
     * * @return Die Uhrzeit als Text.
     */
    public String getUhrzeit() { return uhrzeit; }

    /**
     * Setzt die Uhrzeit für den Einkauf.
     * * @param uhrzeit Die neue Uhrzeit.
     */
    public void setUhrzeit(String uhrzeit) { this.uhrzeit = uhrzeit; }

    /**
     * Gibt an, wie der Kunde an der Kasse bezahlt hat (z. B. "Bar" oder "Karte").
     * * @return Die gewählte Zahlungsart.
     */
    public String getZahlart() { return zahlart; }

    /**
     * Legt die verwendete Zahlungsmethode für diesen Einkauf fest.
     * * @param zahlart Die neue Zahlungsart.
     */
    public void setZahlart(String zahlart) { this.zahlart = zahlart; }

    /**
     * Gibt den Mitarbeiter (Kassierer) zurück, der diesen Einkauf abgewickelt hat.
     * * @return Das Objekt des zuständigen Kassierers.
     */
    public Kassierer getKassierer() { return kassierer; }

    /**
     * Weist diesen Kassenzettel einem bestimmten Kassierer zu.
     * * @param kassierer Der Mitarbeiter an der Kasse.
     */
    public void setKassierer(Kassierer kassierer) { this.kassierer = kassierer; }

    /**
     * Gibt den finalen Gesamtbetrag des gesamten Einkaufs (alle Positionen zusammen) zurück.
     * * @return Der Gesamtpreis des Bons.
     */
    public double getGesamtpreis() { return gesamtpreis; }

    /**
     * Setzt den berechneten Gesamtpreis für diesen Kassenzettel.
     * * @param gesamtpreis Der neue Gesamtbetrag.
     */
    public void setGesamtpreis(double gesamtpreis) { this.gesamtpreis = gesamtpreis; }

    /**
     * Gibt die Liste aller gekauften Artikel (die einzelnen Zeilen auf dem Bon) zurück.
     * Über diese Liste können auch neue Artikel zum Bon hinzugefügt werden.
     * * @return Die Liste der Kassenzettel-Positionen.
     */
    public List<KassenzettelPosition> getPositionen() { return positionen; }
}