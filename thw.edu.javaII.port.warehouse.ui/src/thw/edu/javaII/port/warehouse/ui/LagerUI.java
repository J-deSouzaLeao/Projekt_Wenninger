package thw.edu.javaII.port.warehouse.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.panels.WelcomePage;

/**
 * Diese Klasse repräsentiert das Hauptfenster (Main Frame) der grafischen Benutzeroberfläche.
 * Sie dient als Rahmen für die gesamte Anwendung und enthält das Hauptmenü (oben),
 * eine Fußzeile (unten) und einen dynamischen Mittelbereich, in dem die verschiedenen
 * Ansichten (Panels) je nach Menüauswahl ausgetauscht werden.
 *
 * @author juan.de.souza.leao
 */
public class LagerUI extends JFrame {
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(LagerUI.class.getName());

	@Serial
	private static final long serialVersionUID = -5670441158631808726L;
	private final JPanel contentPane;
	private final Session ses;
	private JMenuBar menuBar;

	/**
	 * Startet die grafische Benutzeroberfläche sicher im Java Swing Event Dispatch Thread (EDT).
	 *
	 * @param ses Die aktuelle Benutzersitzung, die für die Kommunikation mit dem Server benötigt wird.
	 */
	public static void run(Session ses) {
		EventQueue.invokeLater(() -> {
			try {
				LagerUI frame = new LagerUI(ses);
				frame.setVisible(true);
			} catch (Exception e) {
				LOGGER.log(java.util.logging.Level.SEVERE, "Ein Fehler ist beim Laden der Oberfläche aufgetreten", e);
			}
		});
	}

	/**
	 * Konstruktor für das Hauptfenster.
	 * Richtet die Fenstergröße ein, zentriert es auf dem Bildschirm und
	 * initialisiert die Menüleiste, die Fußzeile sowie die Startseite (WelcomePage).
	 *
	 * @param ses Die aktuelle Benutzersitzung.
	 */
	public LagerUI(Session ses) {
		this.ses = ses;
		setTitle("Lagerverwaltung");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(800, 600);
		setBounds(ss.width / 2 - frameSize.width / 2, ss.height / 2 - frameSize.height / 2, frameSize.width,
				frameSize.height);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(new WelcomePage(), BorderLayout.CENTER);
		generateMenu();
		generateFooter();
		createMenuBar();
	}

	/**
	 * Erstellt die Fußzeile (Footer) am unteren Rand des Fensters.
	 * Enthält in der Regel rechtliche Hinweise oder Copyright-Informationen.
	 */
	private void generateFooter() {
		JPanel pnCopyright = new JPanel();
		FlowLayout fl_pnCopyright = (FlowLayout) pnCopyright.getLayout();
		fl_pnCopyright.setVgap(1);
		fl_pnCopyright.setAlignment(FlowLayout.RIGHT);
		JLabel lblCopyright = new JLabel("Copyright Tobias Wenninger");
		lblCopyright.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCopyright.setFont(new Font("Lucida Grande", Font.ITALIC, 12));
		contentPane.add(pnCopyright, BorderLayout.SOUTH);
		pnCopyright.add(lblCopyright);
	}

	/**
	 * Erstellt die primären Menüeinträge der oberen Menüleiste.
	 * Verbindet Basis-Aktionen (wie Startseite, Suchen, Statistik, Beenden)
	 * mit dem zentralen Action-Handler (LagerUIHandler). Der redundante Reiter "Bestand"
	 * wurde entfernt, da die "Suche" dessen Funktionalität vollständig abdeckt.
	 */
	private void generateMenu() {
		menuBar = new JMenuBar();

		// Datei-Menü
		JMenu menuDatei = new JMenu("Datei");
		menuDatei.setPreferredSize(new Dimension(160, menuDatei.getPreferredSize().height));

		JMenuItem miStartseite = new JMenuItem("Startseite");
		miStartseite.setPreferredSize(new Dimension(160, miStartseite.getPreferredSize().height));
		miStartseite.setActionCommand(MenuActionCommands.STARTSEITE.toString());
		// KORRIGIERT: 3 Argumente statt 4
		miStartseite.addActionListener(new LagerUIHandler(ses, this, contentPane));

		JMenuItem miServerBeenden = new JMenuItem("Server Beenden");
		// KORRIGIERT: 3 Argumente statt 4
		miServerBeenden.addActionListener(new LagerUIHandler(ses, this, contentPane));
		miServerBeenden.setActionCommand(MenuActionCommands.SERVERBEENDEN.toString());

		JMenuItem miBeenden = new JMenuItem("Beenden");
		miBeenden.setPreferredSize(new Dimension(160, miBeenden.getPreferredSize().height));
		miBeenden.setActionCommand(MenuActionCommands.BEENDEN.toString());
		// KORRIGIERT: 3 Argumente statt 4
		miBeenden.addActionListener(new LagerUIHandler(ses, this, contentPane));

		// Weitere Hauptmenüpunkte
		JMenuItem miStatistik = new JMenuItem("Statistik");
		// KORRIGIERT: 3 Argumente statt 4
		miStatistik.addActionListener(new LagerUIHandler(ses, this, contentPane));
		miStatistik.setActionCommand(MenuActionCommands.STATISTIK.toString());
		miStatistik.setPreferredSize(new Dimension(160, miStatistik.getPreferredSize().height));

		JMenuItem miSuchen = new JMenuItem("Bestand");
		// KORRIGIERT: 3 Argumente statt 4
		miSuchen.addActionListener(new LagerUIHandler(ses, this, contentPane));
		miSuchen.setActionCommand(MenuActionCommands.SUCHEN.toString());
		miSuchen.setPreferredSize(new Dimension(160, miSuchen.getPreferredSize().height));

		JMenuItem miInfo = new JMenuItem("Info");
		miInfo.setPreferredSize(new Dimension(160, miInfo.getPreferredSize().height));
		// KORRIGIERT: 3 Argumente statt 4
		miInfo.addActionListener(new LagerUIHandler(ses, this, contentPane));
		miInfo.setActionCommand(MenuActionCommands.INFO.toString());

		// Elemente der Menüleiste hinzufügen
		setJMenuBar(menuBar);
		menuBar.add(menuDatei);
		menuDatei.add(miStartseite);
		menuDatei.add(miServerBeenden);
		menuDatei.add(miBeenden);

		menuBar.add(miSuchen); // Suche übernimmt jetzt primär die Bestandsansicht
		menuBar.add(miStatistik);
		menuBar.add(miInfo);
	}

