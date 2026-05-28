package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;

/**
 * Diese Klasse repräsentiert einen Kassierer im System.
 * Sie speichert alle wichtigen Daten, die für den Login am Touch-Kassenterminal
 * und für die Autorisierung von Stornos benötigt werden.
 * * @author juan.de.souza.leao
 */
public class Kassierer implements Serializable {
    private int id;
    private int nummer;
    private String pin;
    private String name;

    /**
     * Standard-Konstruktor.
     * Wird als leeres Grundgerüst für die automatische Datenübertragung (Serialisierung)
     * über das Netzwerk zwischen Server und Client benötigt.
     */
    public Kassierer() {}

    /**
     * Erstellt einen neuen, vollständig ausgefüllten Kassierer.
     * Wird genutzt, wenn ein neuer Mitarbeiter in der Datenbank angelegt wird.
     * * @param id     Die interne Datenbank-ID des Kassierers.
     * @param nummer Die Personalnummer, mit der sich der Kassierer an der Kasse anmeldet.
     * @param pin    Die geheime PIN für den Login und für Freigaben.
     * @param name   Der Vor- und Nachname des Kassierers (z. B. für den Kassenbon).
     */
    public Kassierer(int id, int nummer, String pin, String name) {
        this.id = id;
        this.nummer = nummer;
        this.pin = pin;
        this.name = name;
    }

    /**
     * Gibt die interne Identifikationsnummer (Datenbank-ID) zurück.
     * * @return Die ID des Kassierers.
     */
    public int getId() { return id; }

    /**
     * Setzt die interne Datenbank-ID.
     * * @param id Die neue ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gibt die Kassierernummer (Personalnummer) zurück.
     * Das ist die Nummer, die der Mitarbeiter auf dem Numpad der Kasse eintippen muss.
     * * @return Die Login-Nummer.
     */
    public int getNummer() { return nummer; }

    /**
     * Setzt die Kassierernummer für den Login.
     * * @param nummer Die neue Login-Nummer.
     */
    public void setNummer(int nummer) { this.nummer = nummer; }

    /**
     * Gibt die geheime PIN des Kassierers zurück.
     * * @return Die PIN als Text.
     */
    public String getPin() { return pin; }

    /**
     * Setzt eine neue PIN für diesen Kassierer.
     * * @param pin Die neue PIN.
     */
    public void setPin(String pin) { this.pin = pin; }

    /**
     * Gibt den Namen des Kassierers zurück.
     * * @return Der Name (z. B. "Max Muster").
     */
    public String getName() { return name; }

    /**
     * Legt den Namen für den Kassierer fest.
     * * @param name Der neue Name.
     */
    public void setName(String name) { this.name = name; }

    /** * Temporärer Speicher für den Startbestand (Wechselgeld) bei Schichtbeginn.
     * Wird beim Login an den Server übertragen.
     */
    private double startBestand;

    public double getStartBestand() {
        return startBestand;
    }

    public void setStartBestand(double startBestand) {
        this.startBestand = startBestand;
    }

    /**
     * Legt fest, ob dieser Kassierer administrative Rechte (Manager) besitzt.
     */
    private boolean isManager;

    public boolean isManager() { return isManager; }
    public void setManager(boolean manager) { this.isManager = manager; }
}