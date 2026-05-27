package thw.edu.javaII.port.warehouse.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.model.BestandTableModel;

import java.awt.Font;
import java.io.Serial;

/**
 * Diese Klasse repräsentiert eine einfache Übersichtsseite für den aktuellen Lagerbestand.
 * Im Gegensatz zur umfassenden Verwaltungsansicht dient dieses Panel ausschließlich dazu,
 * dem Benutzer einen schnellen, reinen Lesezugriff auf den gesamten Bestand (Produkt, Menge, Lagerplatz)
 * in Form einer großen Tabelle zu geben. Es gibt hier bewusst keine Buttons für Änderungen.
 */
public class BestandPage extends JPanel {

	@Serial
	private static final long serialVersionUID = 2848864973063147806L;

	/**
	 * Erstellt das Panel für die Bestandsübersicht.
	 * Baut das Layout auf und holt sich direkt beim Start über die übergebene
	 * Benutzersitzung (Session) die aktuellen Bestandsdaten vom Server,
	 * um damit die Anzeigetabelle zu füllen.
	 * * @param ses Die aktuelle Benutzersitzung für die Kommunikation mit dem Server.
	 */
	public BestandPage(Session ses) {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Lagerbestand");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		add(lblNewLabel, BorderLayout.NORTH);

		BestandTableModel model = new BestandTableModel(ses.getCommunicator().getBestand());
		JTable table = new JTable(model);
		model.setJTableColumnsWidth(table, 800, 10, 20, 20, 10, 20, 20);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setGridColor(Color.DARK_GRAY);

		JScrollPane js = new JScrollPane(table);
		js.setVisible(true);
		add(js, BorderLayout.CENTER);

	}

}