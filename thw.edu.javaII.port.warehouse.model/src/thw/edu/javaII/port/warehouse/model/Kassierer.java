package thw.edu.javaII.port.warehouse.model;

import java.io.Serializable;

public class Kassierer implements Serializable {
    private int id;
    private int nummer;
    private String pin;
    private String name;

    public Kassierer() {}

    public Kassierer(int id, int nummer, String pin, String name) {
        this.id = id;
        this.nummer = nummer;
        this.pin = pin;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNummer() { return nummer; }
    public void setNummer(int nummer) { this.nummer = nummer; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}