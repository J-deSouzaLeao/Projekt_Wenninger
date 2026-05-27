package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serial;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.model.BestandTableModel;

/**
 * Diese Klasse repräsentiert die Such- und Verwaltungsansicht der grafischen Benutzeroberfläche.
 * Hier kann der Benutzer gezielt nach Lagerbeständen suchen, die angezeigten Ergebnisse
 * in einer Tabelle einsehen, bestehende Bestände bearbeiten (z. B. Mengen korrigieren)
 * oder den Dialog für die Einlagerung eines komplett neuen Produkts aufrufen.
 * * @author juan.de.souza.leao
 */
public class SearchPage extends JPanel {

	@Serial
	private static final long serialVersionUID = 8512898500044030449L;
	private final JTable table;
	private final JTextField textField;
	private final BestandTableModel model;

	/**
	 * Erstellt das Such-Panel und baut die komplette Benutzeroberfläche auf.
	 * Dazu gehören die Suchleiste im oberen Bereich, die große Ergebnistabelle in der Mitte
	 * und die Aktions-Buttons (Verändern / Neues Produkt) am unteren Rand.
	 * Beim Start wird die Tabelle standardmäßig mit dem kompletten Lagerbestand gefüllt.
	 * * @param ses Die aktuelle Benutzersitzung für die Kommunikation mit dem Server.
	 */
	public SearchPage(Session ses) {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Suche");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		add(lblNewLabel, BorderLayout.NORTH);

		JPanel pannel_2 = getJPanel(ses);
		add(pannel_2, BorderLayout.SOUTH);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 30, 20 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 1.0 };
		panel.setLayout(gbl_panel);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);

		textField = new JTextField();
		panel_1.add(textField);
		textField.setColumns(20);

		JButton btnSearch = createBtnSearch(ses);
		panel_1.add(btnSearch);

		JLabel lblNewLabel_1 = new JLabel("Ergebnisse");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);

		model = new BestandTableModel(ses.getCommunicator().getBestand());
		table = new JTable(model);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setGridColor(Color.DARK_GRAY);

		JScrollPane js = new JScrollPane(table);
		js.setVisible(true);
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 0;
		gbc_table.gridy = 2;
		gbc_table.weighty = 1.0;
		panel.add(js, gbc_table);
	}

	/**
	 * Erstellt den unteren Bereich (Panel) mit den Haupt-Aktionsschaltflächen.
	 * Hier werden die Buttons zum Bearbeiten eines Bestands und zum Anlegen
	 * eines neuen Produkts eingebunden.
	 * * @param ses Die aktuelle Benutzersitzung.
	 * @return Das fertig konfigurierte Panel für den unteren Rand.
	 */
	private JPanel getJPanel(Session ses) {
		JPanel pannel_2 = new JPanel();

		JButton btnNewButton = createBtnNewButton(ses);
		pannel_2.add(btnNewButton);

		JButton btnNewButton_2 = new JButton("Neues Produkt einlagern");
		btnNewButton_2.addActionListener(e -> {
			AddProdukt ap = new AddProdukt(ses);
			ap.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ap.setModalityType(ModalityType.APPLICATION_MODAL);
			ap.setVisible(true);

			// Nach dem Schließen des "Neues Produkt"-Dialogs wird die Tabelle aktualisiert
			model.setData(ses.getCommunicator().getBestand());
			model.fireTableDataChanged();
		});
		pannel_2.add(btnNewButton_2);

		return pannel_2;
	}

	/**
	 * Erstellt den "Suchen"-Button, inklusive der Logik für die Validierung der Eingabe.
	 * Stellt sicher, dass der Benutzer mindestens 3 Zeichen in das Suchfeld eingibt,
	 * bevor eine Anfrage an den Server geschickt wird.
	 * * @param ses Die aktuelle Benutzersitzung.
	 * @return Der fertig konfigurierte Such-Button.
	 */
	private JButton createBtnSearch(Session ses) {
		JButton btnSearch = new JButton("Suchen");
		btnSearch.addActionListener(e -> {
			if (textField.getText().length() < 3) {
				// FIXME ggf. ein ICON einfügen das ein Die Info entsprechend darstellt.
				JOptionPane.showMessageDialog(null, "Für eine Suche müssen mindestens 3 Zeichen eingegeben werden!",
						"Hinweis: Eingabefehler", JOptionPane.INFORMATION_MESSAGE);
			} else {
				List<LagerBestand> searchBestand = ses.getCommunicator().search(textField.getText());
				if (!searchBestand.isEmpty()) {
					model.setData(searchBestand);
					model.fireTableDataChanged();
				}
			}
		});
		return btnSearch;
	}

	/**
	 * Erstellt den "Verändern"-Button.
	 * Dieser öffnet einen Dialog, in dem der ausgewählte Eintrag aus der Tabelle bearbeitet werden kann.
	 * Nach der Bearbeitung wird die Anzeige der Tabelle automatisch aktualisiert.
	 * * @param ses Die aktuelle Benutzersitzung.
	 * @return Der fertig konfigurierte "Verändern"-Button.
	 */
	private JButton createBtnNewButton(Session ses) {
		JButton btnNewButton = new JButton("Verändern");
		btnNewButton.addActionListener(e -> {
			LagerBestand l = model.getObjectAt(table.getSelectedRow());
			ChangeLagerBestand clb = new ChangeLagerBestand(l, ses);
			clb.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			clb.setModalityType(ModalityType.APPLICATION_MODAL);
			clb.setVisible(true);

			// Aktualisiert die Tabelle nach dem Schließen des Bearbeitungs-Dialogs
			if (textField.getText().length() < 3) {
				model.setData(ses.getCommunicator().getBestand());
				model.fireTableDataChanged();
			} else {
				model.setData(ses.getCommunicator().search(textField.getText()));
				model.fireTableDataChanged();
			}
		});
		return btnNewButton;
	}
}