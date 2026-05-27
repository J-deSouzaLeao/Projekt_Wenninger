package thw.edu.javaII.port.warehouse.ui.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import thw.edu.javaII.port.warehouse.model.common.Cast;
import thw.edu.javaII.port.warehouse.model.common.Info;
import thw.edu.javaII.port.warehouse.model.deo.Command;
import thw.edu.javaII.port.warehouse.model.deo.Status;
import thw.edu.javaII.port.warehouse.model.deo.WarehouseDEO;
import thw.edu.javaII.port.warehouse.model.deo.WarehouseReturnDEO;
import thw.edu.javaII.port.warehouse.model.deo.Zone;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;

/**
 * Diese Klasse übernimmt die gesamte Netzwerkkommunikation für den Client (Benutzeroberfläche).
 * Sie baut die Socket-Verbindung zum Server auf, wandelt lokale Methodenaufrufe in
 * Datenpakete (DEOs) um, sendet diese über das Netzwerk und gibt die Antworten
 * des Servers an die Oberfläche zurück.
 * * @author juan.de.souza.leao
 */
public class Communicator {
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Communicator.class.getName());
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private Socket sock;

	/**
	 * Standard-Konstruktor.
	 * Baut sofort beim Erstellen eine Verbindung zum konfigurierten Server auf
	 * und öffnet die Kanäle zum Senden (OutputStream) und Empfangen (InputStream) von Daten.
	 */
	public Communicator() {
		try {
			sock = new Socket(Info.NAME_SERVER, Info.PORT_SERVER);
			sock.setSoTimeout(Info.TIMEOUT_CLIENT);
			toServer = new ObjectOutputStream(sock.getOutputStream());
			fromServer = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
	}

	/**
	 * Fordert eine vollständige, alphabetisch sortierte Liste aller Lagerbestände vom Server an.
	 * * @return Eine Liste aller Lagerbestände oder null im Fehlerfall.
	 */
	public List<LagerBestand> getBestand() {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.STATISTIK);
			deo.setCommand(Command.BESTAND);
			toServer.writeObject(deo);
			return Cast.safeListCast(((WarehouseReturnDEO) fromServer.readObject()).getData(), LagerBestand.class);
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
		return null;
	}

	/**
	 * Fordert die Top 10 der Lagerbestände vom Server an (die Produkte mit der höchsten Stückzahl).
	 * * @return Eine Liste der Top 10 Bestände.
	 */
	public List<LagerBestand> getTOP10Bestand() {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.STATISTIK);
			deo.setCommand(Command.TOP);
			toServer.writeObject(deo);
			return Cast.safeListCast(((WarehouseReturnDEO) fromServer.readObject()).getData(), LagerBestand.class);
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
		return null;
	}

	/**
	 * Fordert die Low 10 der Lagerbestände vom Server an (die Produkte mit der niedrigsten Stückzahl).
	 * Dient oft dazu, Artikel zu finden, die dringend nachbestellt werden müssen.
	 * * @return Eine Liste der Low 10 Bestände.
	 */
	public List<LagerBestand> getLOW10Bestand() {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.STATISTIK);
			deo.setCommand(Command.LOW);
			toServer.writeObject(deo);
			return Cast.safeListCast(((WarehouseReturnDEO) fromServer.readObject()).getData(), LagerBestand.class);
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
		return null;
	}

	/**
	 * Sendet einen Suchbegriff an den Server, um spezifische Lagerbestände zu filtern.
	 * * @param search Der gesuchte Begriff (z. B. ein Produktname oder Hersteller).
	 * @return Eine Liste der Bestände, auf die der Suchbegriff passt.
	 */
	public List<LagerBestand> search(String search) {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERBESTAND);
			deo.setCommand(Command.SEARCH);
			deo.setData(search);
			toServer.writeObject(deo);
			WarehouseReturnDEO d = ((WarehouseReturnDEO) fromServer.readObject());
			return Cast.safeListCast(d.getData(), LagerBestand.class);
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
		return null;
	}

	/**
	 * Sendet geänderte Informationen eines Lagerbestands (z. B. eine korrigierte Menge)
	 * an den Server, um diese dort dauerhaft in der Datenbank zu speichern.
	 * * @param mod Der zu aktualisierende Lagerbestand.
	 */
	public void updateLagerBestand(LagerBestand mod) {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERBESTAND);
			deo.setCommand(Command.UPDATE);
			deo.setData(mod);
			toServer.writeObject(deo);
			fromServer.readObject();
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
	}

	/**
	 * Ermittelt, welche Lagerplätze im System aktuell noch komplett leer sind.
	 * Dazu lädt die Methode erst alle existierenden Plätze und anschließend alle belegten Bestände.
	 * Belegte Plätze werden aus der Liste gefiltert, sodass nur die freien Plätze übrig bleiben.
	 * * @return Ein Array mit allen ungenutzten Lagerplätzen.
	 */
	public LagerPlatz[] getFreeLagerPlatz() {
		try {
			// 1. Alle verfügbaren Lagerplätze abfragen
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERPLATZ);
			deo.setCommand(Command.LIST);
			toServer.writeObject(deo);
			WarehouseReturnDEO d = ((WarehouseReturnDEO) fromServer.readObject());
			List<LagerPlatz> lager = Cast.safeListCast(d.getData(), LagerPlatz.class);

			// 2. Alle aktuellen Lagerbestände abfragen
			deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERBESTAND);
			deo.setCommand(Command.LIST);
			toServer.writeObject(deo);
			d = ((WarehouseReturnDEO) fromServer.readObject());
			List<LagerBestand> bestand = Cast.safeListCast(d.getData(), LagerBestand.class);

			// 3. Filtern: Welcher Platz ist in keinem Bestand vermerkt?
			List<LagerPlatz> removeCandidates = new ArrayList<>();
			for (LagerPlatz p : lager) {
				for (LagerBestand b : bestand) {
					if (b.getLagerplatz_id().getId() == p.getId()) {
						removeCandidates.add(p);
						break;
					}
				}
			}
			lager.removeAll(removeCandidates);

			return lager.toArray(new LagerPlatz[0]);
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
		return null;
	}

	/**
	 * Trennt die Netzwerkverbindung zum Server ordnungsgemäß und schließt die Datenkanäle.
	 */
	public void close() {
		try {
			if (fromServer != null)
				fromServer.close();
			if (toServer != null)
				toServer.close();
			if (sock != null)
				sock.close();
		} catch (IOException ignored) {
		}
	}

	/**
	 * Legt ein komplett neues Produkt an und verknüpft es direkt mit einem Lagerplatz (Bestand).
	 * Da das Produkt für den Bestand eine Datenbank-ID braucht, wird das Produkt zuerst gespeichert,
	 * dann mit ID vom Server zurückgeholt und schließlich dem neuen Bestand zugewiesen und gesichert.
	 * * @param p Das neu anzulegende Produkt.
	 * @param l Der neue Lagerbestand, auf dem das Produkt liegen soll.
	 * @return true, wenn der gesamte Vorgang erfolgreich war, andernfalls false.
	 */
	public boolean addProdukt(Produkt p, LagerBestand l) {
		try {
			// 1. Neues Produkt speichern
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.PRODUKT);
			deo.setCommand(Command.ADD);
			deo.setData(p);
			toServer.writeObject(deo);
			fromServer.readObject();

			// 2. Gespeichertes Produkt (inklusive generierter ID) wieder abrufen
			WarehouseReturnDEO d;
			deo = new WarehouseDEO();
			deo.setZone(Zone.PRODUKT);
			deo.setCommand(Command.GETBYMODEL);
			deo.setData(p);
			toServer.writeObject(deo);
			d = ((WarehouseReturnDEO) fromServer.readObject());
			Produkt pr = Cast.safeCast(d.getData(), Produkt.class);

			// 3. Das vollständige Produkt dem Bestand zuweisen und diesen speichern
			l.setProdukt_id(pr);
			deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERBESTAND);
			deo.setCommand(Command.ADD);
			deo.setData(l);
			toServer.writeObject(deo);
			d = ((WarehouseReturnDEO) fromServer.readObject());

			return d.getStatus().equals(Status.OK);
		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
			return false;
		}

	}

	/**
	 * Sendet einen speziellen Befehl an den Server, um diesen komplett herunterzufahren.
	 * Wird in der Regel nur für Wartungszwecke oder von einem Admin aufgerufen.
	 */
	public void closeServer() {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.GENERAL);
			deo.setCommand(Command.END);
			toServer.writeObject(deo);
			WarehouseReturnDEO d = ((WarehouseReturnDEO) fromServer.readObject());

			// Sendet ein zweites END-Kommando, um sicherzugehen,
			// dass alle Threads des Servers beendet werden.
			if (d.getStatus() == Status.OK) {
				sock = new Socket(Info.NAME_SERVER, Info.PORT_SERVER);
				sock.setSoTimeout(Info.TIMEOUT_CLIENT);
				toServer = new ObjectOutputStream(sock.getOutputStream());
				fromServer = new ObjectInputStream(sock.getInputStream());
				deo = new WarehouseDEO();
				deo.setZone(Zone.GENERAL);
				deo.setCommand(Command.END);
				toServer.writeObject(deo);
				fromServer.readObject();
			}
		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler bei der Kommunikation mit dem Server", e);
		}
	}

}