package thw.edu.javaII.port.warehouse.ui.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.Serial;

public class WelcomePage extends JPanel {

	@Serial
    private static final long serialVersionUID = -2132296457167691423L;

	/**
	 * Create the panel.
	 */
	public WelcomePage() {

		JLabel lblNewLabel = new JLabel("Herzlich Willkommen");
		add(lblNewLabel);

	}

}
