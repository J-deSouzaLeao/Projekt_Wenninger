package thw.edu.javaII.port.warehouse.model.deo;

import java.io.Serial;
import java.io.Serializable;

public class WarehouseReturnDEO implements Serializable{
	@Serial
    private static final long serialVersionUID = 5607081847323905913L;
	private Object data;
	private String message;
	private Status status;

	public Object getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}

	public Status getStatus() {
		return status;
	}

	public WarehouseReturnDEO(Object data, String message, Status status) {
		super();
		this.data = data;
		this.message = message;
		this.status = status;
	}

	public WarehouseReturnDEO() {

	}

}
