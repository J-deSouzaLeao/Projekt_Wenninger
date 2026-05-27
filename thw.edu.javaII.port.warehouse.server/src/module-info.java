/**
 * Dieses Modul definiert den Server-Teil der Lagerverwaltungs-Anwendung.
 * Es bündelt die gesamte Backend-Logik, die Netzwerkkommunikation (Sockets)
 * und die Datenbankanbindung (SQLite).
 * * @author juan.de.souza.leao
 */
module thw.edu.javaII.port.warehouse.server {
	// Eigene Module für Modelle und Startdaten
	requires thw.edu.javaII.port.warehouse.model;
	requires thw.edu.javaII.port.warehouse.init;

	// Standard-Java-Module für Datenbank und Logging
	requires java.sql;
	requires java.logging;

	// Externer SQLite-Treiber für die Datenbankverbindung
	requires org.xerial.sqlitejdbc;
}