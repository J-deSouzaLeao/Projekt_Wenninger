package thw.edu.javaII.port.warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.model.LagerPlatzComboboxModel;

import javax.swing.JComboBox;

/**
 * Diese Klasse repräsentiert einen Dialog (Pop-up-Fenster) zum Anlegen eines komplett neuen Produkts.
 * Der Benutzer kann hier die Stammdaten (Name, Hersteller, Preis) sowie die erste Einlagerung
 * (Stückzahl und Zuweisung auf einen aktuell noch freien Lagerplatz) komfortabel in einem Schritt vornehmen.
 * * @author juan.de.souza.leao
 */
public class AddProdukt extends JDialog {

	@Serial
	private static final long serialVersionUID = -8118048952794691740L;
	private final JTextField txtName;
	private final JTextField txtHersteller;
	private final JTextField txtPreis;
	private final JTextField txtBestand;

	/**
	 * Erstellt den Dialog zum Hinzufügen eines Produkts.
	 * Baut das Formular auf, fragt beim Server alle derzeit komplett leeren Lagerplätze
	 * für das Dropdown-Menü ab und konfiguriert den Speichervorgang.
	 * * @param ses Die aktuelle Benutzersitzung für die Netzwerkkommunikation.
	 */
	public AddProdukt(Session ses) {
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(450, 300);
		setBounds(ss.width / 2 - frameSize.width / 2, ss.height / 2 - frameSize.height / 2, 450, 365);
		getContentPane().setLayout(new BorderLayout());

		JLabel lblNewLabel = new JLabel("Produkt - hinzufügen");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblNewLabel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[99.00][grow][][]", "[][][][][][][][][][]"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblNewLabel_3 = new JLabel("Name:");
		contentPanel.add(lblNewLabel_3, "cell 0 0,alignx trailing");

		txtName = new JTextField();
		contentPanel.add(txtName, "cell 1 0,growx");
		txtName.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Hersteller:");
		contentPanel.add(lblNewLabel_4, "cell 0 1,alignx trailing");

		txtHersteller = new JTextField();
		contentPanel.add(txtHersteller, "cell 1 1,growx");
		txtHersteller.setColumns(10);

		JLabel lblNewLabel_15 = new JLabel("Einzelpreis:");
		contentPanel.add(lblNewLabel_15, "cell 0 2,alignx trailing");

		txtPreis = new JTextField();
		contentPanel.add(txtPreis, "cell 1 2,growx");
		txtPreis.setColumns(10);

		JLabel lblNewLabel_10 = new JLabel("Bestand:");
		contentPanel.add(lblNewLabel_10, "cell 0 3,alignx trailing");

		txtBestand = new JTextField();
		contentPanel.add(txtBestand, "cell 1 3,growx");
		txtBestand.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel("Lagerplatz:");
		contentPanel.add(lblNewLabel_9, "cell 0 4,alignx trailing");

		// Holt sich die freien Plätze und füllt damit das Dropdown-Menü
		LagerPlatzComboboxModel model = new LagerPlatzComboboxModel(ses.getCommunicator().getFreeLagerPlatz());
		JComboBox<LagerPlatz> cbLagerPlatz = new JComboBox<>(model);
		contentPanel.add(cbLagerPlatz, "cell 1 4,growx");

		// Speichern-Button inkl. Verarbeitung der Eingaben
		JButton btnSpeichern = new JButton("speichern");
		btnSpeichern.addActionListener(e -> {
			try {
				// 1. Text auslesen, trimmen und ggf. Komma durch Punkt ersetzen (für den Preis)
				double preis = Double.parseDouble(txtPreis.getText().trim().replace(",", "."));
				int bestand = Integer.parseInt(txtBestand.getText().trim());

				// 2. Objekte mit den sicheren Zahlenwerten erstellen
				Produkt p = new Produkt(0, txtName.getText().trim(), txtHersteller.getText().trim(), preis);
				LagerBestand l = new LagerBestand(0, bestand, p,
						cbLagerPlatz.getModel().getElementAt(cbLagerPlatz.getSelectedIndex()));

				// 3. An den Server senden
				boolean okay = ses.getCommunicator().addProdukt(p, l);
				if (okay) {
					dispose(); // Schließt den Dialog bei Erfolg
				} else {
					JOptionPane.showMessageDialog(this, "Fehler beim Speichern. Der Datensatz konnte nicht gespeichert werden.",
							"Fehler: Speichern", JOptionPane.ERROR_MESSAGE);
				}

			} catch (NumberFormatException ex) {
				// --- HIER WIRD DER FEHLER BEI TEXTEINGABE ABGEFANGEN ---
				JOptionPane.showMessageDialog(this,
						"Ungültige Eingabe! Bitte stellen Sie sicher, dass 'Einzelpreis' und 'Bestand' nur aus Zahlen bestehen.",
						"Eingabefehler",
						JOptionPane.WARNING_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Ein unerwarteter Fehler ist aufgetreten: " + ex.getMessage(),
						"Fehler", JOptionPane.ERROR_MESSAGE);
			}
		});

		contentPanel.add(btnSpeichern, "cell 2 8");

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton btnClose = new JButton("Schließen");
		btnClose.addActionListener(e -> dispose());
		btnClose.setActionCommand("OK");
		buttonPane.add(btnClose);
	}

}