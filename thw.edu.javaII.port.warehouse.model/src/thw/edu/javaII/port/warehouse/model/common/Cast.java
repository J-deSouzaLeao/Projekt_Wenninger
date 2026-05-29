package thw.edu.javaII.port.warehouse.model.common;

import java.util.List;

/**
 * Diese Hilfsklasse stellt Methoden zur Verfügung, um Objekte sicher in andere Datentypen umzuwandeln (Casting).
 * Dadurch werden Programmabstürze (ClassCastExceptions) vermieden, falls beispielsweise
 * Daten vom Server in einem unerwarteten Format eintreffen.
 *
 * @author juan.de.souza.leao
 */
public class Cast {

	/**
	 * Wandelt ein einzelnes Objekt sicher in den gewünschten Ziel-Datentyp um.
	 * Bevor umgewandelt wird, prüft die Methode, ob das Objekt überhaupt zu dieser Klasse gehört.
	 *
	 * @param <T>   Der generische Typ der Zielklasse.
	 * @param o     Das Objekt, das umgewandelt werden soll.
	 * @param clazz Die Klasse (der Datentyp), in die umgewandelt werden soll.
	 * @return Das erfolgreich umgewandelte Objekt oder null, falls die Umwandlung fehlschlägt.
	 */
	public static <T> T safeCast(Object o, Class<T> clazz) {
		return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
	}

	/**
	 * Wandelt ein Objekt sicher in eine Liste mit einem bestimmten Element-Datentyp um.
	 * Um Fehler zu vermeiden, wird überprüft, ob das Objekt eine Liste ist und
	 * ob das erste Element in der Liste dem gewünschten Datentyp entspricht.
	 *
	 * @param <T>   Der generische Typ der Listenelemente.
	 * @param o     Das Objekt (im Idealfall eine Liste), das umgewandelt werden soll.
	 * @param clazz Die Klasse (der Datentyp), den die Elemente in der Liste haben sollen.
	 * @return Die korrekt formatierte Liste oder null, falls die Liste leer ist oder der Datentyp nicht passt.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> safeListCast(Object o, Class<T> clazz) {
		if (clazz != null) {
			if (o instanceof List<?> list) {
				if (!list.isEmpty()) {
					if (list.get(0).getClass().equals(clazz)) {
						return (List<T>) o;
					}
				}
			}
		}
		return null;
	}
}