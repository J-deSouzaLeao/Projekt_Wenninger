package thw.edu.javaII.port.warehouse.server.data;

import java.util.List;

import thw.edu.javaII.port.warehouse.model.DemoModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.model.Kassierer;
import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.model.Kassenabschluss;
import thw.edu.javaII.port.warehouse.model.exception.NegativeStockException;

/**
 * Diese Schnittstelle (Interface) definiert alle notwendigen Datenbank- und Speicheroperationen des Systems.
 * Sie gibt als feste Vorlage vor, welche Funktionen (z. B. Hinzufügen, Löschen, Suchen)
 * für die verschiedenen Datenmodelle zwingend implementiert werden müssen, egal welche Datenbanktechnologie
 * im Hintergrund letztendlich verwendet wird.
 * * @author juan.de.souza.leao
 */
public interface IStorage {

	/**
	 * Befüllt die Datenbank einmalig mit einer Startliste an Lagern (z. B. aus der Initialisierung).
	 * @param list Die Liste der anzulegenden Lager.
	 */
	void initLager(List<Lager> list);

	/**
	 * Speichert ein neues Lager in der Datenbank.
	 * @param model Das hinzuzufügende Lager.
	 */
	void addLager(Lager model);

	/**
	 * Aktualisiert die Daten eines bereits bestehenden Lagers.
	 * @param model Das Lager mit den neuen Informationen.
	 */
	void updateLager(Lager model);

	/**
	 * Löscht ein Lager unwiderruflich aus der Datenbank.
	 * @param model Das zu löschende Lager.
	 */
	void deleteLager(Lager model);

	/**
	 * Ruft eine Liste aller gespeicherten Lager ab.
	 * @return Eine Liste aller Lager.
	 */
	List<Lager> getLagers();

	/**
	 * Befüllt die Datenbank einmalig mit einer Startliste an Lagerplätzen.
	 * @param list Die Liste der anzulegenden Lagerplätze.
	 */
	void initLagerPlatz(List<LagerPlatz> list);

	/**
	 * Speichert einen neuen Lagerplatz in der Datenbank.
	 * @param model Der hinzuzufügende Lagerplatz.
	 */
	void addLagerPlatz(LagerPlatz model);

	/**
	 * Aktualisiert die Daten eines bereits bestehenden Lagerplatzes.
	 * @param model Der Lagerplatz mit den neuen Informationen.
	 */
	void updateLagerPlatz(LagerPlatz model);

	/**
	 * Löscht einen Lagerplatz aus der Datenbank.
	 * @param model Der zu löschende Lagerplatz.
	 */
	void deleteLagerPlatz(LagerPlatz model);

	/**
	 * Ruft eine Liste aller gespeicherten Lagerplätze ab.
	 * @return Eine Liste aller Lagerplätze.
	 */
	List<LagerPlatz> getLagerPlatzs();

	/**
	 * Befüllt die Datenbank einmalig mit einer Startliste an Lagerbeständen.
	 * @param list Die Liste der anzulegenden Bestände.
	 */
	void initLagerBestand(List<LagerBestand> list);

	/**
	 * Speichert einen neuen Lagerbestand (Verknüpfung von Produkt und Platz).
	 * @param model Der neue Bestand.
	 */
	void addLagerBestand(LagerBestand model);

	/**
	 * Aktualisiert die Menge oder Zuweisung eines bestehenden Lagerbestands.
	 * @param model Der zu ändernde Bestand.
	 */
	void updateLagerBestand(LagerBestand model);

	/**
	 * Löscht einen Lagerbestand aus dem System.
	 * @param model Der zu löschende Bestand.
	 */
	void deleteLagerBestand(LagerBestand model);

	/**
	 * Ruft eine Liste aller gespeicherten Lagerbestände ab.
	 * @return Eine Liste der kompletten Lagerbestände.
	 */
	List<LagerBestand> getLagerBestands();

	/**
	 * Befüllt die Datenbank einmalig mit einer Startliste an Produkten.
	 * @param list Die Liste der anzulegenden Produkte.
	 */
	void initProdukt(List<Produkt> list);

	/**
	 * Speichert ein neues Produkt in der Datenbank.
	 * @param model Das hinzuzufügende Produkt.
	 */
	void addProdukt(Produkt model);

