package thw.edu.javaII.port.warehouse.model.common;

/**
 * Diese Klasse speichert zentrale Konfigurationsdaten und Konstanten für das Programm.
 * Sie enthält feste Informationen für die Serververbindung und das Logging,
 * auf die von überall im System zugegriffen werden kann, ohne sie hart kodieren zu müssen.
 * * @author juan.de.souza.leao
 */
public class Info {

	/**
	 * Der Netzwerk-Port, über den der Server erreichbar ist.
	 */
	public final static int PORT_SERVER = 5010;

	/**
	 * Die Adresse oder der Hostname des Servers (hier standardmäßig lokal).
	 */
	public final static String NAME_SERVER = "localhost";

	/**
	 * Die maximale Wartezeit in Millisekunden, bevor der Client bei Verbindungsproblemen abbricht.
	 */
	public final static int TIMEOUT_CLIENT = 50000;

	/**
	 * Der Dateiname für die Log-Datei, in der Fehler und Ereignisse gespeichert werden.
	 */
	public final static String LOG_NAME = "warehouse.log";
}