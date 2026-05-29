package thw.edu.javaII.port.warehouse.ui;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import thw.edu.javaII.port.warehouse.model.*;
import thw.edu.javaII.port.warehouse.model.common.Cast;
import thw.edu.javaII.port.warehouse.model.common.Info;
import thw.edu.javaII.port.warehouse.model.deo.Command;
import thw.edu.javaII.port.warehouse.model.deo.Status;
import thw.edu.javaII.port.warehouse.model.deo.WarehouseDEO;
import thw.edu.javaII.port.warehouse.model.deo.WarehouseReturnDEO;
import thw.edu.javaII.port.warehouse.model.deo.Zone;

/**
 * Der BackendClient ist das zentrale Bindeglied (Proxy) zwischen der grafischen Benutzeroberfläche (Client)
 * und dem Backend-Server. Er kapselt die gesamte Netzwerklogik. Lokale Methodenaufrufe (wie z. B. addProdukt)
 * werden hier in standardisierte Datentransferobjekte (DEOs) verpackt, über eine Socket-Verbindung
 * an den Server gesendet, und die Server-Antworten werden wieder in nutzbare Java-Objekte übersetzt.
 */
public class BackendClient {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(BackendClient.class.getName());
    private final ObjectInputStream fromServer;
    private final ObjectOutputStream toServer;
    private final Socket sock;

    public BackendClient() throws Exception {
        sock = new Socket(Info.NAME_SERVER, Info.PORT_SERVER);
        sock.setSoTimeout(Info.TIMEOUT_CLIENT);
        toServer = new ObjectOutputStream(sock.getOutputStream());
        fromServer = new ObjectInputStream(sock.getInputStream());
    }

    private WarehouseReturnDEO sendRequest(WarehouseDEO request) throws Exception {
        toServer.writeObject(request);
        toServer.reset();
        toServer.flush();
        return (WarehouseReturnDEO) fromServer.readObject();
    }

    // ==========================================
    // BEREICH: PRODUKT, LAGER, BESTAND (Gekürzt für Übersichtlichkeit, bleibt identisch)
    // ==========================================

