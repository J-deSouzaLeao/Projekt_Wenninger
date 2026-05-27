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
	 * Schreibt ein einzelnes, neues Lager per SQL-INSERT in die Datenbank.
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
	 * Überschreibt die Daten eines bestehenden Lagers anhand seiner ID.
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
	 * Löscht ein Lager komplett aus der Datenbank.
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
	 * Liest alle gespeicherten Lager aus der Tabelle aus und gibt sie als Liste zurück.
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
	 * Sucht ein ganz bestimmtes Lager direkt anhand seiner ID.
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
	 * Bereitet die Lagerplatz-Tabelle vor und füllt sie mit Startdaten.
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
	 * Legt einen neuen Lagerplatz in der Datenbank an.
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
	 * Speichert Änderungen (z.B. eine neue Kapazität) für einen Lagerplatz ab.
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
	 * Lädt alle Lagerplätze aus der Datenbank und ordnet ihnen direkt das passende Hauptlager zu.
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
	 * Hilfsmethode, um einen einzelnen Lagerplatz samt seinem Hauptlager zu laden.
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
	 * Erstellt die Bestands-Tabelle oder setzt sie auf die Startdaten zurück.
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
	 * Legt einen neuen Lagerbestand (Verknüpfung Menge - Produkt - Platz) an.
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
	 * Passt einen bestehenden Bestand an (z.B. wenn sich die Anzahl geändert hat).
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
	 * Löscht einen Bestandseintrag komplett.
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
	 * Liest alle Bestände aus. Zu jedem Bestand wird automatisch das richtige Produkt und der Platz geladen.
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
	 * Baut die Produkttabelle auf und lädt die Start-Produkte hinein.
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
	 * Trägt ein neues Produkt in die Datenbank ein.
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
	 * Sucht ein Produkt über ein als Hülle übergebenes Modell (delegiert an getProduktById).
	 */
	@Override
	public Produkt getProduktByModel(Produkt mod) {
		return getProduktById(mod.getId());
	}

	/**
	 * Sucht ein Produkt anhand seiner eindeutigen ID.
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
	 * Speichert aktualisierte Produktinformationen ab.
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
	 * Löscht ein Produkt aus der Datenbank.
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
	 * Holt sich alle eingetragenen Produkte aus der Tabelle.
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
	 * Initialisiert die Demo-Tabelle für einfache Tests.
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
	 * Fügt einen Demo-Eintrag in die Datenbank ein.
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
	 * Interne Hilfsmethode, um alle Datensätze einer Tabelle schnell zu löschen.
	 * @param tableName Der Name der zu leerenden Tabelle.
	 */
	private void truncateTable(String tableName) {
		//noinspection all
		String sql = "DELETE FROM " + tableName;
		try (Connection con = DriverManager.getConnection(dbUrl);
		     Statement st = con.createStatement()) {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Interne Hilfsmethode, die nachschaut, ob eine Tabelle in der Datenbank bereits existiert.
	 * @param tableName Der Tabellenname.
	 * @return True, wenn sie NICHT existiert, sonst False.
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
	 * Legt alle notwendigen Tabellen für das Kassensystem (Kassierer, Kassenzettel, etc.) an
	 * und erstellt automatisch einen normalen Kassierer sowie einen Chef-Nutzer für Freigaben.
	 */
	@Override
	public void initKassenTabellen() {
		String sqlKassierer = "CREATE TABLE IF NOT EXISTS KASSIERER (id integer PRIMARY KEY AUTOINCREMENT, nummer integer UNIQUE NOT NULL, pin text NOT NULL, name text NOT NULL)";
		String sqlKassenzettel = "CREATE TABLE IF NOT EXISTS KASSENZETTEL (id integer PRIMARY KEY AUTOINCREMENT, datum text NOT NULL, uhrzeit text NOT NULL, zahlart text NOT NULL, kassierer_id integer NOT NULL, gesamtpreis real NOT NULL)";
		String sqlPositionen = "CREATE TABLE IF NOT EXISTS KASSENZETTEL_POSITION (id integer PRIMARY KEY AUTOINCREMENT, kassenzettel_id integer NOT NULL, produkt_id integer NOT NULL, anzahl integer NOT NULL, gesamtpreis real NOT NULL)";
		String sqlAbschluss = "CREATE TABLE IF NOT EXISTS KASSENABSCHLUSS (id integer PRIMARY KEY AUTOINCREMENT, datum text NOT NULL, uhrzeit text NOT NULL, kassierer_id integer NOT NULL, soll real NOT NULL, ist real NOT NULL)";

		try (Connection con = DriverManager.getConnection(dbUrl);
		     Statement st = con.createStatement()) {
			st.executeUpdate(sqlKassierer);
			st.executeUpdate(sqlKassenzettel);
			st.executeUpdate(sqlPositionen);
			st.executeUpdate(sqlAbschluss);

			ResultSet rs = st.executeQuery("SELECT count(*) FROM KASSIERER");
			if (rs.next() && rs.getInt(1) == 0) {
				st.executeUpdate("INSERT INTO KASSIERER (nummer, pin, name) VALUES (1001, '1234', 'Max Muster')");
				st.executeUpdate("INSERT INTO KASSIERER (nummer, pin, name) VALUES (9999, '0000', 'Chef Autorisierung')");
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
	}

	/**
	 * Sucht einen Kassierer anhand seiner Anmeldenummer (Login am Touch-Panel).
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
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return k;
	}

	/**
	 * Speichert einen kompletten Kassenbon. Erstellt zuerst den Kopf des Bons und
	 * verknüpft danach alle einzelnen gekauften Positionen damit in der Datenbank.
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
	 * Speichert den Tages- oder Schichtabschluss der Kasse inklusive Soll/Ist-Vergleich.
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
	 * Ruft den zeitlich letzten Kassenabschluss ab, um zu berechnen, ab wann Einnahmen neu gezählt werden müssen.
	 */
	@Override
	public Kassenabschluss getLastKassenabschluss() {
		Kassenabschluss abschluss = null;
		String sql = "SELECT * FROM KASSENABSCHLUSS ORDER BY id DESC LIMIT 1";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				abschluss = new Kassenabschluss();
				abschluss.setId(rs.getInt("id"));
				abschluss.setDatum(rs.getString("datum"));
				abschluss.setUhrzeit(rs.getString("uhrzeit"));
				abschluss.setSollBestand(rs.getDouble("soll"));
				abschluss.setIstBestand(rs.getDouble("ist"));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return abschluss;
	}

	/**
	 * Bucht verkaufte Ware direkt aus dem Lager aus. Bricht ab und wirft einen Fehler,
	 * wenn versucht wird, mehr zu verkaufen, als da ist.
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
	 * Errechnet die Summe aller Barverkäufe, die seit dem letzten Kassenabschluss getätigt wurden.
	 */
	@Override
	public double getBargeldEinnahmenSeitLetztemAbschluss() {
		double einnahmen = 0.0;
		String sql = "SELECT SUM(gesamtpreis) FROM KASSENZETTEL WHERE zahlart='Bar' AND id > COALESCE((SELECT MAX(id) FROM KASSENABSCHLUSS), 0)";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				einnahmen = rs.getDouble(1);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return einnahmen;
	}

	/**
	 * Hilfsmethode, um einen bestimmten Kassierer über seine Datenbank-ID zu finden (wird für die Bon-Historie genutzt).
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
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return k;
	}

	/**
	 * Liest die komplette Historie aller jemals erstellten Kassenzettel aus.
	 */
	@Override
	public List<Kassenzettel> getAllKassenzettel() {
		List<Kassenzettel> list = new ArrayList<>();
		String sql = "SELECT * FROM KASSENZETTEL";
		try (Connection con = DriverManager.getConnection(dbUrl);
		     PreparedStatement pstmt = con.prepareStatement(sql);
		     ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Kassenzettel z = new Kassenzettel();
				z.setId(rs.getInt("id"));
				z.setDatum(rs.getString("datum"));
				z.setUhrzeit(rs.getString("uhrzeit"));
				z.setZahlart(rs.getString("zahlart"));
				z.setGesamtpreis(rs.getDouble("gesamtpreis"));
				z.setKassierer(getKassiererById(rs.getInt("kassierer_id")));
				list.add(z);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Fehler", e);
		}
		return list;
	}
}