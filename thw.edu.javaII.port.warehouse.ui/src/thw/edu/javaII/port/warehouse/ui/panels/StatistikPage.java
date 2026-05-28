package thw.edu.javaII.port.warehouse.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ButtonGroup;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.model.BestandTableModel;

import java.awt.Font;
import java.io.Serial;
import java.util.List;
import thw.edu.javaII.port.warehouse.model.LagerBestand;

/**
 * Diese Klasse repräsentiert die Statistik-Seite in der grafischen Benutzeroberfläche.
 * Sie liefert dem Management wichtige Kennzahlen zur Optimierung des Lagerbestands.
 * Über eine Schalterleiste (Toggle-Buttons) kann bequem zwischen verschiedenen
 * Analyseverfahren gewechselt werden (Menge, gebundenes Kapital, Engpässe).
 * * @author juan.de.souza.leao
 */
public class StatistikPage extends JPanel {

	@Serial
	private static final long serialVersionUID = 6991507120124679776L;
	private final JTable table;
	private final BestandTableModel model;

	/**
	 * Erstellt das Statistik-Panel und baut die Benutzeroberfläche auf.
	 * Nutzt eine ButtonGroup für die nahtlose Umschaltung zwischen den
	 * vier zentralen Business-Metriken, ohne die UI neu laden zu müssen.
	 * * @param ses    Die aktuelle Benutzersitzung (Session) für die Serverkommunikation.
	 * @param parent Das übergeordnete Hauptfenster.
	 */
	public StatistikPage(Session ses, JFrame parent) {
		setLayout(new BorderLayout(10, 10));

		// Kopfbereich
		JLabel lblTitle = new JLabel("Lager-Statistiken & KPIs");
		lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
		lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(lblTitle, BorderLayout.NORTH);

		// Tabelle initialisieren (Standardmäßig TOP 10)
		model = new BestandTableModel(ses.getCommunicator().getTOP10Bestand());
		table = new JTable(model);
		model.setJTableColumnsWidth(table, 800, 10, 20, 20, 10, 20, 20);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setGridColor(Color.DARK_GRAY);

		JScrollPane js = new JScrollPane(table);
		add(js, BorderLayout.CENTER);

		// Aktionsbereich mit Toggle-Buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		add(buttonPanel, BorderLayout.SOUTH);

		JToggleButton btnTop10 = new JToggleButton("TOP 10 (Menge)");
		JToggleButton btnLow10 = new JToggleButton("LOW 10 (Menge)");
		JToggleButton btnKapital = new JToggleButton("Top Kapitalbindung");
		JToggleButton btnKritisch = new JToggleButton("Kritische Engpässe");

		// ButtonGroup sorgt dafür, dass immer nur exakt EIN Button gedrückt sein kann
		ButtonGroup group = new ButtonGroup();
		group.add(btnTop10);
		group.add(btnLow10);
		group.add(btnKapital);
		group.add(btnKritisch);

		buttonPanel.add(btnTop10);
		buttonPanel.add(btnLow10);
		buttonPanel.add(btnKapital);
		buttonPanel.add(btnKritisch);

		btnTop10.setSelected(true); // Start-Zustand

		// Listener für saubere, performante Updates im Hintergrund
		btnTop10.addActionListener(e -> updateTableData(ses.getCommunicator().getTOP10Bestand()));
		btnLow10.addActionListener(e -> updateTableData(ses.getCommunicator().getLOW10Bestand()));

		// HINWEIS: Diese beiden Methoden musst du im Communicator / in der Database noch anlegen!
		btnKapital.addActionListener(e -> updateTableData(ses.getCommunicator().getKapitalbindungBestand()));
		btnKritisch.addActionListener(e -> updateTableData(ses.getCommunicator().getKritischerBestand()));
	}

	/**
	 * Tauscht die Daten im Tabellenmodell aus und informiert die Oberfläche
	 * über die Änderung, anstatt das komplette Panel neu zu zeichnen.
	 * * @param neueDaten Die vom Server gelieferte, neue Liste an Lagerbeständen.
	 */
	private void updateTableData(List<LagerBestand> neueDaten) {
		if (neueDaten == null) {
			neueDaten = new java.util.ArrayList<>();
		}
		model.setData(neueDaten);
		model.fireTableDataChanged();
	}
}