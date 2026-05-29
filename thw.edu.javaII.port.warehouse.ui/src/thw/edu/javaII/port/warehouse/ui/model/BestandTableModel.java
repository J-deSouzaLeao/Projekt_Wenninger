package thw.edu.javaII.port.warehouse.ui.model;

import java.io.Serial;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import thw.edu.javaII.port.warehouse.model.LagerBestand;

/**
 * Diese Klasse fungiert als Datenmodell (Übersetzer) für grafische Tabellen (JTable) in der Benutzeroberfläche.
 * Sie nimmt eine Liste von komplexen LagerBestand-Objekten entgegen und bricht diese so herunter,
 * dass sie sauber formatiert in den entsprechenden Spalten und Zeilen der Tabelle angezeigt werden können.
 *
 * @author juan.de.souza.leao
 */
public class BestandTableModel extends AbstractTableModel {

	@Serial
	private static final long serialVersionUID = -6145988449443265248L;
	private List<LagerBestand> data;

	/**
	 * Erstellt ein neues Tabellenmodell.
	 *
	 * @param data Die Liste der Bestände, die in der Tabelle angezeigt werden sollen.
	 */
	public BestandTableModel(List<LagerBestand> data) {
		this.data = data;
	}

	/**
	 * Aktualisiert die Datenbasis der Tabelle.
	 * Wird z. B. aufgerufen, wenn der Benutzer nach einem Artikel gesucht hat und
	 * nur noch die Suchergebnisse angezeigt werden sollen.
	 *
	 * @param data Die neue Liste der Bestände.
	 */
	public void setData(List<LagerBestand> data) {
		this.data = data;
	}

	/**
	 * Gibt an, wie viele Zeilen die Tabelle zeichnen muss.
	 * Das entspricht exakt der Anzahl der Einträge in der übergebenen Datenliste.
	 *
	 * @return Die Anzahl der Zeilen.
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/**
	 * Gibt an, wie viele Spalten die Tabelle hat.
	 * Wir zeigen hier ID, Produkt, Hersteller, Menge, Lagerplatz und Lager an = 6 Spalten.
	 *
	 * @return Die Anzahl der Spalten.
	 */
	@Override
	public int getColumnCount() {
		return 6;
	}

	/**
	 * Diese Methode wird von der JTable für jedes einzelne Feld aufgerufen, um zu erfahren,
	 * welcher Text dort genau stehen soll. Sie zerlegt das Bestandsobjekt der jeweiligen Zeile
	 * in seine Einzelteile für die passenden Spalten.
	 *
	 * @param rowIndex    Die aktuelle Zeile (entspricht dem Objekt in der Liste).
	 * @param columnIndex Die aktuelle Spalte (entspricht der Eigenschaft des Objekts).
	 * @return Der Text oder Wert, der in dieser Zelle angezeigt werden soll.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LagerBestand bestand = data.get(rowIndex);

		return switch (columnIndex) {
			// --- NEU: Hier wird die ID als suchbarer String (z.B. BST-00012) formatiert ---
			case 0 -> String.format("BST-%05d", bestand.getId());
			case 1 -> bestand.getProdukt_id() != null ? bestand.getProdukt_id().getName() : "-";
			case 2 -> bestand.getProdukt_id() != null ? bestand.getProdukt_id().getHersteller() : "-";
			case 3 -> bestand.getAnzahl();
			case 4 -> bestand.getLagerplatz_id() != null ? bestand.getLagerplatz_id().getName() : "-";
			case 5 -> (bestand.getLagerplatz_id() != null && bestand.getLagerplatz_id().getLager_id() != null)
					? bestand.getLagerplatz_id().getLager_id().getName() : "-";
			default -> null;
		};
	}

	/**
	 * Gibt die Titel (Überschriften) für die einzelnen Spalten zurück.
	 * Präzises Wording für den Logistik-Betrieb.
	 */
	@Override
	public String getColumnName(int arg0) {
		return switch (arg0) {
			case 0 -> "Bestands-ID";
			case 1 -> "Artikelname";
			case 2 -> "Hersteller";
			case 3 -> "Menge (Stk.)"; // Vorher: "Lagerbestand" (verwirrend)
			case 4 -> "Regal / Platz"; // Vorher: "Lagerplatz"
			case 5 -> "Standort (Halle)"; // Vorher: "Lager"
			default -> null;
		};
	}

	/**
	 * Eine Hilfsmethode, um die Spalten der Tabelle prozentual auf eine Gesamtbreite zu verteilen.
	 * Sorgt dafür, dass z. B. die Spalte für die "ID" schmal ist, während "Produktname" viel Platz bekommt.
	 *
	 * @param table               Die JTable, deren Spaltenbreiten angepasst werden sollen.
	 * @param tablePreferredWidth Die gewünschte Gesamtbreite der Tabelle.
	 * @param percentages         Ein Array mit den prozentualen Breiten für jede Spalte (z. B. 10, 30, 20...).
	 */
	public void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
		double total = 0;
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			total += percentages[i];
		}

		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int) (tablePreferredWidth * (percentages[i] / total)));
		}
	}

	/**
	 * Ermittelt das vollständige LagerBestand-Objekt hinter einer vom Benutzer angeklickten Zeile.
	 * Wird verwendet, wenn der Nutzer eine Zeile auswählt und auf "Bearbeiten" klickt.
	 *
	 * @param selectedRow Der Index der markierten Zeile in der Tabelle.
	 * @return Das dazugehörige LagerBestand-Objekt.
	 */
	public LagerBestand getObjectAt(int selectedRow) {
		return data.get(selectedRow);
	}
}