    public List<Produkt> getAllProdukte() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.LIST);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            var list = Cast.safeListCast(ret.getData(), Produkt.class);
            return list != null ? list : List.of();
        }
        return List.of();
    }

    public boolean addProdukt(Produkt produkt) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.ADD);
        deo.setData(produkt);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean updateProdukt(Produkt produkt) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.UPDATE);
        deo.setData(produkt);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean deleteProdukt(int produktId) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.DELETE);
        deo.setData(new Produkt(produktId, null, null, 0.0));
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public List<Lager> getAllLager() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGER);
        deo.setCommand(Command.LIST);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeListCast(ret.getData(), Lager.class);
        }
        return List.of();
    }

    public boolean addLager(Lager lager) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGER);
        deo.setCommand(Command.ADD);
        deo.setData(lager);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean updateLager(Lager lager) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGER);
        deo.setCommand(Command.UPDATE);
        deo.setData(lager);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean deleteLager(int lagerId) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGER);
        deo.setCommand(Command.DELETE);
        deo.setData(new Lager(lagerId, null, null, null));
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public List<LagerPlatz> getAllLagerPlaetze() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERPLATZ);
        deo.setCommand(Command.LIST);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeListCast(ret.getData(), LagerPlatz.class);
        }
        return List.of();
    }

    public boolean addLagerPlatz(LagerPlatz lagerPlatz) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERPLATZ);
        deo.setCommand(Command.ADD);
        deo.setData(lagerPlatz);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean updateLagerPlatz(LagerPlatz lagerPlatz) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERPLATZ);
        deo.setCommand(Command.UPDATE);
        deo.setData(lagerPlatz);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean deleteLagerPlatz(int lagerPlatzId) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERPLATZ);
        deo.setCommand(Command.DELETE);
        deo.setData(new LagerPlatz(lagerPlatzId, null, 0, null));
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public List<LagerBestand> getAllLagerBestaende() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERBESTAND);
        deo.setCommand(Command.LIST);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeListCast(ret.getData(), LagerBestand.class);
        }
        return List.of();
    }

    public boolean addLagerBestand(LagerBestand lagerBestand) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERBESTAND);
        deo.setCommand(Command.ADD);
        deo.setData(lagerBestand);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean updateLagerBestand(LagerBestand lagerBestand) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERBESTAND);
        deo.setCommand(Command.UPDATE);
        deo.setData(lagerBestand);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean deleteLagerBestand(int lagerBestandId) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.LAGERBESTAND);
        deo.setCommand(Command.DELETE);
        deo.setData(new LagerBestand(lagerBestandId, 0, null, null));
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public void close() {
        try {
            if (fromServer != null) fromServer.close();
            if (toServer != null) toServer.close();
            if (sock != null) sock.close();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Fehler beim Schließen der Serververbindung", e);
        }
    }

    // ==========================================
    // BEREICH: KASSE
    // ==========================================

    public Produkt getProduktById(int id) throws Exception {
        var p = new Produkt(id, null, null, 0.0);
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.GETBYMODEL);
        deo.setData(p);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeCast(ret.getData(), Produkt.class);
        }
        return null;
    }

    public void initDemoData() throws Exception {
        var deo = new thw.edu.javaII.port.warehouse.model.deo.WarehouseDEO();
        deo.setZone(thw.edu.javaII.port.warehouse.model.deo.Zone.INIT);
        deo.setCommand(thw.edu.javaII.port.warehouse.model.deo.Command.INIT);
        var ret = sendRequest(deo);
        System.out.println("Server-Nachricht nach Init: " + ret.getMessage());
    }

    public void kassenzettelBuchen(Kassenzettel zettel) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KAUFEN);
        deo.setData(zettel);
        var ret = sendRequest(deo);
        if (ret.getStatus() != Status.OK) {
            throw new Exception(ret.getMessage());
        }
    }

    public double[] getAbschlussDaten() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.ABSCHLUSS_LADEN);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return (double[]) ret.getData();
        }
        return new double[]{0.0, 0.0};
    }

    public void saveKassenabschluss(thw.edu.javaII.port.warehouse.model.Kassenabschluss abschluss) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.ABSCHLUSS_SPEICHERN);
        deo.setData(abschluss);
        var ret = sendRequest(deo);
        if (ret.getStatus() != Status.OK) {
            throw new Exception(ret.getMessage());
        }
    }

    public List<thw.edu.javaII.port.warehouse.model.Kassenzettel> getAllKassenzettel() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KASSENZETTEL_LIST); // <-- BUGFIX: Das 'E' wurde entfernt
        var ret = sendRequest(deo);

        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeListCast(ret.getData(), thw.edu.javaII.port.warehouse.model.Kassenzettel.class);
        }
        return List.of();
    }

    public thw.edu.javaII.port.warehouse.model.Kassierer loginKassierer(int nummer, String pin, double startBestand) throws Exception {
        var k = new thw.edu.javaII.port.warehouse.model.Kassierer();
        k.setNummer(nummer);
        k.setPin(pin);
        k.setStartBestand(startBestand);

        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.LOGIN);
        deo.setData(k);
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeCast(ret.getData(), thw.edu.javaII.port.warehouse.model.Kassierer.class);
        }
        return null;
    }

    // ==========================================
    // BEREICH: KASSIERER-VERWALTUNG
    // ==========================================

    public List<thw.edu.javaII.port.warehouse.model.Kassierer> getAllKassierer() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KASSIERER_LIST); // <-- BUGFIX: Das 'E' wurde entfernt
        var ret = sendRequest(deo);
        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeListCast(ret.getData(), thw.edu.javaII.port.warehouse.model.Kassierer.class);
        }
        return List.of();
    }

    /**
     * Legt einen neuen Kassierer im System an.
     * Nutzt System.out.println() um den Server-Status zur Fehleranalyse auszuspucken.
     */
    public boolean addKassierer(thw.edu.javaII.port.warehouse.model.Kassierer k) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KASSIERER_ADD);
        deo.setData(k);

        WarehouseReturnDEO ret = sendRequest(deo);

        // DEBUG-AUSGABE: Zeigt dir in der Konsole an, ob der Server fälschlicherweise "OK" meldet
        System.out.println("SERVER ANTWORT (Kassierer anlegen): " + ret.getStatus() + " | Nachricht: " + ret.getMessage());

        return ret.getStatus() == Status.OK;
    }

    public boolean updateKassierer(thw.edu.javaII.port.warehouse.model.Kassierer k) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KASSIERER_UPDATE);
        deo.setData(k);
        return sendRequest(deo).getStatus() == Status.OK;
    }

    public boolean deleteKassierer(int nummer) throws Exception {
        var k = new thw.edu.javaII.port.warehouse.model.Kassierer();
        k.setNummer(nummer);
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KASSIERER_DELETE);
        deo.setData(k);
        return sendRequest(deo).getStatus() == Status.OK;
    }
}