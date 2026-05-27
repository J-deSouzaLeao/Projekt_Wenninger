package thw.edu.javaII.port.warehouse.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import thw.edu.javaII.port.warehouse.model.common.Cast;
import thw.edu.javaII.port.warehouse.model.deo.Status;
import thw.edu.javaII.port.warehouse.model.deo.WarehouseDEO;
import thw.edu.javaII.port.warehouse.model.deo.WarehouseReturnDEO;
import thw.edu.javaII.port.warehouse.model.Lager;
import thw.edu.javaII.port.warehouse.model.LagerBestand;
import thw.edu.javaII.port.warehouse.model.LagerPlatz;
import thw.edu.javaII.port.warehouse.model.Produkt;
import thw.edu.javaII.port.warehouse.server.comparator.BestandByLagerBestand;
import thw.edu.javaII.port.warehouse.server.comparator.BestandByProduktAlpha;
import thw.edu.javaII.port.warehouse.server.data.Database;
import thw.edu.javaII.port.warehouse.server.data.IStorage;
import thw.edu.javaII.port.warehouse.server.init.Loading;

/**
 * Diese Klasse arbeitet als eigener Prozess (Thread) und fungiert als persönlicher Betreuer
 * für genau einen verbundenen Client (z. B. das Kassenterminal oder die Verwaltungsoberfläche).
 * Sie nimmt die Anfragen (DEOs) des Clients entgegen, leitet sie an den richtigen Bereich
 * der Datenbank weiter und sendet die passenden Antworten zurück.
 * * Da die Client-Verbindungen zustandslos sind (pro Request ein neuer Socket/Service),
 * verwaltet diese Klasse den Schicht-Startbestand der Kasse in einer statischen Variablen.
 * * @author juan.de.souza.leao
 */
