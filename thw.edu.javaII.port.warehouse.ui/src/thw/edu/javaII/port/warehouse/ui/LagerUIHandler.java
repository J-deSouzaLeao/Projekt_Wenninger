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

public class LagerUIHandler implements ActionListener {
	private final Session ses;
	private final JFrame frame;
	private final JFrame parent;
	private final JPanel contentPane;

	public LagerUIHandler(Session ses, JFrame frame, JPanel contentPane, JFrame parent) {
		this.ses = ses;
		this.frame = frame;
		this.contentPane = contentPane;
		this.parent = parent;
	}

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
				switchPage(new BestandPage(ses));
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
	 * Tauscht das aktuelle Center-Panel gegen ein neues aus und aktualisiert die UI.
	 */
	private void switchPage(JPanel newPage) {
		BorderLayout layout = (BorderLayout) contentPane.getLayout();
		contentPane.remove(layout.getLayoutComponent(BorderLayout.CENTER));
		contentPane.add(newPage, BorderLayout.CENTER);
		contentPane.revalidate(); // UI-Struktur neu berechnen
		contentPane.repaint();    // UI neu zeichnen
	}
}