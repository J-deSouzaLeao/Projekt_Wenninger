package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;

public class KassenzettelPosition implements Serializable {
    private int id;
    private Produkt produkt;
    private int anzahl;
    private double gesamtpreis;

    public KassenzettelPosition() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Produkt getProdukt() { return produkt; }
    public void setProdukt(Produkt produkt) { this.produkt = produkt; }
    public int getAnzahl() { return anzahl; }
    public void setAnzahl(int anzahl) { this.anzahl = anzahl; }
    public double getGesamtpreis() { return gesamtpreis; }
    public void setGesamtpreis(double gesamtpreis) { this.gesamtpreis = gesamtpreis; }
}