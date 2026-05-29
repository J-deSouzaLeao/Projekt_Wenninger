package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.model.BestandTableModel;

/**
 * Diese Klasse repräsentiert die Such- und Verwaltungsansicht der grafischen Benutzeroberfläche.
 * Hier kann der Benutzer gezielt nach Lagerbeständen suchen, die angezeigten Ergebnisse
 * in einer Tabelle einsehen, bestehende Bestände bearbeiten oder neue anlegen.
 * Durch den integrierten TableRowSorter erfolgt die Suche nun in Echtzeit und
 * filtert zuverlässig über alle Spalten (inklusive IDs und Namen).
 *
 * @author juan.de.souza.leao
 */
public class SearchPage extends JPanel {

	@Serial
	private static final long serialVersionUID = 8512898500044030449L;
	private final JTable table;
	private final JTextField textField;
	private final BestandTableModel model;
	private final TableRowSorter<BestandTableModel> sorter;

	/**
	 * Erstellt das Such-Panel und baut die komplette Benutzeroberfläche auf.
	 * Die Tabelle wird initial mit dem kompletten Bestand gefüllt und ein Sorter
	 * für die Echtzeit-Suche wird darübergelegt.
	 *
	 * @param ses Die aktuelle Benutzersitzung für die Kommunikation mit dem Server.
	 */
	public SearchPage(Session ses) {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Lagerbestand");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		add(lblNewLabel, BorderLayout.NORTH);

		// --- Tabellen-Modell und Sorter initialisieren ---
		model = new BestandTableModel(ses.getCommunicator().getBestand());
		table = new JTable(model);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setGridColor(Color.DARK_GRAY);

		// Sorter für die Live-Suche über alle Spalten anlegen
		sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);

		// 1. Spaltenbreiten prozentual verteilen (Gesamtbreite z.B. 1000 Pixel als Basis)
		// Reihenfolge: ID (10%), Name (25%), Hersteller (20%), Menge (10%), Platz (15%), Standort (20%)
		model.setJTableColumnsWidth(table, 1000, 10, 25, 20, 10, 15, 20);

		// 2. Die Spalte "Menge (Stk.)" zur besseren Lesbarkeit zentrieren
		javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

		// 3. Tabellenkopf (Überschriften) optisch abheben
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		table.setRowHeight(25); // Zeilen etwas höher machen für leichtere Lesbarkeit (Touch/Klick)

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

		// Suchfeld-Bereich
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

		// --- Live-Überwachung des Suchfeldes (Echtzeit-Filterung) ---
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				anwendenFilter();
			}
		});

		JButton btnSearch = createBtnSearch();
		panel_1.add(btnSearch);

		JLabel lblNewLabel_1 = new JLabel("Ergebnisse");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);

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
	 * Wendet den Text aus dem Suchfeld als Filter auf die Tabelle an.
	 * Ist das Feld leer, wird der Filter entfernt und alles angezeigt.
	 */
	private void anwendenFilter() {
		String searchText = textField.getText().trim();
		if (searchText.isEmpty()) {
			sorter.setRowFilter(null);
		} else {
			// (?i) = Sucht unabhängig von Groß- und Kleinschreibung
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
		}
	}

	/**
	 * Erstellt den unteren Bereich (Panel) mit den Haupt-Aktionsschaltflächen.
	 *
	 * @param ses Die aktuelle Benutzersitzung.
	 * @return Das fertig konfigurierte Panel für den unteren Rand.
	 */
	private JPanel getJPanel(Session ses) {
		JPanel pannel_2 = new JPanel();

		JButton btnNewButton = createBtnNewButton(ses);
		pannel_2.add(btnNewButton);

		// Der Button "Neues Produkt einlagern" wurde hier entfernt,
		// da die Neuanlage nun zentral über die Stammdatenverwaltung erfolgt.

		return pannel_2;
	}

	/**
	 * Erstellt den "Suchen"-Button.
	 * Da wir nun Live-Suche haben, wendet der Button einfach den Sorter an,
	 * falls der Nutzer aus Gewohnheit darauf klickt.
	 *
	 * @return Der fertig konfigurierte Such-Button.
	 */
	private JButton createBtnSearch() {
		JButton btnSearch = new JButton("Suchen");
		btnSearch.addActionListener(e -> anwendenFilter());
		return btnSearch;
	}

	/**
	 * Erstellt den "Verändern"-Button für die Bearbeitung eines Bestands.
	 * Da die Tabelle nun gefiltert sein kann, wird hier der korrekte Index
	 * von der Ansicht (View) auf das Modell umgerechnet.
	 *
	 * @param ses Die aktuelle Benutzersitzung.
	 * @return Der fertig konfigurierte "Verändern"-Button.
	 */
	private JButton createBtnNewButton(Session ses) {
		JButton btnNewButton = new JButton("Bestand Verändern");
		btnNewButton.addActionListener(e -> {
			int selectedRowView = table.getSelectedRow();
			if (selectedRowView == -1) {
				JOptionPane.showMessageDialog(this, "Bitte wählen Sie zuerst einen Eintrag zum Verändern aus.",
						"Hinweis", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// WICHTIG: Visuelle Zeile in die echte Daten-Zeile umrechnen (wegen Filter/Sortierung)
			int selectedRowModel = table.convertRowIndexToModel(selectedRowView);

			LagerBestand l = model.getObjectAt(selectedRowModel);
			ChangeLagerBestand clb = new ChangeLagerBestand(l, ses);
			clb.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			clb.setModalityType(ModalityType.APPLICATION_MODAL);
			clb.setVisible(true);

			// Nach dem Ändern einfach den neuesten Stand laden, der Sorter behält den Suchtext automatisch!
			model.setData(ses.getCommunicator().getBestand());
			model.fireTableDataChanged();
		});
		return btnNewButton;
	}
}