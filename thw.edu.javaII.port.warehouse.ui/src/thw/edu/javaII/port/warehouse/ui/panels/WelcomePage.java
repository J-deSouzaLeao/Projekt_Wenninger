package thw.edu.javaII.port.warehouse.ui.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.Serial;

/**
 * Diese Klasse repräsentiert die Startseite (Willkommensbildschirm) der grafischen Benutzeroberfläche.
 * Es ist ein simples Panel (JPanel), das den Benutzer direkt nach dem Programmstart
 * oder nach einem erfolgreichen Login freundlich begrüßt.
 * * @author juan.de.souza.leao
 */
public class WelcomePage extends JPanel {

	@Serial
	private static final long serialVersionUID = -2132296457167691423L;

	/**
	 * Standard-Konstruktor.
	 * Baut das Panel auf und platziert ein einfaches Text-Label mit der
	 * Nachricht "Herzlich Willkommen" auf der Oberfläche.
	 */
	public WelcomePage() {

		JLabel lblNewLabel = new JLabel("Herzlich Willkommen");
		add(lblNewLabel);

	}

}