package thw.edu.javaII.port.warehouse.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import thw.edu.javaII.port.warehouse.ui.common.Session;
import thw.edu.javaII.port.warehouse.ui.panels.BestandPage;
import thw.edu.javaII.port.warehouse.ui.panels.InfoPage;
import thw.edu.javaII.port.warehouse.ui.panels.SearchPage;
import thw.edu.javaII.port.warehouse.ui.panels.StatistikPage;
import thw.edu.javaII.port.warehouse.ui.panels.WelcomePage;

/**
 * Diese Klasse fungiert als zentraler Ereignis-Controller (ActionListener) für das Hauptmenü der Anwendung.
 * Sie fängt alle Klicks auf die Menüeinträge der oberen Leiste ab, wertet den dazugehörigen Befehl
 * aus und führt die entsprechende Aktion aus – wie beispielsweise das Wechseln der Hauptansicht
 * im Fenster oder das Beenden des Programms.
 */
public class LagerUIHandler implements ActionListener {
	private final Session ses;
	private final JFrame frame;
	private final JFrame parent;
	private final JPanel contentPane;

	/**
	 * Erstellt den Handler für das Hauptmenü.
	 * * @param ses         Die aktuelle Benutzersitzung für die Kommunikation mit dem Server.
	 * @param frame       Das aktuelle Hauptfenster (LagerUI). Wird benötigt, um das Fenster z. B. beim Beenden schließen zu können.
	 * @param contentPane Der Hauptbereich (Container) des Fensters, in dem die wechselnden Ansichten (Panels) angezeigt werden.
	 * @param parent      Das übergeordnete Fenster, das teilweise an Kind-Dialoge oder -Panels weitergereicht wird.
	 */
	public LagerUIHandler(Session ses, JFrame frame, JPanel contentPane, JFrame parent) {
		this.ses = ses;
		this.frame = frame;
		this.contentPane = contentPane;
		this.parent = parent;
	}

	/**
	 * Diese Methode wird automatisch aufgerufen, sobald der Benutzer auf einen Menüeintrag klickt.
	 * Sie liest den hinterlegten Befehl (ActionCommand) aus und wechselt mithilfe
	 * einer Switch-Anweisung in den entsprechenden Programmzweig.
	 * * @param e Das ausgelöste Klick-Ereignis, welches den genauen Befehl enthält.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		MenuActionCommands command = MenuActionCommands.valueOf(e.getActionCommand());

		switch (command) {
			case BEENDEN:
				ses.close();
				frame.dispose();
				break;
			case SERVERBEENDEN:
				ses.getCommunicator().closeServer();
				frame.dispose();
				break;
			case STARTSEITE:
				switchPage(new WelcomePage());
				break;
			case BESTAND:
				switchPage(new BestandPage());
				break;
			case STATISTIK:
				switchPage(new StatistikPage(ses, parent));
				break;
			case SUCHEN:
				switchPage(new SearchPage(ses));
				break;
			case INFO:
				switchPage(new InfoPage());
				break;
			default:
				break;
		}
	}

	/**
	 * Eine interne Hilfsmethode, die den dynamischen Mittelteil der Benutzeroberfläche austauscht.
	 * Entfernt die bisherige Ansicht und setzt das übergebene, neue Panel in das Zentrum (BorderLayout.CENTER) ein.
	 * Anschließend wird die Oberfläche angewiesen, sich selbst neu zu strukturieren und zu zeichnen.
	 * * @param newPage Das neue JPanel, das dem Benutzer angezeigt werden soll.
	 */
	private void switchPage(JPanel newPage) {
		BorderLayout layout = (BorderLayout) contentPane.getLayout();
		contentPane.remove(layout.getLayoutComponent(BorderLayout.CENTER));
		contentPane.add(newPage, BorderLayout.CENTER);
		contentPane.revalidate(); // UI-Struktur neu berechnen
		contentPane.repaint();    // UI neu zeichnen
	}
}