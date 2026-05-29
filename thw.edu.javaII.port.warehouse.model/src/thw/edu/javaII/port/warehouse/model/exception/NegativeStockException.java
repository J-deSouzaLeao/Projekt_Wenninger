package thw.edu.javaII.port.warehouse.model.exception;

/**
 * Diese spezielle Ausnahme (Exception) wird ausgelöst, wenn ein Vorgang dazu führen würde,
 * dass der Bestand eines Produkts im Lager unter Null fällt.
 * Das passiert vor allem, wenn an der Kasse mehr Artikel verkauft werden sollen,
 * als im System physisch noch vorhanden sind.
 *
 * @author barbara.liegnitz
 */
public class NegativeStockException extends Exception {

    /**
     * Erstellt eine neue Ausnahme mit einer genauen Fehlerbeschreibung.
     * Diese Nachricht kann später auf der Benutzeroberfläche angezeigt werden,
     * um dem Kassierer zu erklären, warum der Kauf abgebrochen wurde.
     *
     * @param message Die Nachricht, die den Fehler genauer beschreibt (z. B. welches Produkt fehlt).
     */
    public NegativeStockException(String message) {
        super(message);
    }
}