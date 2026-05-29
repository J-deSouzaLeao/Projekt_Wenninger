package thw.edu.javaII.port.warehouse.server.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import thw.edu.javaII.port.warehouse.init.Initizilaizer;
import thw.edu.javaII.port.warehouse.model.DemoModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.model.Kassierer;
import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.model.Kassenabschluss;
import thw.edu.javaII.port.warehouse.model.KassenzettelPosition;
import thw.edu.javaII.port.warehouse.model.exception.NegativeStockException;

/**
 * Diese Klasse ist das Herzstück der Datenspeicherung. Sie implementiert das IStorage-Interface
 * und baut die tatsächliche Verbindung zu unserer lokalen SQLite-Datenbank auf.
 * Hier werden alle SQL-Befehle ausgeführt, um Daten zu lesen, zu schreiben oder zu löschen.
 * * @author juan.de.souza.leao
 */
public class Database implements IStorage {

	private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
	private static final String driverClass = "org.sqlite.JDBC";
	private static final String dbUrl = "jdbc:sqlite:warehouse.sqlite";
	private final Initizilaizer init;

	/**
	 * Konstruktor der Datenbank-Klasse.
	 * Lädt den Datenbanktreiber und stößt die Tabellen-Erstellung für das Kassensystem an.
	 * @throws Exception Wenn der Treiber nicht gefunden wird.
	 */
	public Database() throws Exception {
		try {
			Class.forName(driverClass);
			init = new Initizilaizer();
			initKassenTabellen();
		} catch (ClassNotFoundException e) {
			throw new Exception(e);
		}
	}

