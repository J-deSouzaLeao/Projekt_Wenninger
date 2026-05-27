package thw.edu.javaII.port.warehouse.model;

import java.io.Serial;
import java.io.Serializable;

public class DemoModel implements Serializable {
	@Serial
    private static final long serialVersionUID = -474679583809542535L;
	private int id;
	private String name;

	public DemoModel() {
		
	}

	public DemoModel(int id, String name) {
		this.id=id;
		this.name = name;
	}

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

}