	/**
	 * Erweitert die Menüleiste um zusätzliche, verwaltungsspezifische Menüs (Stammdaten, Lagerbetrieb, Kasse).
	 * Diese Menüeinträge nutzen direkte Lambda-Ausdrücke, um die jeweiligen
	 * Unter-Ansichten (Panels) in den zentralen Bereich des Fensters zu laden.
	 */
	private void createMenuBar() {
		// Menü Stammdaten
		JMenu menuStammdaten = new JMenu("Stammdaten");
		JMenuItem itemProdukte = new JMenuItem("Produkte verwalten");
		JMenuItem itemLager = new JMenuItem("Lager verwalten");
		JMenuItem itemLagerPlatz = new JMenuItem("Lagerplätze verwalten");

		menuStammdaten.add(itemProdukte);
		menuStammdaten.add(itemLager);
		menuStammdaten.add(itemLagerPlatz);

		// Menü Lagerbetrieb
		JMenu menuLagerbetrieb = new JMenu("Lagerbetrieb");
		JMenuItem itemBestand = new JMenuItem("Bestandsübersicht");
		menuLagerbetrieb.add(itemBestand);

		// Action Listeners für Stammdaten & Lagerbetrieb
		itemProdukte.addActionListener(e -> showPanel(new thw.edu.javaII.port.warehouse.ui.panels.ProduktVerwaltungPanel(ses)));
		itemLager.addActionListener(e -> showPanel(new thw.edu.javaII.port.warehouse.ui.panels.LagerVerwaltungPanel()));
		itemLagerPlatz.addActionListener(e -> showPanel(new thw.edu.javaII.port.warehouse.ui.panels.LagerPlatzVerwaltungPanel()));
		itemBestand.addActionListener(e -> showPanel(new thw.edu.javaII.port.warehouse.ui.panels.LagerBestandVerwaltungPanel()));

		// An die bestehende menuBar anhängen
		this.menuBar.add(menuStammdaten);
		this.menuBar.add(menuLagerbetrieb);

		// Menü Kasse
		JMenu menuKasse = new JMenu("Kasse");

		// 1. ZUERST die Variablen deklarieren
		JMenuItem itemKassenzettel = new JMenuItem("Kassenzettel verwalten");
		JMenuItem itemAbschluesse = new JMenuItem("Kassenabschlüsse einsehen"); // NEU

		// 2. DANN die Items an das Menü anhängen
		menuKasse.add(itemKassenzettel);
		menuKasse.add(itemAbschluesse); // NEU

		// 3. ZULETZT die Klick-Aktionen (Action-Listener) zuweisen
		itemKassenzettel.addActionListener(e -> showPanel(new thw.edu.javaII.port.warehouse.ui.panels.KassenzettelVerwaltungPanel()));
		itemAbschluesse.addActionListener(e -> showPanel(new thw.edu.javaII.port.warehouse.ui.panels.KassenabschlussVerwaltungPanel(ses))); // NEU

		// 4. Das fertige Kassen-Menü an die Leiste anhängen
		this.menuBar.add(menuKasse);
	}

	/**
	 * Tauscht das aktuell in der Mitte des Fensters angezeigte Panel gegen ein neues aus.
	 * Wird verwendet, um bei Klicks im Menü nahtlos zwischen den verschiedenen
	 * Ansichten (z. B. Startseite zu Produktverwaltung) zu wechseln, ohne das Fenster neu laden zu müssen.
	 *
	 * @param panel Das neu anzuzeigende JPanel (die neue Ansicht).
	 */
	public void showPanel(JPanel panel) {
		// Entfernt das aktuelle Panel in der Mitte (z.B. WelcomePage oder andere)
		java.awt.Component centerComponent = ((BorderLayout) contentPane.getLayout()).getLayoutComponent(BorderLayout.CENTER);
		if (centerComponent != null) {
			contentPane.remove(centerComponent);
		}
		// Fügt das neue Panel hinzu
		contentPane.add(panel, BorderLayout.CENTER);
		contentPane.revalidate();
		contentPane.repaint();
	}

}