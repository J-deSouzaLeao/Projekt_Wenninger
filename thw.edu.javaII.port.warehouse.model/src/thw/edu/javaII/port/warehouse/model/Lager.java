package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

public class Lager implements Serializable{
	@Serial
    private static final long serialVersionUID = 8230932661185246836L;
	private int id;
	private String name;
	private String ort;
	private String art;

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

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getArt() {
		return art;
	}

	public void setArt(String art) {
		this.art = art;
	}
	
	public Lager() {
		
	}

	public Lager(int id, String name, String ort, String art) {
		super();
		this.id = id;
		this.name = name;
		this.ort = ort;
		this.art = art;
	}

}
