package thw.edu.javaII.port.warehouse.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import thw.edu.javaII.port.warehouse.ui.model.BestandTableModel;
import thw.edu.javaII.port.warehouse.ui.BackendClient;
import thw.edu.javaII.port.warehouse.model.LagerBestand;

/**
 * Diese Klasse repräsentiert eine einfache Übersichtsseite für den aktuellen Lagerbestand.
 * Im Gegensatz zur umfassenden Verwaltungsansicht dient dieses Panel ausschließlich dazu,
 * dem Benutzer einen schnellen, reinen Lesezugriff auf den gesamten Bestand (Produkt, Menge, Lagerplatz)
 * in Form einer großen Tabelle zu geben.
 * * Hinweis zur Architektur: Um Verbindungsabbrüche (Timeouts) bei längerer Inaktivität
 * zu vermeiden, baut diese Ansicht bei jedem Aufruf temporär eine frische Verbindung
 * zum Server auf, lädt die Daten und schließt die Verbindung sofort wieder.
 * * @author juan.de.souza.leao
 */
public class BestandPage extends JPanel {

	@Serial
	private static final long serialVersionUID = 2848864973063147806L;
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(BestandPage.class.getName());

	/**
	 * Erstellt das Panel für die Bestandsübersicht.
	 * Baut das Layout auf, baut eine kurzlebige Verbindung zum Backend auf,
	 * ruft die aktuellen Bestandsdaten ab und füllt damit die Anzeigetabelle.
	 * * @param ses Die aktuelle Benutzersitzung (wird hier für das Layout/Kontext entgegengenommen,
	 * aber für den Datenabruf wird eine dedizierte neue Verbindung genutzt).
	 */
	public BestandPage() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Lagerbestand");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		add(lblNewLabel, BorderLayout.NORTH);

		// 1. Sicheres Laden der Daten (verhindert Timeout-Abstürze)
		List<LagerBestand> aktuelleBestaende = loadBestandData();

		// 2. Initialisierung des TableModels mit den frisch geladenen Daten
		BestandTableModel model = new BestandTableModel(aktuelleBestaende);
		JTable table = new JTable(model);

		// 3. Konfiguration der Tabellenoptik
		model.setJTableColumnsWidth(table, 800, 10, 20, 20, 10, 20, 20);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setGridColor(Color.DARK_GRAY);

		JScrollPane js = new JScrollPane(table);
		js.setVisible(true);
		add(js, BorderLayout.CENTER);
	}

	/**
	 * Baut eine eigenständige, frische Verbindung zum Server auf, um die
	 * aktuellen Lagerbestände abzurufen. Dies verhindert Abstürze durch
	 * inaktive, vom Server geschlossene Sockets (Timeouts).
	 * * @return Eine Liste aller aktuellen Lagerbestände. Im Fehlerfall wird
	 * eine leere Liste zurückgegeben und eine Warnung angezeigt.
	 */
	private List<LagerBestand> loadBestandData() {
		List<LagerBestand> data = new ArrayList<>();
		try {
			// Frische Verbindung aufbauen
			BackendClient client = new BackendClient();

			// Daten sicher abrufen
			data = client.getAllLagerBestaende();

			// Verbindung sauber schließen
			client.close();

		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler beim Laden der Bestandsübersicht", e);
			JOptionPane.showMessageDialog(
					this,
					"Fehler beim Laden der Bestände: " + e.getMessage(),
					"Netzwerkfehler",
					JOptionPane.ERROR_MESSAGE
			);
		}
		return data;
	}
}