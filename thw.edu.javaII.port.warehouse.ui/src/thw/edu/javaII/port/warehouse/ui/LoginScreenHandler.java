package thw.edu.javaII.port.warehouse.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import thw.edu.javaII.port.warehouse.ui.common.Session;

/**
 * Diese Klasse fungiert als Controller für das Anmeldefenster (LoginScreen) der Hauptanwendung.
 * Sie verarbeitet die Benutzerinteraktionen, indem sie sowohl auf Klicks des Login-Buttons
 * (über den ActionListener) als auch auf Tastatureingaben wie die Enter-Taste in den Textfeldern
 * (über den KeyListener) lauscht.
 * * @author juan.de.souza.leao
 */
public class LoginScreenHandler implements ActionListener, KeyListener {
	private final Session ses;
	private final JFrame frame;
	private final JTextField txtUser;
	private final JPasswordField txtPassword;
	private final JLabel lblHint;

	/**
	 * Erstellt den Handler und verknüpft ihn mit den relevanten UI-Elementen des Login-Fensters.
	 * * @param ses         Die aktuelle Benutzersitzung, um den Login-Status zu speichern.
	 * @param frame       Das Login-Fenster, welches bei erfolgreicher Anmeldung geschlossen wird.
	 * @param txtUser     Das Eingabefeld für den Benutzernamen.
	 * @param txtPassword Das Eingabefeld für das Passwort.
	 * @param lblHint     Das Text-Label zur Anzeige von Fehlermeldungen (z.B. "Logindaten fehlerhaft!").
	 */
	public LoginScreenHandler(Session ses, JFrame frame, JTextField txtUser, JPasswordField txtPassword,
	                          JLabel lblHint) {
		this.ses = ses;
		this.frame = frame;
		this.txtUser = txtUser;
		this.txtPassword = txtPassword;
		this.lblHint = lblHint;

	}

	/**
	 * Wird ausgelöst, wenn der Benutzer mit der Maus auf den "Login"-Button klickt.
	 * * @param e Das Klick-Ereignis.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		checkLogin();
	}

	/**
	 * Führt die eigentliche Überprüfung der Anmeldedaten durch.
	 * Liest Benutzername und Passwort aus, leitet sie zur Verifizierung an den UserService weiter
	 * und reagiert entsprechend: Bei Erfolg wird die Session freigegeben und das Fenster geschlossen.
	 * Bei einem Fehler wird das Passwortfeld geleert und ein Hinweis angezeigt.
	 */
	private void checkLogin() {
		if (!txtUser.getText().isEmpty() && !String.valueOf(txtPassword.getPassword()).isEmpty()) {
			if (ses.getUserService().checkLogin(txtUser.getText(), String.valueOf(txtPassword.getPassword()))) {
				ses.setLogin(true);
				frame.dispose();
			} else {
				lblHint.setText("Logindaten fehlerhaft!");
				txtPassword.setText("");
			}
		}
	}

	/**
	 * Wird ausgelöst, wenn eine Taste in einem der überwachten Textfelder gedrückt wird.
	 * Ermöglicht es dem Benutzer, sich komfortabel durch Drücken der Enter-Taste anzumelden,
	 * ohne die Maus benutzen zu müssen.
	 * * @param e Das Tastatur-Ereignis.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			checkLogin();
		}

	}

	/**
	 * Wird aufgerufen, wenn ein Zeichen getippt wird.
	 * Für den Login nicht relevant, muss aber wegen des KeyListener-Interfaces implementiert werden.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Wird aufgerufen, wenn eine gedrückte Taste wieder losgelassen wird.
	 * Für den Login nicht relevant, muss aber wegen des KeyListener-Interfaces implementiert werden.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}
}