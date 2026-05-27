package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

public class LagerPlatz implements Serializable {
	@Serial
    private static final long serialVersionUID = -208247320517732519L;
	private int id;
	private String name;
	private int kapazitaet;
	private Lager lager_id;

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

	public int getKapazitaet() {
		return kapazitaet;
	}

	public void setKapazitaet(int kapazitaet) {
		this.kapazitaet = kapazitaet;
	}

	public Lager getLager_id() {
		return lager_id;
	}

	public void setLager_id(Lager lager_id) {
		this.lager_id = lager_id;
	}

	public LagerPlatz() {

	}

	public LagerPlatz(int id, String name, int kapazitaet, Lager lager_id) {
		super();
		this.id = id;
		this.name = name;
		this.kapazitaet = kapazitaet;
		this.lager_id = lager_id;
	}

	public String toString() {
		return getName() + " - " + getLager_id().getName();
	}

}