	/**
	 * Erstellt die Lagertabelle (falls sie noch nicht existiert) oder leert sie
	 * und füllt sie anschließend mit den Basisdaten aus dem Initializer.
	 * @param list Die initiale Liste an Lagern (wird hier indirekt über den Initializer gesteuert).
	 */
	@Override
	public void initLager(List<Lager> list) {
		if (tableDoesNotExist("LAGER")) {
			try (Connection con = DriverManager.getConnection(dbUrl);
			     Statement st = con.createStatement()) {
				String sql = "CREATE TABLE IF NOT EXISTS LAGER (id integer PRIMARY KEY, name text NOT NULL, ort text, art text)";
				st.executeUpdate(sql);
				for (Lager mod : init.getLager()) {
					addLager(mod);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Fehler bei LAGER Init", e);
			}
		} else {
			truncateTable("LAGER");
			for (Lager mod : init.getLager()) {
				addLager(mod);
			}
		}
	}

	/**
	 * Fügt ein neues Lager zur Datenbank hinzu.
	 * @param model Das zu speichernde Lager-Objekt.
	 */
	@Override
	public void addLager(Lager model) {
		String sql = "INSERT INTO LAGER (id,name,ort,art) VALUES (?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.setString(2, model.getName());
			pstmt.setString(3, model.getOrt());
			pstmt.setString(4, model.getArt());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Aktualisiert die Daten eines bestehenden Lagers anhand seiner ID.
	 * @param model Das Lager mit den neuen Werten.
	 */
	@Override
	public void updateLager(Lager model) {
		String sql = "UPDATE LAGER SET name=?, ort=?, art=? WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, model.getName());
			pstmt.setString(2, model.getOrt());
			pstmt.setString(3, model.getArt());
			pstmt.setInt(4, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Löscht ein Lager aus der Datenbank.
	 * @param model Das zu löschende Lager.
	 */
	@Override
	public void deleteLager(Lager model) {
		String sql = "DELETE FROM LAGER WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Ruft alle gespeicherten Lager aus der Datenbank ab.
	 * @return Eine Liste aller Lager.
	 */
	@Override
	public List<Lager> getLagers() {
		List<Lager> l = new ArrayList<>();
		String sql = "SELECT * FROM LAGER";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				l.add(new Lager(rs.getInt("id"), rs.getString("name"), rs.getString("ort"), rs.getString("art")));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return l;
	}

	/**
	 * Sucht ein bestimmtes Lager anhand seiner eindeutigen ID.
	 * @param id Die Datenbank-ID des Lagers.
	 * @return Das gefundene Lager-Objekt oder ein leeres Objekt.
	 */
	public Lager getLagerById(int id) {
		Lager model = new Lager();
		String sql = "SELECT * FROM LAGER WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				model.setId(id);
				model.setName(rs.getString("name"));
				model.setOrt(rs.getString("ort"));
				model.setArt(rs.getString("art"));
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return model;
	}

	/**
	 * Erstellt oder setzt die Tabelle für Lagerplätze zurück und füllt sie mit Demodaten.
	 * @param list Die Liste der initialen Lagerplätze.
	 */
	@Override
	public void initLagerPlatz(List<LagerPlatz> list) {
		if (tableDoesNotExist("LAGERPLATZ")) {
			try (Connection con = DriverManager.getConnection(dbUrl);
			     Statement st = con.createStatement()) {
				String sql = "CREATE TABLE IF NOT EXISTS LAGERPLATZ (id integer PRIMARY KEY, name text NOT NULL, kapazitaet integer NOT NULL, lager_id integer NOT NULL)";
				st.executeUpdate(sql);
				for (LagerPlatz mod : init.getLagerplatz()) {
					addLagerPlatz(mod);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Fehler", e);
			}
		} else {
			truncateTable("LAGERPLATZ");
			for (LagerPlatz mod : init.getLagerplatz()) {
				addLagerPlatz(mod);
			}
		}
	}

	/**
	 * Speichert einen neuen Lagerplatz in der Datenbank.
	 * @param model Der neue Lagerplatz.
	 */
	@Override
	public void addLagerPlatz(LagerPlatz model) {
		String sql = "INSERT INTO LAGERPLATZ (id,name,kapazitaet,lager_id) VALUES (?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.setString(2, model.getName());
			pstmt.setInt(3, model.getKapazitaet());
			pstmt.setInt(4, model.getLager_id().getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Aktualisiert die Informationen eines bestehenden Lagerplatzes.
	 * @param model Der Lagerplatz mit aktualisierten Werten.
	 */
	@Override
	public void updateLagerPlatz(LagerPlatz model) {
		String sql = "UPDATE LAGERPLATZ SET name=?, kapazitaet=?, lager_id=? WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, model.getName());
			pstmt.setInt(2, model.getKapazitaet());
			pstmt.setInt(3, model.getLager_id().getId());
			pstmt.setInt(4, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Entfernt einen Lagerplatz aus der Datenbank.
	 * @param model Der zu löschende Lagerplatz.
	 */
	@Override
	public void deleteLagerPlatz(LagerPlatz model) {
		String sql = "DELETE FROM LAGERPLATZ WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Ruft eine Liste aller vorhandenen Lagerplätze ab.
	 * @return Die Liste der Lagerplätze.
	 */
	@Override
	public List<LagerPlatz> getLagerPlatzs() {
		List<LagerPlatz> l = new ArrayList<>();
		String sql = "SELECT * FROM LAGERPLATZ";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Lager lager_id = getLagerById(rs.getInt("lager_id"));
				l.add(new LagerPlatz(rs.getInt("id"), rs.getString("name"), rs.getInt("kapazitaet"), lager_id));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return l;
	}

	/**
	 * Sucht einen spezifischen Lagerplatz anhand seiner ID.
	 * @param id Die ID des Lagerplatzes.
	 * @return Das gefundene Lagerplatz-Objekt.
	 */
	public LagerPlatz getLagerPlatzById(int id) {
		LagerPlatz model = new LagerPlatz();
		String sql = "SELECT * FROM LAGERPLATZ WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				model.setId(id);
				model.setName(rs.getString("name"));
				model.setKapazitaet(rs.getInt("kapazitaet"));
				model.setLager_id(getLagerById(rs.getInt("lager_id")));
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return model;
	}

	/**
	 * Erstellt oder setzt die Lagerbestands-Tabelle zurück.
	 * @param list Die initialen Lagerbestände.
	 */
	@Override
	public void initLagerBestand(List<LagerBestand> list) {
		if (tableDoesNotExist("LAGERBESTAND")) {
			try (Connection con = DriverManager.getConnection(dbUrl);
			     Statement st = con.createStatement()) {
				String sql = "CREATE TABLE IF NOT EXISTS LAGERBESTAND (id integer PRIMARY KEY, anzahl integer NOT NULL, produkt_id integer NOT NULL, lagerplatz_id integer NOT NULL)";
				st.executeUpdate(sql);
				for (LagerBestand mod : init.getLagerbestand()) {
					addLagerBestand(mod);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Fehler", e);
			}
		} else {
			truncateTable("LAGERBESTAND");
			for (LagerBestand mod : init.getLagerbestand()) {
				addLagerBestand(mod);
			}
		}
	}

	/**
	 * Bucht einen neuen Lagerbestand in die Datenbank ein.
	 * @param model Der zu speichernde Lagerbestand.
	 */
	@Override
	public void addLagerBestand(LagerBestand model) {
		String sql = "INSERT INTO LAGERBESTAND (id,anzahl,produkt_id,lagerplatz_id) VALUES (?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.setInt(2, model.getAnzahl());
			pstmt.setInt(3, model.getProdukt_id().getId());
			pstmt.setInt(4, model.getLagerplatz_id().getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Aktualisiert Menge oder Zuordnung eines bestehenden Lagerbestands.
	 * @param model Der aktualisierte Lagerbestand.
	 */
	@Override
	public void updateLagerBestand(LagerBestand model) {
		String sql = "UPDATE LAGERBESTAND SET anzahl=?, produkt_id=?, lagerplatz_id=? WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getAnzahl());
			pstmt.setInt(2, model.getProdukt_id().getId());
			pstmt.setInt(3, model.getLagerplatz_id().getId());
			pstmt.setInt(4, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Entfernt einen Lagerbestand vollständig aus der Datenbank.
	 * @param model Der zu löschende Bestand.
	 */
	@Override
	public void deleteLagerBestand(LagerBestand model) {
		String sql = "DELETE FROM LAGERBESTAND WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Ruft alle System-Lagerbestände mitsamt verknüpften Produkten und Plätzen ab.
	 * @return Liste der Lagerbestände.
	 */
	@Override
	public List<LagerBestand> getLagerBestands() {
		List<LagerBestand> l = new ArrayList<>();
		String sql = "SELECT * FROM LAGERBESTAND";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				LagerPlatz lagerplatz_id = getLagerPlatzById(rs.getInt("lagerplatz_id"));
				Produkt produkt_id = getProduktById(rs.getInt("produkt_id"));
				l.add(new LagerBestand(rs.getInt("id"), rs.getInt("anzahl"), produkt_id, lagerplatz_id));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return l;
	}

	/**
	 * Ermittelt die Top 10 Lagerbestände mit der höchsten Kapitalbindung.
	 * Die Datenbank berechnet live den Gesamtwert (Menge * Stückpreis) und
	 * sortiert das Ergebnis absteigend.
	 * @return Eine Liste der 10 teuersten Lagerbestände.
	 */
	public List<LagerBestand> getKapitalbindungBestand() {
		List<LagerBestand> list = new ArrayList<>();
		String sql = "SELECT lb.* FROM LAGERBESTAND lb " +
				"JOIN PRODUKT p ON lb.produkt_id = p.id " +
				"ORDER BY (lb.anzahl * p.preis) DESC LIMIT 10";

		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				LagerPlatz lagerplatz_id = getLagerPlatzById(rs.getInt("lagerplatz_id"));
				Produkt produkt_id = getProduktById(rs.getInt("produkt_id"));
				list.add(new LagerBestand(rs.getInt("id"), rs.getInt("anzahl"), produkt_id, lagerplatz_id));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler bei der Berechnung der Kapitalbindung", e);
		}
		return list;
	}

	/**
	 * Ermittelt alle Lagerbestände, die einen kritischen Meldebestand erreicht haben.
	 * Aktuell ist der Schwellenwert auf unter 5 Stück festgelegt.
	 * @return Liste der kritischen Lagerbestände, aufsteigend sortiert nach Menge.
	 */
	public List<LagerBestand> getKritischerBestand() {
		List<LagerBestand> list = new ArrayList<>();
		String sql = "SELECT * FROM LAGERBESTAND WHERE anzahl < 5 ORDER BY anzahl ASC";

		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				LagerPlatz lagerplatz_id = getLagerPlatzById(rs.getInt("lagerplatz_id"));
				Produkt produkt_id = getProduktById(rs.getInt("produkt_id"));
				list.add(new LagerBestand(rs.getInt("id"), rs.getInt("anzahl"), produkt_id, lagerplatz_id));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der kritischen Bestände", e);
		}
		return list;
	}

	/**
	 * Initialisiert die Tabelle für die Stammdaten der Produkte.
	 * @param list Die initiale Produktliste.
	 */
	@Override
	public void initProdukt(List<Produkt> list) {
		if (tableDoesNotExist("PRODUKT")) {
			try (Connection con = DriverManager.getConnection(dbUrl);
			     Statement st = con.createStatement()) {
				String sql = "CREATE TABLE IF NOT EXISTS PRODUKT (id integer PRIMARY KEY, name text NOT NULL, hersteller text, preis real)";
				st.executeUpdate(sql);
				for (Produkt mod : init.getProdukt()) {
					addProdukt(mod);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Fehler", e);
			}
		} else {
			truncateTable("PRODUKT");
			for (Produkt mod : init.getProdukt()) {
				addProdukt(mod);
			}
		}
	}

	/**
	 * Fügt ein neues Produkt in die Datenbank ein.
	 * @param model Das Produkt.
	 */
	@Override
	public void addProdukt(Produkt model) {
		String sql = "INSERT INTO PRODUKT (id,name,hersteller,preis) VALUES (?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.setString(2, model.getName());
			pstmt.setString(3, model.getHersteller());
			pstmt.setDouble(4, model.getPreis());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Gibt ein Produkt-Objekt basierend auf der ID eines übergebenen Objekts zurück.
	 * @param mod Ein Produkt-Objekt, das als Container für die ID dient.
	 * @return Das gefundene Produkt aus der Datenbank.
	 */
	@Override
	public Produkt getProduktByModel(Produkt mod) {
		return getProduktById(mod.getId());
	}

	/**
	 * Sucht ein Produkt gezielt über seine ID.
	 * @param id Die Produkt-ID.
	 * @return Das gefundene Produkt.
	 */
	public Produkt getProduktById(int id) {
		Produkt model = new Produkt();
		String sql = "SELECT * FROM PRODUKT WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				model.setId(id);
				model.setName(rs.getString("name"));
				model.setHersteller(rs.getString("hersteller"));
				model.setPreis(rs.getDouble("preis"));
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return model;
	}

	/**
	 * Aktualisiert die Eigenschaften (z.B. Preis, Name) eines Produkts.
	 * @param model Das Produkt mit den neuen Eigenschaften.
	 */
	@Override
	public void updateProdukt(Produkt model) {
		String sql = "UPDATE PRODUKT SET name=?, hersteller=?, preis=? WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, model.getName());
			pstmt.setString(2, model.getHersteller());
			pstmt.setDouble(3, model.getPreis());
			pstmt.setInt(4, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Löscht ein Produkt aus den Stammdaten.
	 * @param model Das zu löschende Produkt.
	 */
	@Override
	public void deleteProdukt(Produkt model) {
		String sql = "DELETE FROM PRODUKT WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Ruft alle Produkte aus der Datenbank ab.
	 * @return Liste aller Produkte.
	 */
	@Override
	public List<Produkt> getProdukts() {
		List<Produkt> l = new ArrayList<>();
		String sql = "SELECT * FROM PRODUKT";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				l.add(new Produkt(rs.getInt("id"), rs.getString("name"), rs.getString("hersteller"), rs.getDouble("preis")));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return l;
	}

	/**
	 * Initialisiert die Tabelle für Demodaten.
	 * @param list Die Liste an Demo-Objekten.
	 */
	@Override
	public void initDemo(List<DemoModel> list) {
		if (tableDoesNotExist("DEMOS")) {
			try (Connection con = DriverManager.getConnection(dbUrl);
			     Statement st = con.createStatement()) {
				String sql = "CREATE TABLE IF NOT EXISTS DEMOS (id integer PRIMARY KEY, name text NOT NULL)";
				st.executeUpdate(sql);
				for (DemoModel mod : init.getDemo()) {
					addDemo(mod);
				}
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Fehler", e);
			}
		} else {
			truncateTable("DEMOS");
			for (DemoModel mod : init.getDemo()) {
				addDemo(mod);
			}
		}
	}

	/**
	 * Fügt ein Demo-Objekt in die Datenbank ein.
	 * @param model Das Demo-Objekt.
	 */
	@Override
	public void addDemo(DemoModel model) {
		String sql = "INSERT INTO DEMOS (id,name) VALUES (?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, model.getId());
			pstmt.setString(2, model.getName());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Hilfsmethode, um den gesamten Inhalt einer Tabelle zu löschen.
	 * Der Kommentar schützt vor lästigen IDE-Warnungen wegen des fehlenden WHERE.
	 * @param tableName Der Name der zu leerenden Tabelle.
	 */
	private void truncateTable(String tableName) {
		//noinspection SqlWithoutWhere
		String sql = "DELETE FROM " + tableName;
		try (Connection con = DriverManager.getConnection(dbUrl);
		     Statement st = con.createStatement()) {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Prüft anhand der Datenbank-Metadaten, ob eine Tabelle existiert.
	 * @param tableName Der zu prüfende Tabellenname.
	 * @return true, falls sie noch nicht existiert.
	 */
	private boolean tableDoesNotExist(String tableName) {
		boolean exists = true;
		try (Connection con = DriverManager.getConnection(dbUrl)) {
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(null, null, tableName, null);
			exists = rs.next();
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return !exists;
	}

	// ==========================================
	// Kassen-Erweiterungen (Terminal & Verkauf)
	// ==========================================

	/**
	 * Legt alle notwendigen Tabellen für das Kassensystem (Kassierer, Kassenzettel, etc.) an.
	 * Die Tabelle KASSIERER enthält die Spalte "manager" für erweiterte Rechte.
	 */
	@Override
	public void initKassenTabellen() {
		// NEU: manager Spalte als integer (0 = Nein, 1 = Ja)
		String sqlKassierer = "CREATE TABLE IF NOT EXISTS KASSIERER (id integer PRIMARY KEY AUTOINCREMENT, nummer integer UNIQUE NOT NULL, pin text NOT NULL, name text NOT NULL, manager integer DEFAULT 0)";
		String sqlKassenzettel = "CREATE TABLE IF NOT EXISTS KASSENZETTEL (id integer PRIMARY KEY AUTOINCREMENT, datum text NOT NULL, uhrzeit text NOT NULL, zahlart text NOT NULL, kassierer_id integer NOT NULL, gesamtpreis real NOT NULL)";
		String sqlPositionen = "CREATE TABLE IF NOT EXISTS KASSENZETTEL_POSITION (id integer PRIMARY KEY AUTOINCREMENT, kassenzettel_id integer NOT NULL, produkt_id integer NOT NULL, anzahl integer NOT NULL, gesamtpreis real NOT NULL)";
		String sqlAbschluss = "CREATE TABLE IF NOT EXISTS KASSENABSCHLUSS (id integer PRIMARY KEY AUTOINCREMENT, datum text NOT NULL, uhrzeit text NOT NULL, kassierer_id integer NOT NULL, soll real NOT NULL, ist real NOT NULL)";

		try (Connection con = DriverManager.getConnection(dbUrl);
		     Statement st = con.createStatement()) {

			st.executeUpdate(sqlKassierer);
			st.executeUpdate(sqlKassenzettel);
			st.executeUpdate(sqlPositionen);
			st.executeUpdate(sqlAbschluss);

			try {
				st.executeUpdate("ALTER TABLE KASSIERER ADD COLUMN manager integer DEFAULT 0");
			} catch (SQLException ignored) {
				// Ignorieren, falls die Spalte schon existiert
			}

			ResultSet rs = st.executeQuery("SELECT count(*) FROM KASSIERER");
			if (rs.next() && rs.getInt(1) == 0) {
				st.executeUpdate("INSERT INTO KASSIERER (nummer, pin, name, manager) VALUES (1001, '1234', 'Max Muster', 0)");
				st.executeUpdate("INSERT INTO KASSIERER (nummer, pin, name, manager) VALUES (9999, '0000', 'Chef Autorisierung', 1)");
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Initialisieren der Kassentabellen", e);
		}
	}

	// ==========================================
	// BEREICH: KASSIERER VERWALTUNG
	// ==========================================

	/**
	 * Holt eine Liste aller hinterlegten Kassierer aus der Datenbank.
	 * @return Liste der Kassierer.
	 */
	@Override
	public List<Kassierer> getAllKassierer() {
		List<Kassierer> list = new ArrayList<>();
		String sql = "SELECT * FROM KASSIERER";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Kassierer k = new Kassierer(rs.getInt("id"), rs.getInt("nummer"), rs.getString("pin"), rs.getString("name"));
				k.setManager(rs.getInt("manager") == 1);
				list.add(k);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Abrufen der Kassierer", e);
		}
		return list;
	}

	/**
	 * Fügt einen neuen Kassierer zur Datenbank hinzu.
	 * @param k Der anzulegende Kassierer.
	 */
	@Override
	public void addKassierer(Kassierer k) {
		String sql = "INSERT INTO KASSIERER (nummer, name, pin, manager) VALUES (?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, k.getNummer());
			pstmt.setString(2, k.getName());
			pstmt.setString(3, k.getPin());
			pstmt.setInt(4, k.isManager() ? 1 : 0);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Hinzufügen eines Kassierers", e);
		}
	}

	/**
	 * Aktualisiert einen bestehenden Kassierer (Identifizierung über die Nummer).
	 * @param k Der Kassierer mit aktualisierten Daten.
	 */
	@Override
	public void updateKassierer(Kassierer k) {
		String sql = "UPDATE KASSIERER SET name=?, pin=?, manager=? WHERE nummer=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, k.getName());
			pstmt.setString(2, k.getPin());
			pstmt.setInt(3, k.isManager() ? 1 : 0);
			pstmt.setInt(4, k.getNummer());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Kassierers", e);
		}
	}

	/**
	 * Löscht einen Kassierer komplett aus der Datenbank.
	 * @param nummer Die eindeutige Nummer des Kassierers.
	 */
	@Override
	public void deleteKassierer(int nummer) {
		String sql = "DELETE FROM KASSIERER WHERE nummer=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, nummer);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Löschen des Kassierers", e);
		}
	}

	/**
	 * Sucht einen Kassierer anhand seiner Login-Nummer.
	 * @param nummer Die Personalnummer.
	 * @return Der Kassierer (oder null, falls nicht gefunden).
	 */
	@Override
	public Kassierer getKassiererByNummer(int nummer) {
		Kassierer k = null;
		String sql = "SELECT * FROM KASSIERER WHERE nummer=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, nummer);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				k = new Kassierer(rs.getInt("id"), rs.getInt("nummer"), rs.getString("pin"), rs.getString("name"));
				k.setManager(rs.getInt("manager") == 1);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return k;
	}

	/**
	 * Sucht einen Kassierer anhand seiner Datenbank-ID.
	 * @param id Die interne Datenbank-ID.
	 * @return Der Kassierer.
	 */
	public Kassierer getKassiererById(int id) {
		Kassierer k = null;
		String sql = "SELECT * FROM KASSIERER WHERE id=?";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				k = new Kassierer(rs.getInt("id"), rs.getInt("nummer"), rs.getString("pin"), rs.getString("name"));
				k.setManager(rs.getInt("manager") == 1);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return k;
	}

	// ==========================================
	// BEREICH: BESTEHENDE KASSENBEFEHLE
	// ==========================================

	/**
	 * Speichert einen abgeschlossenen Kassenzettel inklusive all seiner Positionen.
	 * @param kassenzettel Der fertige Kassenzettel vom Kassen-Client.
	 */
	@Override
	public void saveKassenzettel(Kassenzettel kassenzettel) {
		String sqlInsertZettel = "INSERT INTO KASSENZETTEL (datum, uhrzeit, zahlart, kassierer_id, gesamtpreis) VALUES (?, ?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sqlInsertZettel)) {

			pstmt.setString(1, kassenzettel.getDatum());
			pstmt.setString(2, kassenzettel.getUhrzeit());
			pstmt.setString(3, kassenzettel.getZahlart());
			pstmt.setInt(4, kassenzettel.getKassierer().getId());
			pstmt.setDouble(5, kassenzettel.getGesamtpreis());
			pstmt.executeUpdate();

			int lastId = 0;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT last_insert_rowid()");
			if (rs.next()) {
				lastId = rs.getInt(1);
			}
			rs.close();
			st.close();

			String sqlInsertPos = "INSERT INTO KASSENZETTEL_POSITION (kassenzettel_id, produkt_id, anzahl, gesamtpreis) VALUES (?, ?, ?, ?)";
			PreparedStatement posStmt = con.prepareStatement(sqlInsertPos);
			for (KassenzettelPosition pos : kassenzettel.getPositionen()) {
				posStmt.setInt(1, lastId);
				posStmt.setInt(2, pos.getProdukt().getId());
				posStmt.setInt(3, pos.getAnzahl());
				posStmt.setDouble(4, pos.getGesamtpreis());
				posStmt.executeUpdate();
			}
			posStmt.close();

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Speichert das Protokoll eines Schicht- bzw. Kassenabschlusses.
	 * @param abschluss Das Abschluss-Objekt (Soll, Ist, Kassierer).
	 */
	@Override
	public void saveKassenabschluss(Kassenabschluss abschluss) {
		String sql = "INSERT INTO KASSENABSCHLUSS (datum, uhrzeit, kassierer_id, soll, ist) VALUES (?, ?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, abschluss.getDatum());
			pstmt.setString(2, abschluss.getUhrzeit());
			pstmt.setInt(3, abschluss.getKassierer().getId());
			pstmt.setDouble(4, abschluss.getSollBestand());
			pstmt.setDouble(5, abschluss.getIstBestand());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Reduziert nach einem erfolgreichen Verkauf den Lagerbestand des jeweiligen Produkts.
	 * @param produktId Die ID des verkauften Produkts.
	 * @param anzahl Die gekaufte Menge, die abgebucht werden soll.
	 * @throws NegativeStockException Falls durch den Verkauf der Bestand unter 0 fallen würde.
	 */
	@Override
	public void reduceLagerbestand(int produktId, int anzahl) throws NegativeStockException {
		String selectSql = "SELECT id, anzahl FROM LAGERBESTAND WHERE produkt_id=? ORDER BY id LIMIT 1";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement selectStmt = con.prepareStatement(selectSql)) {

			selectStmt.setInt(1, produktId);
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				int bestandId = rs.getInt("id");
				int aktuellerBestand = rs.getInt("anzahl");

				if (aktuellerBestand - anzahl < 0) {
					rs.close();
					throw new NegativeStockException("Lagerbestand für Produkt ID " + produktId + " darf nicht negativ werden.");
				}

				String updateSql = "UPDATE LAGERBESTAND SET anzahl=? WHERE id=?";
				PreparedStatement updateStmt = con.prepareStatement(updateSql);
				updateStmt.setInt(1, aktuellerBestand - anzahl);
				updateStmt.setInt(2, bestandId);
				updateStmt.executeUpdate();
				updateStmt.close();

			} else {
				rs.close();
				throw new NegativeStockException("Kein Lagerbestand für Produkt ID " + produktId + " gefunden.");
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Liest die komplette Historie aller jemals erstellten Kassenzettel aus der Datenbank.
	 * Lädt in einer Sub-Query direkt alle dazugehörigen Positionen mit.
	 * @return Eine Liste aller Kassenzettel inklusive Positionen.
	 */
	@Override
	public List<Kassenzettel> getAllKassenzettel() {
		List<Kassenzettel> list = new ArrayList<>();
		String sqlZettel = "SELECT * FROM KASSENZETTEL";
		String sqlPositionen = "SELECT * FROM KASSENZETTEL_POSITION WHERE kassenzettel_id=?";

		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmtZettel = con.prepareStatement(sqlZettel);
		     ResultSet rsZettel = pstmtZettel.executeQuery()) {

			while (rsZettel.next()) {
				Kassenzettel z = new Kassenzettel();
				int zettelId = rsZettel.getInt("id");

				z.setId(zettelId);
				z.setDatum(rsZettel.getString("datum"));
				z.setUhrzeit(rsZettel.getString("uhrzeit"));
				z.setZahlart(rsZettel.getString("zahlart"));
				z.setGesamtpreis(rsZettel.getDouble("gesamtpreis"));
				z.setKassierer(getKassiererById(rsZettel.getInt("kassierer_id")));

				try (PreparedStatement pstmtPos = con.prepareStatement(sqlPositionen)) {
					pstmtPos.setInt(1, zettelId);
					try (ResultSet rsPos = pstmtPos.executeQuery()) {
						while (rsPos.next()) {
							thw.edu.javaII.port.warehouse.model.KassenzettelPosition pos = new thw.edu.javaII.port.warehouse.model.KassenzettelPosition();
							thw.edu.javaII.port.warehouse.model.Produkt p = getProduktById(rsPos.getInt("produkt_id"));

							pos.setProdukt(p);
							pos.setAnzahl(rsPos.getInt("anzahl"));
							pos.setGesamtpreis(rsPos.getDouble("gesamtpreis"));

							z.getPositionen().add(pos);
						}
					}
				}
				list.add(z);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Laden der Kassenzettel und Positionen", e);
		}
		return list;
	}

	/**
	 * Liest die komplette Historie aller jemals durchgeführten Kassenabschlüsse aus der Datenbank.
	 * @return Eine Liste aller Kassenabschlüsse inklusive Kassierer-Referenz.
	 */
	@Override
	public List<Kassenabschluss> getAllKassenabschluesse() {
		List<Kassenabschluss> list = new ArrayList<>();
		String sql = "SELECT * FROM KASSENABSCHLUSS";

		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Kassenabschluss a = new Kassenabschluss();
				// ID setzen (Wichtig für die spätere formatierte Anzeige)
				a.setId(rs.getInt("id"));
				a.setDatum(rs.getString("datum"));
				a.setUhrzeit(rs.getString("uhrzeit"));
				a.setSollBestand(rs.getDouble("soll"));
				a.setIstBestand(rs.getDouble("ist"));

				// Kassierer-Objekt anhand der hinterlegten ID auflösen
				a.setKassierer(getKassiererById(rs.getInt("kassierer_id")));

				list.add(a);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler beim Laden der Kassenabschlüsse", e);
		}
		return list;
	}
}