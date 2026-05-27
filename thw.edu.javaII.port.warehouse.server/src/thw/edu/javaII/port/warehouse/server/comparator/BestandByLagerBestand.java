package thw.edu.javaII.port.warehouse.server.comparator;

import java.util.Comparator;

import thw.edu.javaII.port.warehouse.model.LagerBestand;

/**
 * Diese Klasse dient als Sortierhilfe (Comparator) für Lagerbestände.
 * Sie wird genutzt, um eine Liste von Beständen aufsteigend nach ihrer
 * aktuellen Menge (Stückzahl) zu sortieren – also vom kleinsten zum größten Bestand.
 * * @author juan.de.souza.leao
 */
public class BestandByLagerBestand implements Comparator<LagerBestand> {

	/**
	 * Vergleicht zwei Lagerbestände anhand ihrer gelagerten Stückzahl.
	 * Damit weiß die Liste, in welcher Reihenfolge die Einträge angezeigt werden sollen.
	 * * @param a Der erste Lagerbestand für den Vergleich.
	 * @param b Der zweite Lagerbestand für den Vergleich.
	 * @return Ein negativer Wert, wenn im ersten Bestand weniger liegt als im zweiten,
	 * 0 bei exakt gleicher Menge, oder ein positiver Wert, wenn im ersten mehr liegt.
	 */
	@Override
	public int compare(LagerBestand a, LagerBestand b) {
		return a.getAnzahl() - b.getAnzahl();
	}
}