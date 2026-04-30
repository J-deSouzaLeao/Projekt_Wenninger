package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;

public class Kassenabschluss implements Serializable {
    private int id;
    private String datum;
    private String uhrzeit;
    private Kassierer kassierer;
    private double sollBestand;
    private double istBestand;

    public Kassenabschluss() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDatum() { return datum; }
    public void setDatum(String datum) { this.datum = datum; }
    public String getUhrzeit() { return uhrzeit; }
    public void setUhrzeit(String uhrzeit) { this.uhrzeit = uhrzeit; }
    public Kassierer getKassierer() { return kassierer; }
    public void setKassierer(Kassierer kassierer) { this.kassierer = kassierer; }
    public double getSollBestand() { return sollBestand; }
    public void setSollBestand(double sollBestand) { this.sollBestand = sollBestand; }
    public double getIstBestand() { return istBestand; }
    public void setIstBestand(double istBestand) { this.istBestand = istBestand; }
}