package thw.edu.javaII.port.warehouse.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.Serial;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import thw.edu.javaII.port.warehouse.ui.common.Session;

/**
 * Diese Klasse repräsentiert das grafische Anmeldefenster (Login-Screen) für die Hauptanwendung der Lagerverwaltung.
 * Sie stellt die Eingabemasken für Benutzername und Passwort bereit und verknüpft diese
 * mit der entsprechenden Überprüfungslogik (LoginScreenHandler).
 *
 * @author barbara.liegnitz
 */
public class LoginScreen extends JFrame {

	@Serial
	private static final long serialVersionUID = 5093248500031405219L;
	private final JPanel contentPane;
	private final JPanel pnFooter = new JPanel();
	private final Session ses;

	/**
	 * Erstellt das Anmeldefenster.
	 * Konfiguriert die Fenstergröße, zentriert es auf dem Bildschirm und stößt den
	 * Aufbau der drei Hauptbereiche (Header, Content, Footer) an.
	 *
	 * @param ses Die aktuelle Benutzersitzung, die nach erfolgreichem Login aktualisiert wird.
	 */
	public LoginScreen(Session ses) {
		this.ses = ses;
		setTitle("Lagerverwaltung - Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(350, 230);
		setBounds(ss.width / 2 - frameSize.width / 2, ss.height / 2 - frameSize.height / 2, frameSize.width,
				frameSize.height);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(10, 10));
		setContentPane(contentPane);
		generateHeader();
		generateContent();
		generateFooter();
	}

	/**
	 * Erstellt die Fußzeile des Fensters.
	 * Beinhaltet den statischen Copyright-Hinweis am unteren rechten Rand.
	 */
	private void generateFooter() {
		JLabel lblCopyright = new JLabel("Copyright by Tobias Wenninger");
		FlowLayout fl_pnFooter = (FlowLayout) pnFooter.getLayout();
		fl_pnFooter.setVgap(1);
		fl_pnFooter.setAlignment(FlowLayout.RIGHT);
		contentPane.add(pnFooter, BorderLayout.SOUTH);
		pnFooter.add(lblCopyright);
		lblCopyright.setVerticalAlignment(SwingConstants.BOTTOM);
		lblCopyright.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCopyright.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
	}

	/**
	 * Erstellt den Kopfbereich des Fensters mit dem zentrierten Titel der Anwendung.
	 */
	private void generateHeader() {
		JLabel lblHeader = new JLabel("Lagerverwaltung - Login");
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblHeader.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		contentPane.add(lblHeader, BorderLayout.NORTH);
	}

	/**
	 * Baut den zentralen Inhaltsbereich mit dem Eingabeformular auf.
	 * Erstellt die Textfelder für Benutzername und Passwort, den Login-Button sowie
	 * ein Label für eventuelle Fehlermeldungen (z.B. falsches Passwort). Alle interaktiven
	 * Elemente werden hier direkt an den LoginScreenHandler gekoppelt, um Tastatureingaben
	 * (wie das Drücken der Enter-Taste) und Klicks zu verarbeiten.
	 */
	private void generateContent() {
		JPanel pnContent = new JPanel();
		contentPane.add(pnContent, BorderLayout.CENTER);
		pnContent.setLayout(new GridLayout(4, 2, 10, 0));
		JLabel lblUser = new JLabel("Benutzer:");
		JTextField txtUser = new JTextField();
		JLabel lblPassword = new JLabel("Passwort:");
		JPasswordField txtPassword = new JPasswordField();
		txtPassword.setSize(200, 10);
		JLabel lblPlacer = new JLabel("");
		JLabel lblHint = new JLabel("");
		lblHint.setFont(new Font("Lucida Grande", Font.ITALIC, 12));
		lblHint.setForeground(Color.RED);
		JButton btnLogin = new JButton("Login");

		// Verknüpfen der UI-Elemente mit der Login-Logik
		txtUser.addKeyListener(new LoginScreenHandler(ses, this, txtUser, txtPassword, lblHint));
		txtPassword.addKeyListener(new LoginScreenHandler(ses, this, txtUser, txtPassword, lblHint));
		btnLogin.addActionListener(new LoginScreenHandler(ses, this, txtUser, txtPassword, lblHint));

		pnContent.add(lblUser);
		pnContent.add(txtUser);
		pnContent.add(lblPassword);
		pnContent.add(txtPassword);
		pnContent.add(lblPlacer);
		pnContent.add(btnLogin);
		pnContent.add(lblHint);
	}

}