package thw.edu.javaII.port.warehouse.ui.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein einfacher Dienst zur Benutzerverwaltung und Authentifizierung für die grafische Oberfläche.
 * In dieser Implementierung wird eine hartkodierte Liste von Benutzern (Mock-Daten) verwendet,
 * um die Anmeldedaten beim Start des Programms zu überprüfen. Dies dient in der Regel zu
 * Test- und Demonstrationszwecken.
 * * @author juan.de.souza.leao
 */
public class UserService {
	private final List<User> users;

	/**
	 * Standard-Konstruktor.
	 * Initialisiert die interne Benutzerliste und fügt direkt einige vordefinierte
	 * Test-Benutzer (Benutzername und Passwort) für den Login hinzu.
	 */
	public UserService() {
		users = new ArrayList<>();
		users.add(new User("user","pass"));
		users.add(new User("jsh","jsh123"));
		users.add(new User("mmn","mmn123"));
	}

	/**
	 * Überprüft, ob die eingegebenen Anmeldedaten (Benutzername und Passwort)
	 * mit einem der hinterlegten Benutzer in der Liste übereinstimmen.
	 * * @param user Der vom Anwender eingegebene Benutzername.
	 * @param pass Das vom Anwender eingegebene Passwort.
	 * @return true, wenn die Kombination aus Benutzername und Passwort gefunden wurde, andernfalls false.
	 */
	public boolean checkLogin(String user, String pass) {
		for(User u : users) {
			if(u.userName().equals(user) && u.password().equals(pass)) {
				return true;
			}
		}
		return false;
	}

}

/**
 * Ein kompaktes Datenobjekt (Record) zur reinen Speicherung der Anmeldeinformationen eines Benutzers.
 * Records eignen sich in Java ideal für unveränderliche (immutable) Datenstrukturen wie diese.
 * * @param userName Der Kontoname des Benutzers.
 * @param password Das zugehörige Passwort.
 */
record User(String userName, String password) {

}