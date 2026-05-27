package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

public class Produkt implements Serializable {
	@Serial
    private static final long serialVersionUID = -5308799831441367738L;
	private int id;
	private String name;
	private String hersteller;
	private double preis;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHersteller() {
		return hersteller;
	}

	public void setHersteller(String hersteller) {
		this.hersteller = hersteller;
	}

	public double getPreis() {
		return preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

	public Produkt() {

	}

	public Produkt(int id, String name, String hersteller, double preis) {
		super();
		this.id = id;
		this.name = name;
		this.hersteller = hersteller;
		this.preis = preis;
	}

}
