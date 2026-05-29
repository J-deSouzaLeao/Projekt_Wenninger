package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;

/**
 * Diese Klasse repräsentiert einen Kassenabschluss (Tages- oder Schichtabschluss) im System.
 * Sie dokumentiert, welcher Kassierer zu welchem Zeitpunkt die Kasse abgerechnet hat
 * und gleicht das vom System erwartete Geld mit dem tatsächlich gezählten Geld ab.
 *
 * @author barbara.liegnitz
 */
public class Kassenabschluss implements Serializable {
    private int id;
    private String datum;
    private String uhrzeit;
    private Kassierer kassierer;
    private double sollBestand;
    private double istBestand;

    /**
     * Standard-Konstruktor.
     * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
     * über das Netzwerk zwischen Server und Client benötigt.
     */
    public Kassenabschluss() {}

    /**
     * Gibt die interne Identifikationsnummer (Belegnummer) dieses Kassenabschlusses zurück.
     *
     * @return Die ID des Abschlusses.
     */
    public int getId() { return id; }

    /**
     * Setzt die interne Datenbank-ID für diesen Kassenabschluss.
     *
     * @param id Die neue Belegnummer.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gibt das Datum zurück, an dem die Kasse abgerechnet wurde (z. B. "27.05.2026").
     *
     * @return Das Datum als Text.
     */
    public String getDatum() { return datum; }

    /**
     * Setzt das Datum für diesen Kassenabschluss.
     *
     * @param datum Das neue Datum.
     */
    public void setDatum(String datum) { this.datum = datum; }

    /**
     * Gibt die genaue Uhrzeit zurück, zu der der Abschluss durchgeführt wurde.
     *
     * @return Die Uhrzeit als Text.
     */
    public String getUhrzeit() { return uhrzeit; }

    /**
     * Setzt die Uhrzeit für den Kassenabschluss.
     *
     * @param uhrzeit Die neue Uhrzeit.
     */
    public void setUhrzeit(String uhrzeit) { this.uhrzeit = uhrzeit; }

    /**
     * Gibt den Kassierer (Mitarbeiter) zurück, der diesen Abschluss gemacht hat.
     *
     * @return Das Objekt des zuständigen Kassierers.
     */
    public Kassierer getKassierer() { return kassierer; }

    /**
     * Legt fest, welcher Kassierer die Kasse abgerechnet hat.
     *
     * @param kassierer Der zuständige Kassierer.
     */
    public void setKassierer(Kassierer kassierer) { this.kassierer = kassierer; }

    /**
     * Gibt den Soll-Bestand zurück.
     * Das ist der Betrag, der laut den Verkäufen im System in der Kasse liegen müsste.
     *
     * @return Der vom System errechnete Kassenbestand.
     */
    public double getSollBestand() { return sollBestand; }

    /**
     * Setzt den vom System berechneten Soll-Bestand.
     *
     * @param sollBestand Der erwartete Betrag.
     */
    public void setSollBestand(double sollBestand) { this.sollBestand = sollBestand; }

    /**
     * Gibt den Ist-Bestand zurück.
     * Das ist der Betrag, den der Kassierer beim Zählen der Scheine und Münzen tatsächlich vorliegen hat.
     *
     * @return Der manuell gezählte Kassenbestand.
     */
    public double getIstBestand() { return istBestand; }

    /**
     * Setzt den tatsächlich vom Kassierer gezählten Ist-Bestand.
     * Über die Differenz zum Soll-Bestand lassen sich dann mögliche Kassendifferenzen ermitteln.
     *
     * @param istBestand Der gezählte Betrag.
     */
    public void setIstBestand(double istBestand) { this.istBestand = istBestand; }
}