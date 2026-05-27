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

public class Communicator {
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Communicator.class.getName());
	private ObjectInputStream fromServer;
	private ObjectOutputStream toServer;
	private Socket sock;

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

	public LagerPlatz[] getFreeLagerPlatz() {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERPLATZ);
			deo.setCommand(Command.LIST);
			toServer.writeObject(deo);
			WarehouseReturnDEO d = ((WarehouseReturnDEO) fromServer.readObject());
			List<LagerPlatz> lager = Cast.safeListCast(d.getData(), LagerPlatz.class);
			deo = new WarehouseDEO();
			deo.setZone(Zone.LAGERBESTAND);
			deo.setCommand(Command.LIST);
			toServer.writeObject(deo);
			d = ((WarehouseReturnDEO) fromServer.readObject());
			List<LagerBestand> bestand = Cast.safeListCast(d.getData(), LagerBestand.class);
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

	public boolean addProdukt(Produkt p, LagerBestand l) {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.PRODUKT);
			deo.setCommand(Command.ADD);
			deo.setData(p);
			toServer.writeObject(deo);
			fromServer.readObject();
			WarehouseReturnDEO d;
			deo = new WarehouseDEO();
			deo.setZone(Zone.PRODUKT);
			deo.setCommand(Command.GETBYMODEL);
			deo.setData(p);
			toServer.writeObject(deo);
			d = ((WarehouseReturnDEO) fromServer.readObject());
			Produkt pr = Cast.safeCast(d.getData(), Produkt.class);
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

	public void closeServer() {
		try {
			WarehouseDEO deo = new WarehouseDEO();
			deo.setZone(Zone.GENERAL);
			deo.setCommand(Command.END);
			toServer.writeObject(deo);
			WarehouseReturnDEO d = ((WarehouseReturnDEO) fromServer.readObject());
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
