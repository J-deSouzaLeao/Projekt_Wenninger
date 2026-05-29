package thw.edu.javaII.port.warehouse.server.comparator;

import java.util.Comparator;

import thw.edu.javaII.port.warehouse.model.LagerBestand;

/**
 * Diese Klasse dient als Sortierhilfe (Comparator) für Lagerbestände.
 * Sie wird genutzt, um eine Liste von Beständen alphabetisch (A-Z)
 * nach dem Namen der darin gelagerten Produkte zu sortieren.
 *
 * @author barbara.liegnitz
 */
public class BestandByProduktAlpha implements Comparator<LagerBestand> {

	/**
	 * Vergleicht zwei Lagerbestände anhand ihrer Produktnamen.
	 * Damit weiß die Liste, in welcher Reihenfolge die Einträge angezeigt werden sollen.
	 *
	 * @param a Der erste Lagerbestand für den Vergleich.
	 * @param b Der zweite Lagerbestand für den Vergleich.
	 * @return Ein negativer Wert, wenn das erste Produkt alphabetisch vor dem zweiten steht,
	 * 0 bei Gleichheit, oder ein positiver Wert, wenn es danach kommt.
	 */
	@Override
	public int compare(LagerBestand a, LagerBestand b) {
		return a.getProdukt_id().getName().compareTo(b.getProdukt_id().getName());
	}
}