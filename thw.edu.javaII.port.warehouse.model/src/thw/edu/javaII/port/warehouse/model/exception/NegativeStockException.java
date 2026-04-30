package thw.edu.javaII.port.warehouse.model.exception;

public class NegativeStockException extends Exception {
    public NegativeStockException(String message) {
        super(message);
    }
}