	/**
	 * Sucht ein ganz bestimmtes Produkt anhand eines übergebenen Modells (meist über die ID).
	 * @param model Das Modell, das die Suchkriterien (z. B. ID) enthält.
	 * @return Das gefundene Produkt aus der Datenbank oder null.
	 */
	Produkt getProduktByModel(Produkt model);

	/**
	 * Aktualisiert die Stammdaten eines bereits bestehenden Produkts (z. B. Preisänderung).
	 * @param model Das Produkt mit den aktualisierten Daten.
	 */
	void updateProdukt(Produkt model);

	/**
	 * Löscht ein Produkt aus der Datenbank.
	 * @param model Das zu löschende Produkt.
	 */
	void deleteProdukt(Produkt model);

	/**
	 * Ruft eine Liste aller verfügbaren Produkte ab.
	 * @return Eine Liste aller Produkte.
	 */
	List<Produkt> getProdukts();

	/**
	 * Befüllt die Datenbank mit Test-Demodaten.
	 * @param list Die Liste der Demo-Objekte.
	 */
	void initDemo(List<DemoModel> list);

	/**
	 * Fügt der Datenbank ein neues Demo-Objekt hinzu.
	 * @param model Das neue Demo-Objekt.
	 */
	void addDemo(DemoModel model);

	// ==========================================
	// Kassen-Erweiterungen (Terminal & Verkauf)
	// ==========================================

	/**
	 * Erstellt (falls nicht vorhanden) die benötigten Datenbanktabellen für das Kassen-System
	 * und legt bei Bedarf einen Standard-Kassierer an.
	 */
	void initKassenTabellen();

	/**
	 * Sucht einen Mitarbeiter für den Login am Kassenterminal.
	 * @param nummer Die Personal- oder Loginnummer des Kassierers.
	 * @return Das gefundene Kassierer-Objekt oder null, falls die Nummer falsch ist.
	 */
	Kassierer getKassiererByNummer(int nummer);

	/**
	 * Speichert einen abgeschlossenen Einkauf inklusive aller gekauften Positionen fest in der Datenbank.
	 * @param kassenzettel Der zu speichernde Kassenbon.
	 */
	void saveKassenzettel(Kassenzettel kassenzettel);

	/**
	 * Speichert einen durchgeführten Kassenabschluss (Schicht- oder Tagesende) in der Datenbank.
	 * @param abschluss Der zu speichernde Abschluss.
	 */
	void saveKassenabschluss(Kassenabschluss abschluss);

	/**
	 * Zieht verkaufte Ware direkt vom Lagerbestand ab.
	 * Verhindert automatisch, dass mehr verkauft wird, als physisch im Lager liegt (Überverkauf).
	 * @param produktId Die ID des verkauften Produkts.
	 * @param anzahl Die Menge, die abgezogen werden soll.
	 * @throws NegativeStockException Wird ausgelöst, falls der Bestand unter 0 fallen würde.
	 */
	void reduceLagerbestand(int produktId, int anzahl) throws NegativeStockException;

	/**
	 * Holt eine Historie aller jemals gespeicherten Kassenzettel aus der Datenbank.
	 * @return Eine Liste aller Kassenbons.
	 */
	List<Kassenzettel> getAllKassenzettel();

	// ==========================================
	// BEREICH: KASSIERER / PERSONAL
	// ==========================================

	/**
	 * Ruft eine Liste aller im System hinterlegten Kassierer ab.
	 * @return Eine Liste von Kassierer-Objekten.
	 */
	List<Kassierer> getAllKassierer();

	/**
	 * Speichert einen neuen Kassierer in der Datenbank.
	 * @param k Das Kassierer-Objekt, das gespeichert werden soll.
	 */
	void addKassierer(Kassierer k);

	/**
	 * Aktualisiert die Daten eines bestehenden Kassierers (z. B. PIN-Änderung oder Manager-Status).
	 * @param k Das Kassierer-Objekt mit den aktualisierten Daten.
	 */
	void updateKassierer(Kassierer k);

	/**
	 * Löscht einen Kassierer anhand seiner eindeutigen Mitarbeiternummer.
	 * @param nummer Die Identifikationsnummer des Kassierers.
	 */
	void deleteKassierer(int nummer);

	/**
	 * Ermittelt die Top 10 Lagerbestände mit der höchsten Kapitalbindung.
	 */
	List<LagerBestand> getKapitalbindungBestand();

	/**
	 * Ermittelt alle Lagerbestände, die einen kritischen Meldebestand erreicht haben.
	 */
	List<LagerBestand> getKritischerBestand();
}