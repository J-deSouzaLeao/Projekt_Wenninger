package thw.edu.javaII.port.warehouse.server.data;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import thw.edu.javaII.port.warehouse.init.Initizilaizer;
import thw.edu.javaII.port.warehouse.model.DemoModel;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.model.common.Info;

import thw.edu.javaII.port.warehouse.model.Kassierer;
import thw.edu.javaII.port.warehouse.model.Kassenzettel;
import thw.edu.javaII.port.warehouse.model.Kassenabschluss;
import thw.edu.javaII.port.warehouse.model.KassenzettelPosition;
import thw.edu.javaII.port.warehouse.model.exception.NegativeStockException;

public class Database implements IStorage {
	private static final String driverClass = "org.sqlite.JDBC";
	private static final String dbUrl = "jdbc:sqlite:warehouse.sqlite";
	private Initizilaizer init;
	private Logger logger;

	public Database() throws Exception {
		try {
			Class.forName(driverClass);
			init = new Initizilaizer();
			logger = System.getLogger(Info.LOG_NAME);
		} catch (ClassNotFoundException e) {
			throw new Exception(e);
		}
	}

	@Override
	public void initLager(List<Lager> list) {
		if (!tableExists("LAGER")) {
			Connection con = null;
			Statement st = null;
			try {
				con = DriverManager.getConnection(dbUrl);
				st = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS LAGER (id integer PRIMARY KEY,	name text NOT NULL, ort text, art text)";
				st.executeUpdate(sql);
				for (Lager mod : init.getLager()) {
					addLager(mod);
				}
			} catch (SQLException e) {
				logger.log(Level.ERROR, e);
				e.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			turncateTable("LAGER");
			for (Lager mod : init.getLager()) {
				addLager(mod);
			}
		}

	}

	@Override
	public void addLager(Lager model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "INSERT INTO LAGER (id,name,ort,art) VALUES (" + model.getId() + ", '" + model.getName()
					+ "', '" + model.getOrt() + "', '" + model.getArt() + "')";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void updateLager(Lager model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "UPDATE LAGER SET name='" + model.getName() + "', ort='" + model.getOrt() + "', art='"
					+ model.getArt() + "' WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void deleteLager(Lager model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "DELETE FROM LAGER WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<Lager> getLagers() {
		List<Lager> l = new ArrayList<Lager>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM LAGER";
			rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String ort = rs.getString("ORT");
				String art = rs.getString("ART");
				l.add(new Lager(id, name, ort, art));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return l;
	}

	public Lager getLagerById(int id) {
		Lager model = new Lager();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM LAGER WHERE id=" + id;
			rs = st.executeQuery(sql);

			rs.next();
			model.setId(id);
			String name = rs.getString("NAME");
			String ort = rs.getString("ORT");
			String art = rs.getString("ART");
			model.setName(name);
			model.setOrt(ort);
			model.setArt(art);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return model;
	}

	@Override
	public void initLagerPlatz(List<LagerPlatz> list) {
		if (!tableExists("LAGERPLATZ")) {
			Connection con = null;
			Statement st = null;
			try {
				con = DriverManager.getConnection(dbUrl);
				st = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS LAGERPLATZ (id integer PRIMARY KEY, name text NOT NULL, kapazitaet integer NOT NULL, lager_id integer NOT NULL)";
				st.executeUpdate(sql);
				for (LagerPlatz mod : init.getLagerplatz()) {
					addLagerPlatz(mod);
				}
			} catch (SQLException e) {
				logger.log(Level.ERROR, e);
				e.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			turncateTable("LAGERPLATZ");
			for (LagerPlatz mod : init.getLagerplatz()) {
				addLagerPlatz(mod);
			}
		}
	}

	@Override
	public void addLagerPlatz(LagerPlatz model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "INSERT INTO LAGERPLATZ (id,name,kapazitaet,lager_id) VALUES (" + model.getId() + ", '"
					+ model.getName() + "', " + model.getKapazitaet() + ", " + model.getLager_id().getId() + ")";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void updateLagerPlatz(LagerPlatz model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "UPDATE LAGERPLATZ SET name='" + model.getName() + "', kapazitaet=" + model.getKapazitaet()
					+ ", lager_id=" + model.getLager_id().getId() + " WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void deleteLagerPlatz(LagerPlatz model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "DELETE FROM LAGERPLATZ WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<LagerPlatz> getLagerPlatzs() {
		List<LagerPlatz> l = new ArrayList<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM LAGERPLATZ";
			rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				int kapazitaet = rs.getInt("KAPAZITAET");
				Lager lager_id = getLagerById(rs.getInt("LAGER_ID"));
				l.add(new LagerPlatz(id, name, kapazitaet, lager_id));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return l;
	}

	public LagerPlatz getLagerPlatzById(int id) {
		LagerPlatz model = new LagerPlatz();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM LAGERPLATZ WHERE id=" + id;
			rs = st.executeQuery(sql);

			rs.next();
			model.setId(id);
			String name = rs.getString("NAME");
			int kapazitaet = rs.getInt("KAPAZITAET");
			Lager lager_id = getLagerById(rs.getInt("LAGER_ID"));
			model.setName(name);
			model.setKapazitaet(kapazitaet);
			model.setLager_id(lager_id);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return model;
	}

	@Override
	public void initLagerBestand(List<LagerBestand> list) {
		if (!tableExists("LAGERBESTAND")) {
			Connection con = null;
			Statement st = null;
			try {
				con = DriverManager.getConnection(dbUrl);
				st = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS LAGERBESTAND (id integer PRIMARY KEY, anzahl integer NOT NULL, produkt_id integer NOT NULL, lagerplatz_id integer NOT NULL)";
				st.executeUpdate(sql);
				for (LagerBestand mod : init.getLagerbestand()) {
					addLagerBestand(mod);
				}
			} catch (SQLException e) {
				logger.log(Level.ERROR, e);
				e.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			turncateTable("LAGERBESTAND");
			for (LagerBestand mod : init.getLagerbestand()) {
				addLagerBestand(mod);
			}
		}

	}

	@Override
	public void addLagerBestand(LagerBestand model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "INSERT INTO LAGERBESTAND (id,anzahl,produkt_id,lagerplatz_id) VALUES (" + model.getId() + ", "
					+ model.getAnzahl() + ", " + model.getProdukt_id().getId() + ", " + model.getLagerplatz_id().getId()
					+ ")";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void updateLagerBestand(LagerBestand model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "UPDATE LAGERBESTAND SET anzahl=" + model.getAnzahl() + ", produkt_id="
					+ model.getProdukt_id().getId() + ", lagerplatz_id=" + model.getLagerplatz_id().getId()
					+ " WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void deleteLagerBestand(LagerBestand model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "DELETE FROM LAGERBESTAND WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<LagerBestand> getLagerBestands() {
		List<LagerBestand> l = new ArrayList<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM LAGERBESTAND";
			rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("ID");
				int anzahl = rs.getInt("ANZAHL");
				LagerPlatz lagerplatz_id = getLagerPlatzById(rs.getInt("LAGERPLATZ_ID"));
				Produkt produkt_id = getProduktById(rs.getInt("PRODUKT_ID"));
				l.add(new LagerBestand(id, anzahl, produkt_id, lagerplatz_id));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return l;
	}

	@Override
	public void initProdukt(List<Produkt> list) {
		if (!tableExists("PRODUKT")) {
			Connection con = null;
			Statement st = null;
			try {
				con = DriverManager.getConnection(dbUrl);
				st = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS PRODUKT (id integer PRIMARY KEY, name text NOT NULL, hersteller text, preis real)";
				st.executeUpdate(sql);
				for (Produkt mod : init.getProdukt()) {
					addProdukt(mod);
				}
			} catch (SQLException e) {
				logger.log(Level.ERROR, e);
				e.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			turncateTable("PRODUKT");
			for (Produkt mod : init.getProdukt()) {
				addProdukt(mod);
			}
		}

	}

	@Override
	public void addProdukt(Produkt model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "INSERT INTO PRODUKT (id,name,hersteller,preis) VALUES (" + model.getId() + ", '"
					+ model.getName() + "', '" + model.getHersteller() + "', " + model.getPreis() + ")";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Produkt getProduktByModel(Produkt mod) {
		Produkt model = new Produkt();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM PRODUKT WHERE id=" + mod.getId();
			rs = st.executeQuery(sql);

			rs.next();
			model.setId(mod.getId());
			String name = rs.getString("NAME");
			String hersteller = rs.getString("HERSTELLER");
			double preis = rs.getDouble("PREIS");
			model.setName(name);
			model.setHersteller(hersteller);
			model.setPreis(preis);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return model;
	}

	public Produkt getProduktById(int id) {
		Produkt model = new Produkt();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM PRODUKT WHERE id=" + id;
			rs = st.executeQuery(sql);

			rs.next();
			model.setId(id);
			String name = rs.getString("NAME");
			String hersteller = rs.getString("HERSTELLER");
			double preis = rs.getDouble("PREIS");
			model.setName(name);
			model.setHersteller(hersteller);
			model.setPreis(preis);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return model;
	}

	@Override
	public void updateProdukt(Produkt model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "UPDATE PRODUKT SET name='" + model.getName() + "', hersteller='" + model.getHersteller()
					+ "', preis=" + model.getPreis() + " WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void deleteProdukt(Produkt model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "DELETE FROM PRODUKT WHERE id=" + model.getId();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<Produkt> getProdukts() {
		List<Produkt> l = new ArrayList<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM PRODUKT";
			rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String hersteller = rs.getString("HERSTELLER");
				double preis = rs.getDouble("PREIS");
				l.add(new Produkt(id, name, hersteller, preis));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return l;
	}

	@Override
	public List<DemoModel> getDemos() {
		List<DemoModel> l = new ArrayList<DemoModel>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "SELECT * FROM DEMOS";
			rs = st.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				l.add(new DemoModel(id, name));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return l;
	}

	@Override
	public void initDemo(List<DemoModel> list) {
		if (!tableExists("DEMOS")) {
			Connection con = null;
			Statement st = null;
			try {
				con = DriverManager.getConnection(dbUrl);
				st = con.createStatement();
				String sql = "CREATE TABLE IF NOT EXISTS DEMOS (id integer PRIMARY KEY,	name text NOT NULL)";
				st.executeUpdate(sql);
				for (DemoModel mod : init.getDemo()) {
					addDemo(mod);
				}
			} catch (SQLException e) {
				logger.log(Level.ERROR, e);
				e.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			turncateTable("DEMOS");
			for (DemoModel mod : init.getDemo()) {
				addDemo(mod);
			}
		}

	}

	@Override
	public void addDemo(DemoModel model) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "INSERT INTO DEMOS (id,name) VALUES (" + model.getId() + ", '" + model.getName() + "')";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void turncateTable(String tableName) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			st = con.createStatement();
			String sql = "DELETE FROM " + tableName;
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean tableExists(String tableName) {
		Connection con = null;
		ResultSet rs = null;
		DatabaseMetaData md = null;
		try {
			con = DriverManager.getConnection(dbUrl);
			md = con.getMetaData();
			rs = md.getTables(null, null, tableName, null);
			rs.next();
			return rs.getRow() > 0;
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public void initKassenTabellen() {
		String sqlKassierer = "CREATE TABLE IF NOT EXISTS KASSIERER (id integer PRIMARY KEY AUTOINCREMENT, nummer integer UNIQUE NOT NULL, pin text NOT NULL, name text NOT NULL)";
		String sqlKassenzettel = "CREATE TABLE IF NOT EXISTS KASSENZETTEL (id integer PRIMARY KEY AUTOINCREMENT, datum text NOT NULL, uhrzeit text NOT NULL, zahlart text NOT NULL, kassierer_id integer NOT NULL, gesamtpreis real NOT NULL)";
		String sqlPositionen = "CREATE TABLE IF NOT EXISTS KASSENZETTEL_POSITION (id integer PRIMARY KEY AUTOINCREMENT, kassenzettel_id integer NOT NULL, produkt_id integer NOT NULL, anzahl integer NOT NULL, gesamtpreis real NOT NULL)";
		String sqlAbschluss = "CREATE TABLE IF NOT EXISTS KASSENABSCHLUSS (id integer PRIMARY KEY AUTOINCREMENT, datum text NOT NULL, uhrzeit text NOT NULL, kassierer_id integer NOT NULL, soll real NOT NULL, ist real NOT NULL)";

		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement()) {
			st.executeUpdate(sqlKassierer);
			st.executeUpdate(sqlKassenzettel);
			st.executeUpdate(sqlPositionen);
			st.executeUpdate(sqlAbschluss);

			// Dummy Kassierer anlegen falls Tabelle leer
			ResultSet rs = st.executeQuery("SELECT count(*) FROM KASSIERER");
			if (rs.next() && rs.getInt(1) == 0) {
				st.executeUpdate("INSERT INTO KASSIERER (nummer, pin, name) VALUES (1001, '1234', 'Max Muster')");
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
			e.printStackTrace();
		}
	}

	@Override
	public Kassierer getKassiererByNummer(int nummer) {
		String sql = "SELECT * FROM KASSIERER WHERE nummer=" + nummer;
		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next()) {
				return new Kassierer(rs.getInt("id"), rs.getInt("nummer"), rs.getString("pin"), rs.getString("name"));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
		}
		return null;
	}

	@Override
	public void saveKassenzettel(Kassenzettel kassenzettel) {
		String sqlInsertZettel = "INSERT INTO KASSENZETTEL (datum, uhrzeit, zahlart, kassierer_id, gesamtpreis) VALUES ('" +
				kassenzettel.getDatum() + "', '" + kassenzettel.getUhrzeit() + "', '" + kassenzettel.getZahlart() + "', " +
				kassenzettel.getKassierer().getId() + ", " + kassenzettel.getGesamtpreis() + ")";

		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement()) {
			st.executeUpdate(sqlInsertZettel);
			ResultSet rs = st.executeQuery("SELECT last_insert_rowid()");
			int lastId = 0;
			if (rs.next()) {
				lastId = rs.getInt(1);
			}

			for (KassenzettelPosition pos : kassenzettel.getPositionen()) {
				String sqlInsertPos = "INSERT INTO KASSENZETTEL_POSITION (kassenzettel_id, produkt_id, anzahl, gesamtpreis) VALUES (" +
						lastId + ", " + pos.getProdukt().getId() + ", " + pos.getAnzahl() + ", " + pos.getGesamtpreis() + ")";
				st.executeUpdate(sqlInsertPos);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
		}
	}

	@Override
	public void saveKassenabschluss(Kassenabschluss abschluss) {
		String sql = "INSERT INTO KASSENABSCHLUSS (datum, uhrzeit, kassierer_id, soll, ist) VALUES ('" +
				abschluss.getDatum() + "', '" + abschluss.getUhrzeit() + "', " + abschluss.getKassierer().getId() + ", " +
				abschluss.getSollBestand() + ", " + abschluss.getIstBestand() + ")";
		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement()) {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
		}
	}

	@Override
	public Kassenabschluss getLastKassenabschluss() {
		String sql = "SELECT * FROM KASSENABSCHLUSS ORDER BY id DESC LIMIT 1";
		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next()) {
				Kassenabschluss abschluss = new Kassenabschluss();
				abschluss.setId(rs.getInt("id"));
				abschluss.setDatum(rs.getString("datum"));
				abschluss.setUhrzeit(rs.getString("uhrzeit"));
				abschluss.setSollBestand(rs.getDouble("soll"));
				abschluss.setIstBestand(rs.getDouble("ist"));
				return abschluss;
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
		}
		return null; // Kein vorheriger Abschluss vorhanden
	}

	@Override
	public void reduceLagerbestand(int produktId, int anzahl) throws NegativeStockException {
		String selectSql = "SELECT id, anzahl FROM LAGERBESTAND WHERE produkt_id=" + produktId + " ORDER BY id ASC LIMIT 1";
		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement()) {
			ResultSet rs = st.executeQuery(selectSql);
			if (rs.next()) {
				int bestandId = rs.getInt("id");
				int aktuellerBestand = rs.getInt("anzahl");

				if (aktuellerBestand - anzahl < 0) {
					// Hier jetzt nur noch NegativeStockException statt dem langen Pfad
					throw new NegativeStockException("Lagerbestand für Produkt ID " + produktId + " darf nicht negativ werden.");
				}

				String updateSql = "UPDATE LAGERBESTAND SET anzahl=" + (aktuellerBestand - anzahl) + " WHERE id=" + bestandId;
				st.executeUpdate(updateSql);
			} else {
				// Hier ebenfalls
				throw new NegativeStockException("Kein Lagerbestand für Produkt ID " + produktId + " gefunden.");
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
		}
	}

	@Override
	public double getBargeldEinnahmenSeitLetztemAbschluss() {
		// Berechnet die Summe aller Bar-Kassenzettel, die neuer sind als der letzte Kassenabschluss
		String sql = "SELECT SUM(gesamtpreis) FROM KASSENZETTEL WHERE zahlart='Bar' AND id > COALESCE((SELECT MAX(id) FROM KASSENABSCHLUSS), 0)";
		try (Connection con = DriverManager.getConnection(dbUrl); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, e);
		}
		return 0.0;
	}

}