public class Service extends Thread {
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Service.class.getName());
	static int count = 0;

	// --- NEU: Statischer Zwischenspeicher für die aktuelle Kassenschicht ---
	// Da für jeden Request ein neuer Service-Thread erstellt wird, hält diese
	// statische Variable den Startbestand über die einzelnen Requests hinweg im Speicher.
	private static double aktuellerStartBestand = 0.0;

	private static double aktuellerSchichtUmsatz = 0.0;

	private int currentNumber = 0;

	private Socket sock;
	private ObjectInputStream fromClient;
	private ObjectOutputStream toClient;
	private IStorage store;
	private boolean run;

	/**
	 * Erstellt einen neuen Service-Betreuer für eine eingehende Client-Verbindung.
	 * Öffnet die Datenströme zum Senden und Empfangen von Nachrichten und
	 * stellt die Verbindung zur Datenbank her.
	 * * @param sock Der Socket (die Netzwerkverbindung) des verbundenen Clients.
	 */
	public Service(Socket sock) {
		try {
			run = true;
			this.sock = sock;
			this.store = new Database();
			currentNumber = ++count;
			toClient = new ObjectOutputStream(sock.getOutputStream());
			fromClient = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			System.out.println("IO-Error bei Client " + currentNumber);
			LOGGER.log(java.util.logging.Level.SEVERE, "Ein Fehler in der Netzwerkkommunikation ist aufgetreten", e);
		} catch (Exception e) {
			System.out.println("Fehler bei der Erzeugung der Datenbank");
			LOGGER.log(java.util.logging.Level.SEVERE, "Ein Fehler in der Netzwerkkommunikation ist aufgetreten", e);
		}
	}

	/**
	 * Die Hauptschleife dieser Verbindung.
	 * Wartet kontinuierlich auf neue Anfragen des Clients, prüft für welche "Zone"
	 * die Anfrage gedacht ist, führt die Aktion aus und sendet das Ergebnis zurück.
	 */
	@Override
	public void run() {
		System.out.println("Bearbeitung fuer Client " + currentNumber + " gestartet");
		try {
			while (run) {
				WarehouseDEO deoIn = (WarehouseDEO) fromClient.readObject();
				WarehouseReturnDEO deoOut;
				deoOut = switch (deoIn.getZone()) {
					case INIT -> handleZoneInit(deoIn);
					case LAGER -> handleZoneLager(deoIn);
					case LAGERBESTAND -> handleZoneLagerBestand(deoIn);
					case LAGERPLATZ -> handleZoneLagerPlatz(deoIn);
					case PRODUKT -> handleZoneProdukt(deoIn);
					case STATISTIK -> handleZoneStatistik(deoIn);
					case GENERAL -> handleZoneGeneral(deoIn);
					case KASSE -> handleZoneKasse(deoIn);
					default -> new WarehouseReturnDEO(null, "Unbekannte Zone", Status.ERROR);
				};
				toClient.writeObject(deoOut);
			}
		} catch (IOException e) {
			System.out.println("IO-Error bei Client " + currentNumber);
		} catch (ClassNotFoundException e) {
			System.out.println("Fehler beim uebergeben Objekt");
		} finally {
			try {
				if (fromClient != null)
					fromClient.close();
				if (toClient != null)
					toClient.close();
				if (sock != null)
					sock.close();
			} catch (IOException ignored) {
			}

		}
		System.out.println("Protokoll fuer Client " + currentNumber + " beendet");
	}

	/**
	 * Verarbeitet alle Anfragen, die den Bereich "Lager" (Hauptstandorte) betreffen.
	 * * @param deoIn Das empfangene Datenpaket des Clients.
	 * @return Die Antwort, die an den Client zurückgesendet wird.
	 */
	private WarehouseReturnDEO handleZoneLager(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case ADD:
				if (deoIn.getData() != null && deoIn.getData() instanceof Lager) {
					Lager l = Cast.safeCast(deoIn.getData(), Lager.class);
					store.addLager(l);
					deoOut = new WarehouseReturnDEO(null, "Lager erfolgreich angelegt", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case DELETE:
				if (deoIn.getData() != null && deoIn.getData() instanceof Lager) {
					Lager l = Cast.safeCast(deoIn.getData(), Lager.class);
					store.deleteLager(l);
					deoOut = new WarehouseReturnDEO(null, "Lager erfolgreich gelöscht", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case INIT:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;
			case LIST:
				deoOut = new WarehouseReturnDEO(store.getLagers(), "Liste aller Lager", Status.OK);
				break;
			case UPDATE:
				if (deoIn.getData() != null && deoIn.getData() instanceof Lager) {
					Lager l = Cast.safeCast(deoIn.getData(), Lager.class);
					store.updateLager(l);
					deoOut = new WarehouseReturnDEO(null, "Lager erfolgreich bearbeitet", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet alle Anfragen, die einzelne Lagerplätze betreffen.
	 * * @param deoIn Das empfangene Datenpaket des Clients.
	 * @return Die Antwort, die an den Client zurückgesendet wird.
	 */
	private WarehouseReturnDEO handleZoneLagerPlatz(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case ADD:
				if (deoIn.getData() != null && deoIn.getData() instanceof LagerPlatz) {
					LagerPlatz l = Cast.safeCast(deoIn.getData(), LagerPlatz.class);
					store.addLagerPlatz(l);
					deoOut = new WarehouseReturnDEO(null, "Lagerplatz erfolgreich angelegt", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case DELETE:
				if (deoIn.getData() != null && deoIn.getData() instanceof LagerPlatz) {
					LagerPlatz l = Cast.safeCast(deoIn.getData(), LagerPlatz.class);
					store.deleteLagerPlatz(l);
					deoOut = new WarehouseReturnDEO(null, "Lagerplatz erfolgreich gelöscht", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case INIT:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;
			case LIST:
				deoOut = new WarehouseReturnDEO(store.getLagerPlatzs(), "Liste aller Lagerplätze", Status.OK);
				break;
			case UPDATE:
				if (deoIn.getData() != null && deoIn.getData() instanceof LagerPlatz) {
					LagerPlatz l = Cast.safeCast(deoIn.getData(), LagerPlatz.class);
					store.updateLagerPlatz(l);
					deoOut = new WarehouseReturnDEO(null, "Lagerplatz erfolgreich bearbeitet", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet alle Anfragen zur Buchung und Verwaltung von Lagerbeständen (Mengen).
	 * * @param deoIn Das empfangene Datenpaket des Clients.
	 * @return Die Antwort, die an den Client zurückgesendet wird.
	 */
	private WarehouseReturnDEO handleZoneLagerBestand(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case ADD:
				if (deoIn.getData() != null && deoIn.getData() instanceof LagerBestand) {
					LagerBestand l = Cast.safeCast(deoIn.getData(), LagerBestand.class);
					store.addLagerBestand(l);
					deoOut = new WarehouseReturnDEO(null, "Lagerbestand erfolgreich angelegt", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case DELETE:
				if (deoIn.getData() != null && deoIn.getData() instanceof LagerBestand) {
					LagerBestand l = Cast.safeCast(deoIn.getData(), LagerBestand.class);
					store.deleteLagerBestand(l);
					deoOut = new WarehouseReturnDEO(null, "Lagerbestand erfolgreich gelöscht", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case INIT:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;
			case LIST:
				deoOut = new WarehouseReturnDEO(store.getLagerBestands(), "Liste aller Lagerbestände", Status.OK);
				break;
			case UPDATE:
				if (deoIn.getData() != null && deoIn.getData() instanceof LagerBestand) {
					LagerBestand l = Cast.safeCast(deoIn.getData(), LagerBestand.class);
					store.updateLagerBestand(l);
					deoOut = new WarehouseReturnDEO(null, "Lagerbestand erfolgreich bearbeitet", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case SEARCH:
				if (deoIn.getData() != null && deoIn.getData() instanceof String) {
					String search = Cast.safeCast(deoIn.getData(), String.class);
					List<LagerBestand> all = store.getLagerBestands();
					List<LagerBestand> relevantData = new ArrayList<>();
					for (LagerBestand mod : all) {
						String searchData = mod.getProdukt_id().getName() + mod.getProdukt_id().getHersteller()
								+ mod.getLagerplatz_id().getName() + mod.getLagerplatz_id().getLager_id().getName()
								+ mod.getLagerplatz_id().getLager_id().getOrt()
								+ mod.getLagerplatz_id().getLager_id().getArt();
						if (searchData.toLowerCase().contains(search.toLowerCase())) {
							relevantData.add(mod);
						}
					}
					deoOut = new WarehouseReturnDEO(relevantData, "Lagerbestand durchsucht nach " + search, Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet alle Anfragen rund um die Anlage, Änderung und Löschung von Produkten.
	 * Prüft beim Anlegen (ADD) explizit, ob die Produkt-ID bereits vergeben ist,
	 * um Duplikate zu vermeiden und dem Client einen korrekten Fehlerstatus zurückzumelden.
	 *
	 * @param deoIn Das empfangene Datenpaket des Clients.
	 * @return Die Antwort, die an den Client zurückgesendet wird.
	 */
	private WarehouseReturnDEO handleZoneProdukt(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case ADD:
				if (deoIn.getData() != null && deoIn.getData() instanceof Produkt) {
					Produkt neuesProdukt = Cast.safeCast(deoIn.getData(), Produkt.class);

					// --- NEU: Manuelle Prüfung, ob die ID bereits existiert ---
					boolean idExistiert = false;
					for (Produkt p : store.getProdukts()) {
						if (p.getId() == neuesProdukt.getId()) {
							idExistiert = true;
							break;
						}
					}

					if (idExistiert) {
						// ID ist schon vergeben -> Sende ERROR an den Client
						deoOut = new WarehouseReturnDEO(null, "Fehler: Produkt-ID existiert bereits!", Status.ERROR);
					} else {
						// ID ist frei -> Speichern und OK senden
						store.addProdukt(neuesProdukt);
						deoOut = new WarehouseReturnDEO(null, "Produkt erfolgreich angelegt", Status.OK);
					}
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;

			case DELETE:
				if (deoIn.getData() != null && deoIn.getData() instanceof Produkt) {
					Produkt l = Cast.safeCast(deoIn.getData(), Produkt.class);
					store.deleteProdukt(l);
					deoOut = new WarehouseReturnDEO(null, "Produkt erfolgreich gelöscht", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;

			case INIT:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;

			case LIST:
				deoOut = new WarehouseReturnDEO(store.getProdukts(), "Liste aller Produkte", Status.OK);
				break;

			case UPDATE:
				if (deoIn.getData() != null && deoIn.getData() instanceof Produkt) {
					Produkt l = Cast.safeCast(deoIn.getData(), Produkt.class);
					store.updateProdukt(l);
					deoOut = new WarehouseReturnDEO(null, "Produkt erfolgreich bearbeitet", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;

			case GETBYMODEL:
				if (deoIn.getData() != null && deoIn.getData() instanceof Produkt) {
					Produkt l = Cast.safeCast(deoIn.getData(), Produkt.class);
					Produkt r = store.getProduktByModel(l);
					deoOut = new WarehouseReturnDEO(r, "Produkt gefunden bearbeitet", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;

			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet Anforderungen für statistische Auswertungen (z. B. Top 10 Produkte oder Engpässe).
	 * * @param deoIn Das empfangene Datenpaket des Clients.
	 * @return Die Antwort inklusive der gefilterten oder sortierten Listen.
	 */
	private WarehouseReturnDEO handleZoneStatistik(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;

		List<LagerBestand> data;
		List<LagerBestand> relevantData;

		switch (deoIn.getCommand()) {
			case ADD:
			case DELETE:
			case INIT:
			case LIST:
			case UPDATE:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;
			case BESTAND:
				data = store.getLagerBestands();
				data.sort(new BestandByProduktAlpha());
				deoOut = new WarehouseReturnDEO(data, "Liste der Prdoukte mit Lagerinfo alphabetisch", Status.OK);
				break;
			case TOP:
				data = store.getLagerBestands();
				data.sort(new BestandByLagerBestand());
				Collections.reverse(data);
				relevantData = data.stream().limit(10).collect(Collectors.toList());
				deoOut = new WarehouseReturnDEO(relevantData, "Liste der TOP 10 Prdoukte nach Lagerbestand", Status.OK);
				break;
			case LOW:
				data = store.getLagerBestands();
				data.sort(new BestandByLagerBestand());
				relevantData = data.stream().limit(10).collect(Collectors.toList());
				deoOut = new WarehouseReturnDEO(relevantData, "Liste der LOW 10 Prdoukte nach Lagerbestand", Status.OK);
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet den Befehl zum Zurücksetzen oder Initialisieren der Datenbank mit Startwerten.
	 * * @param deoIn Das empfangene Datenpaket.
	 * @return Status der Initialisierung.
	 */
	private WarehouseReturnDEO handleZoneInit(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case ADD:
			case DELETE:
			case LIST:
			case UPDATE:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;
			case INIT:
				Loading load = new Loading();
				load.initLoading(store);
				deoOut = new WarehouseReturnDEO(null, "Aktion erfolgreich bearbeitet", Status.OK);
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet systemweite Befehle, wie z. B. das Trennen der Verbindung oder das Herunterfahren des Servers.
	 * * @param deoIn Das empfangene Datenpaket.
	 * @return Status der Systemanfrage.
	 */
	private WarehouseReturnDEO handleZoneGeneral(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case ADD:
			case DELETE:
			case INIT:
			case LIST:
			case UPDATE:
				deoOut = new WarehouseReturnDEO(null, "Für die Zone nicht unterstütztes Kommando", Status.INFO);
				break;
			case CLOSE:
				deoOut = new WarehouseReturnDEO(null, "Server-Verbindung wird beendet", Status.OK);
				run = false;
				break;
			case END:
				deoOut = new WarehouseReturnDEO(null, "Server-Verbindung und Server wird beendet", Status.OK);
				run = false;
				Server.run = false;
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando", Status.ERROR);
				break;
		}
		return deoOut;
	}

	/**
	 * Verarbeitet alle Anfragen, die direkt vom Kassenterminal kommen (Verkauf, Login, Abschluss).
	 * Speichert den Schicht-Startbestand, berechnet den korrekten Soll-Bestand und führt den Abschluss durch.
	 * * @param deoIn Das empfangene Datenpaket des Kassen-Clients.
	 * @return Die Antwort (z. B. Erfolg bei Verkauf oder Kassenstand beim Abschluss).
	 */
	private WarehouseReturnDEO handleZoneKasse(WarehouseDEO deoIn) {
		WarehouseReturnDEO deoOut;
		switch (deoIn.getCommand()) {
			case LOGIN:
				if (deoIn.getData() != null && deoIn.getData() instanceof thw.edu.javaII.port.warehouse.model.Kassierer) {
					thw.edu.javaII.port.warehouse.model.Kassierer req = Cast.safeCast(deoIn.getData(), thw.edu.javaII.port.warehouse.model.Kassierer.class);
					thw.edu.javaII.port.warehouse.model.Kassierer k = store.getKassiererByNummer(req.getNummer());

					if (k != null && k.getPin().equals(req.getPin())) {
						// --- NEU: Den Startbestand beim Login serverseitig abspeichern ---
						aktuellerStartBestand = req.getStartBestand();

						deoOut = new WarehouseReturnDEO(k, "Login erfolgreich", Status.OK);
					} else {
						deoOut = new WarehouseReturnDEO(null, "Ungültige Nummer oder PIN", Status.ERROR);
					}
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case KAUFEN:
				if (deoIn.getData() != null && deoIn.getData() instanceof thw.edu.javaII.port.warehouse.model.Kassenzettel) {
					thw.edu.javaII.port.warehouse.model.Kassenzettel zettel = Cast.safeCast(deoIn.getData(), thw.edu.javaII.port.warehouse.model.Kassenzettel.class);

					try {
						// 1. Zettel und Positionen in DB speichern (für die Historie)
						store.saveKassenzettel(zettel);

						// 2. Lagerbestand reduzieren
						for (thw.edu.javaII.port.warehouse.model.KassenzettelPosition pos : zettel.getPositionen()) {
							store.reduceLagerbestand(pos.getProdukt().getId(), pos.getAnzahl());
						}

						// --- NEU: Umsatz der aktuellen Schicht berechnen ---
						// Wir addieren den Umsatz NUR, wenn der Kunde in Bar gezahlt hat!
						if ("Bar".equalsIgnoreCase(zettel.getZahlart())) {
							aktuellerSchichtUmsatz += zettel.getGesamtpreis();
						}

						deoOut = new WarehouseReturnDEO(null, "Bezahlung erfolgreich verbucht", Status.OK);
					} catch (thw.edu.javaII.port.warehouse.model.exception.NegativeStockException e) {
						deoOut = new WarehouseReturnDEO(null, "Bestandsfehler: " + e.getMessage(), Status.ERROR);
					} catch (Exception e) {
						deoOut = new WarehouseReturnDEO(null, "Fehler beim Speichern: " + e.getMessage(), Status.ERROR);
					}
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;

			case ABSCHLUSS_LADEN:
				// --- NEU: Wir fragen nicht mehr die fehleranfällige Datenbank ab! ---
				// Stattdessen nutzen wir unseren exakten, internen Schicht-Zähler.
				deoOut = new WarehouseReturnDEO(new double[]{aktuellerStartBestand, aktuellerSchichtUmsatz}, "Abschlussdaten geladen", Status.OK);
				break;

			case ABSCHLUSS_SPEICHERN:
				if (deoIn.getData() != null && deoIn.getData() instanceof thw.edu.javaII.port.warehouse.model.Kassenabschluss) {
					thw.edu.javaII.port.warehouse.model.Kassenabschluss abschluss = Cast.safeCast(deoIn.getData(), thw.edu.javaII.port.warehouse.model.Kassenabschluss.class);

					// Für die Historie/Statistik in der DB speichern
					store.saveKassenabschluss(abschluss);

					// --- NEU: Kasse "leeren" nach dem Abschluss (Simulation der Geldabgabe an die Bank/Tresor) ---
					// Sowohl der Startbestand als auch die gezählten Einnahmen werden genullt!
					aktuellerStartBestand = 0.0;
					aktuellerSchichtUmsatz = 0.0;

					deoOut = new WarehouseReturnDEO(null, "Kassenabschluss gespeichert", Status.OK);
				} else {
					deoOut = new WarehouseReturnDEO(null, "Falsche Daten übergeben", Status.ERROR);
				}
				break;
			case KASSENZETTEL_LISTE:
				deoOut = new WarehouseReturnDEO(store.getAllKassenzettel(), "Kassenzettel geladen", Status.OK);
				break;
			default:
				deoOut = new WarehouseReturnDEO(null, "Unbekanntes Kommando in Zone KASSE", Status.ERROR);
				break;
		}
		return deoOut;
	}
}