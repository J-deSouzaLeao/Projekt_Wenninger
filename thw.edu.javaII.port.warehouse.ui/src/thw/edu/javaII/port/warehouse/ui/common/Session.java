package thw.edu.javaII.port.warehouse.ui.common;

/**
 * Diese Klasse repräsentiert die aktuelle Sitzung (Session) des Clients (Benutzeroberfläche).
 * Sie ist der zentrale Knotenpunkt, der den aktuellen Login-Status speichert und
 * gleichzeitig den Zugriff auf die Netzwerkkommunikation zum Server sowie auf die
 * Benutzerdienste (z. B. Authentifizierung) bündelt.
 * * @author juan.de.souza.leao
 */
public class Session {
	private boolean login;
	private final Communicator comm;
	private final UserService userService;

	/**
	 * Standard-Konstruktor.
	 * Startet eine neue Sitzung. Standardmäßig ist der Benutzer zu Beginn nicht eingeloggt.
	 * Außerdem werden hier die Helfer-Klassen für die Server-Kommunikation und
	 * die Benutzerverwaltung direkt mit instanziiert.
	 */
	public Session() {
		login = false;
		comm = new Communicator();
		userService = new UserService();
	}

	/**
	 * Prüft, ob der Benutzer in der aktuellen Sitzung erfolgreich angemeldet ist.
	 * * @return true, wenn der Nutzer eingeloggt ist, andernfalls false.
	 */
	public boolean isLogin() {
		return login;
	}

	/**
	 * Setzt den Login-Status der aktuellen Sitzung.
	 * Wird zum Beispiel nach erfolgreicher Eingabe von Personalnummer und PIN auf true gesetzt.
	 * * @param login Der neue Login-Status.
	 */
	public void setLogin(boolean login) {
		this.login = login;
	}

	/**
	 * Gibt das Kommunikationsobjekt zurück.
	 * Über dieses Objekt kann die Benutzeroberfläche (UI) Nachrichten und Befehle
	 * (DEOs) an den Server senden und dessen Antworten empfangen.
	 * * @return Der zuständige Communicator.
	 */
	public Communicator getCommunicator() {
		return comm;
	}

	/**
	 * Gibt den Benutzer-Dienst zurück.
	 * Dieser Service kümmert sich um spezifische Aufgaben rund um den Nutzer,
	 * wie beispielsweise die Validierung der Anmeldedaten.
	 * * @return Der zuständige UserService.
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * Beendet die aktuelle Sitzung ordnungsgemäß.
	 * Schließt insbesondere die offene Netzwerkverbindung zum Server,
	 * damit keine Ressourcen blockiert werden.
	 */
	public void close() {
		comm.close();
	}

}