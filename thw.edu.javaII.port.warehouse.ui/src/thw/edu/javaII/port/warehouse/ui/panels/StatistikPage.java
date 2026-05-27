package thw.edu.javaII.port.warehouse.ui.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.model.BestandTableModel;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.Serial;

/**
 * Diese Klasse repräsentiert die Statistik-Seite in der grafischen Benutzeroberfläche.
 * Sie zeigt eine übersichtliche Tabelle an, in der der Benutzer bequem zwischen
 * den "Top 10" (Artikel mit dem absolut höchsten Lagerbestand) und den "Low 10"
 * (Artikel mit dem geringsten Bestand, die eventuell nachbestellt werden müssen) wechseln kann.
 * * @author juan.de.souza.leao
 */
public class StatistikPage extends JPanel {

	@Serial
	private static final long serialVersionUID = 6991507120124679776L;
	private JTable table;
	private final JToggleButton tglbtnNewToggleButton;
	private final JToggleButton tglbtnNewToggleButton_1;

	/**
	 * Erstellt das Statistik-Panel und baut die dazugehörige Benutzeroberfläche auf.
	 * Beim ersten Aufruf wird standardmäßig die Top 10-Ansicht vom Server geladen.
	 * Außerdem richtet diese Methode die Toggle-Buttons (Schalter) am unteren Rand ein,
	 * mit denen dynamisch die Daten ausgetauscht werden können.
	 * * @param ses    Die aktuelle Benutzersitzung (Session), über die die Verbindung
	 * zum Server und die Datenabfrage gesteuert wird.
	 * @param parent Das übergeordnete Hauptfenster (JFrame). Wird benötigt, um das Fenster
	 * nach dem Umschalten der Tabellen neu zu zeichnen (aktualisieren).
	 */
	public StatistikPage(Session ses, JFrame parent) {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Statistik");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		add(lblNewLabel, BorderLayout.NORTH);

		BestandTableModel model = new BestandTableModel(ses.getCommunicator().getTOP10Bestand());
		table = new JTable(model);
		model.setJTableColumnsWidth(table, 800, 10, 20, 20, 10, 20, 20);
		table.setShowGrid(true);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setGridColor(Color.DARK_GRAY);
		JScrollPane js = new JScrollPane(table);
		js.setVisible(true);
		add(js, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		tglbtnNewToggleButton = new JToggleButton("TOP 10");
		tglbtnNewToggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnNewToggleButton_1.isSelected()) {
					tglbtnNewToggleButton.setSelected(true);
					tglbtnNewToggleButton_1.setSelected(false);
					BestandTableModel model = new BestandTableModel(ses.getCommunicator().getTOP10Bestand());
					table = new JTable(model);
					model.setJTableColumnsWidth(table, 800, 10, 20, 20, 10, 20, 20);
					table.setShowGrid(true);
					table.setShowVerticalLines(true);
					table.setShowHorizontalLines(true);
					table.setGridColor(Color.DARK_GRAY);
					JScrollPane js = new JScrollPane(table);
					js.setVisible(true);
					BorderLayout layout = (BorderLayout) getLayout();
					remove(layout.getLayoutComponent(BorderLayout.CENTER));
					add(js, BorderLayout.CENTER);
					setVisible(true);
					parent.setVisible(true);
				}
				else {
					tglbtnNewToggleButton.setSelected(true);
				}
			}
		});
		tglbtnNewToggleButton.setSelected(true);
		panel.add(tglbtnNewToggleButton);


		tglbtnNewToggleButton_1 = new JToggleButton("LOW 10");
		tglbtnNewToggleButton_1.addActionListener(e -> {
			if(tglbtnNewToggleButton.isSelected()) {
				tglbtnNewToggleButton_1.setSelected(true);
				tglbtnNewToggleButton.setSelected(false);
				BestandTableModel model1 = new BestandTableModel(ses.getCommunicator().getLOW10Bestand());
				table = new JTable(model1);
				model1.setJTableColumnsWidth(table, 800, 10, 20, 20, 10, 20, 20);
				table.setShowGrid(true);
				table.setShowVerticalLines(true);
				table.setShowHorizontalLines(true);
				table.setGridColor(Color.DARK_GRAY);
				JScrollPane js1 = new JScrollPane(table);
				js1.setVisible(true);
				BorderLayout layout = (BorderLayout) getLayout();
				remove(layout.getLayoutComponent(BorderLayout.CENTER));
				add(js1, BorderLayout.CENTER);
				setVisible(true);
				parent.setVisible(true);
			}
			else {
				tglbtnNewToggleButton_1.setSelected(true);
			}
		});
		tglbtnNewToggleButton_1.setSelected(false);
		panel.add(tglbtnNewToggleButton_1);

	}

}