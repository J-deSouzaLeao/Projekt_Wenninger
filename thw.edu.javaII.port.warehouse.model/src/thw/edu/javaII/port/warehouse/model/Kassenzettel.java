package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Kassenzettel implements Serializable {
    private int id;
    private String datum;
    private String uhrzeit;
    private String zahlart;
    private Kassierer kassierer;
    private double gesamtpreis;
    private final List<KassenzettelPosition> positionen = new ArrayList<>();

    public Kassenzettel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDatum() { return datum; }
    public void setDatum(String datum) { this.datum = datum; }
    public String getUhrzeit() { return uhrzeit; }
    public void setUhrzeit(String uhrzeit) { this.uhrzeit = uhrzeit; }
    public String getZahlart() { return zahlart; }
    public void setZahlart(String zahlart) { this.zahlart = zahlart; }
    public Kassierer getKassierer() { return kassierer; }
    public void setKassierer(Kassierer kassierer) { this.kassierer = kassierer; }
    public double getGesamtpreis() { return gesamtpreis; }
    public void setGesamtpreis(double gesamtpreis) { this.gesamtpreis = gesamtpreis; }
    public List<KassenzettelPosition> getPositionen() { return positionen; }
}