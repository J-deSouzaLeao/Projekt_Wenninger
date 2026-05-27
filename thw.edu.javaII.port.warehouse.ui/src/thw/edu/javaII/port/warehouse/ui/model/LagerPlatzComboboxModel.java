package thw.edu.javaII.port.warehouse.ui.model;

import javax.swing.DefaultComboBoxModel;

import thw.edu.javaII.port.warehouse.model.LagerPlatz;

import java.io.Serial;

/**
 * Diese Klasse ist ein spezielles Datenmodell für ein Dropdown-Menü (ComboBox) in der Benutzeroberfläche.
 * Sie sorgt dafür, dass die Auswahlbox ausschließlich Lagerplatz-Objekte verwalten kann
 * und macht es einfacher, den vom Benutzer ausgewählten Platz im Code direkt als
 * richtiges Objekt weiterzuverarbeiten (anstatt nur als simplen Text).
 * * @author juan.de.souza.leao
 */
public class LagerPlatzComboboxModel extends DefaultComboBoxModel<LagerPlatz> {

	@Serial
	private static final long serialVersionUID = 4492813848639274499L;

	/**
	 * Erstellt ein neues Datenmodell für das Dropdown-Menü und füllt es direkt
	 * mit einer Start-Liste an Lagerplätzen.
	 * * @param items Ein Array mit allen Lagerplätzen, die zur Auswahl stehen sollen.
	 */
	public LagerPlatzComboboxModel(LagerPlatz[] items) {
		super(items);
	}

	/**
	 * Gibt den aktuell im Dropdown-Menü ausgewählten Lagerplatz zurück.
	 * Durch das Überschreiben dieser Methode wird automatisch direkt ein echtes
	 * LagerPlatz-Objekt zurückgegeben, sodass wir es in der Benutzeroberfläche
	 * nicht mehr mühsam manuell umwandeln (casten) müssen.
	 * * @return Der vom Benutzer ausgewählte Lagerplatz.
	 */
	@Override
	public LagerPlatz getSelectedItem() {
		return (LagerPlatz) super.getSelectedItem();
	}
}