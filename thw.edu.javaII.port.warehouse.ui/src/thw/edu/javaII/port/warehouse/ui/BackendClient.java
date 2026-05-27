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

    /**
     * Baut eine neue Socket-Verbindung zum konfigurierten Server auf und öffnet die
     * Datenströme (Streams) für das Senden und Empfangen von Objekten.
     * * @throws Exception Wenn der Server nicht erreichbar ist oder die Verbindung fehlschlägt.
     */
    public BackendClient() throws Exception {
        sock = new Socket(Info.NAME_SERVER, Info.PORT_SERVER);
        sock.setSoTimeout(Info.TIMEOUT_CLIENT);
        toServer = new ObjectOutputStream(sock.getOutputStream());
        fromServer = new ObjectInputStream(sock.getInputStream());
    }

    /**
     * Zentrale Hilfsmethode für den Netzwerkverkehr. Sendet eine Anfrage an den Server
     * und wartet blockierend auf die Antwort.
     * * @param request Das fertig konfigurierte Datenpaket (Zone, Command, ggf. Payload).
     * @return Die Antwort des Servers inklusive Statuscode und angeforderten Daten.
     * @throws Exception Bei Kommunikationsfehlern während des Sendens oder Empfangens.
     */
    private WarehouseReturnDEO sendRequest(WarehouseDEO request) throws Exception {
        toServer.writeObject(request);
        return (WarehouseReturnDEO) fromServer.readObject();
    }

    // ==========================================
    // BEREICH: PRODUKT
    // ==========================================

    /**
     * Ruft eine vollständige Liste aller im System hinterlegten Produkte ab.
     * * @return Eine Liste von Produkten. Ist leer, falls keine existieren oder ein Fehler auftrat.
     * @throws Exception Bei Netzwerk- oder Konvertierungsfehlern.
     */
    public List<Produkt> getAllProdukte() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.LIST);

        var ret = sendRequest(deo);

        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            var list = Cast.safeListCast(ret.getData(), Produkt.class);
            return list != null ? list : List.of(); // Das fängt den Null-Wert ab!
        }
        return List.of();
    }

    /**
     * Sendet ein neues Produkt an den Server, um es in der Datenbank zu speichern.
     * * @param produkt Das neu anzulegende Produkt.
     * @return true, wenn das Speichern erfolgreich war, sonst false.
     * @throws Exception Bei Kommunikationsfehlern.
     */
    public boolean addProdukt(Produkt produkt) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.ADD);
        deo.setData(produkt);

        return sendRequest(deo).getStatus() == Status.OK;
    }

    /**
     * Aktualisiert die Daten eines bereits existierenden Produkts auf dem Server.
     * * @param produkt Das veränderte Produkt (die ID muss zwingend übereinstimmen).
     * @return true bei erfolgreichem Update, sonst false.
     * @throws Exception Bei Kommunikationsfehlern.
     */
    public boolean updateProdukt(Produkt produkt) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.UPDATE);
        deo.setData(produkt);

        return sendRequest(deo).getStatus() == Status.OK;
    }

    /**
     * Löscht ein Produkt anhand seiner ID aus der Datenbank des Servers.
     * * @param produktId Die ID des zu löschenden Produkts.
     * @return true, wenn der Löschvorgang erfolgreich war, sonst false.
     * @throws Exception Bei Kommunikationsfehlern.
     */
    public boolean deleteProdukt(int produktId) throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.PRODUKT);
        deo.setCommand(Command.DELETE);
        deo.setData(new Produkt(produktId, null, null, 0.0));

        return sendRequest(deo).getStatus() == Status.OK;
    }

    // ==========================================
    // BEREICH: LAGER
    // ==========================================

    /**
     * Ruft eine Liste aller Hauptlager (Standorte) vom Server ab.
     * * @return Eine Liste aller Lager.
     * @throws Exception Bei Kommunikationsfehlern.
     */
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

    // ==========================================
    // BEREICH: LAGERPLATZ
    // ==========================================

    /**
     * Ruft eine Liste aller spezifischen Lagerplätze (z. B. Regalfächer) ab.
     * * @return Eine Liste aller Lagerplätze im System.
     * @throws Exception Bei Kommunikationsfehlern.
     */
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

    // ==========================================
    // BEREICH: LAGERBESTAND
    // ==========================================

    /**
     * Ruft eine Übersicht ab, welches Produkt in welcher Menge auf welchem Lagerplatz liegt.
     * * @return Die Liste der aktuellen Lagerbestände.
     * @throws Exception Bei Kommunikationsfehlern.
     */
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

    // ==========================================
    // VERBINDUNG SCHLIESSEN
    // ==========================================

    /**
     * Schließt alle Ein- und Ausgabeströme sowie die Socket-Verbindung sauber ab,
     * um Netzwerkressourcen freizugeben.
     */
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

    /**
     * Authentifiziert einen Kassierer am System anhand seiner Mitarbeiternummer und PIN.
     * * @param nummer Die Identifikationsnummer des Kassierers.
     * @param pin Die geheime PIN.
     * @return Das Kassierer-Objekt bei erfolgreichem Login, andernfalls null.
     * @throws Exception Bei Kommunikationsfehlern.
     */
    public thw.edu.javaII.port.warehouse.model.Kassierer loginKassierer(int nummer, String pin) throws Exception {
        var k = new thw.edu.javaII.port.warehouse.model.Kassierer();
        k.setNummer(nummer);
        k.setPin(pin);

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

    /**
     * Sucht auf dem Server nach einem bestimmten Produkt anhand seiner Artikelnummer.
     * (Wird von der Kasse beim Scannen/Eingeben eines Artikels verwendet).
     * * @param id Die Artikel-ID.
     * @return Das entsprechende Produkt-Objekt oder null, wenn es nicht existiert.
     * @throws Exception Bei Kommunikationsfehlern.
     */
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

    /**
     * Stößt die Initialisierung der Demo-Datenbank auf dem Server an.
     * * @throws Exception Bei Kommunikationsfehlern.
     */
    public void initDemoData() throws Exception {
        var deo = new thw.edu.javaII.port.warehouse.model.deo.WarehouseDEO();
        deo.setZone(thw.edu.javaII.port.warehouse.model.deo.Zone.INIT);
        deo.setCommand(thw.edu.javaII.port.warehouse.model.deo.Command.INIT);

        var ret = sendRequest(deo);
        System.out.println("Server-Nachricht nach Init: " + ret.getMessage());
    }

    /**
     * Bucht einen abgeschlossenen Bezahlvorgang (Kassenzettel inklusive aller Positionen)
     * im Backend-System.
     * * @param zettel Das Kassenzettel-Objekt mit allen Transaktionsdaten.
     * @throws Exception Wenn die Verbuchung auf Serverseite fehlschlägt (inkl. Fehlermeldung).
     */
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

    /**
     * Ruft die Basisdaten zur Vorbereitung eines Kassenabschlusses ab.
     * Liefert ein Array zurück: Index 0 = Bargeldbestand beim letzten Abschluss,
     * Index 1 = Summe der Bareinnahmen seit dem letzten Abschluss.
     * * @return Ein double-Array mit den relevanten Berechnungsgrößen für den Soll-Bestand.
     * @throws Exception Bei Kommunikationsfehlern.
     */
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

    /**
     * Speichert einen fertiggestellten Kassenabschluss (Schichtende) dauerhaft im Backend.
     * * @param abschluss Das Kassenabschluss-Objekt (Soll, Ist, Differenz, Kassierer).
     * @throws Exception Wenn das Speichern serverseitig fehlschlägt.
     */
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

    /**
     * Ruft die gesamte Historie aller bisher getätigten Verkäufe (Kassenzettel) ab.
     * * @return Liste aller Kassenzettel.
     * @throws Exception Bei Kommunikationsfehlern.
     */
    public List<thw.edu.javaII.port.warehouse.model.Kassenzettel> getAllKassenzettel() throws Exception {
        var deo = new WarehouseDEO();
        deo.setZone(Zone.KASSE);
        deo.setCommand(Command.KASSENZETTEL_LISTE);
        var ret = sendRequest(deo);

        if (ret.getStatus() == Status.OK && ret.getData() != null) {
            return Cast.safeListCast(ret.getData(), thw.edu.javaII.port.warehouse.model.Kassenzettel.class);
        }
        return List.of();
    }

    /**
     * Authentifiziert einen Kassierer am System und übermittelt gleichzeitig den
     * physischen Startbestand (Wechselgeld) der Kasse für die neue Schicht.
     *
     * @param nummer Die Identifikationsnummer des Kassierers.
     * @param pin Die geheime PIN.
     * @param startBestand Das gezählte Wechselgeld in der Kasse vor dem ersten Verkauf.
     * @return Das Kassierer-Objekt bei erfolgreichem Login, andernfalls null.
     * @throws Exception Bei Kommunikationsfehlern.
     */
    public thw.edu.javaII.port.warehouse.model.Kassierer loginKassierer(int nummer, String pin, double startBestand) throws Exception {
        var k = new thw.edu.javaII.port.warehouse.model.Kassierer();
        k.setNummer(nummer);
        k.setPin(pin);
        k.setStartBestand(startBestand); // <-- NEU: Übergabe an den Server

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
}