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
import thw.edu.javaII.port.warehouse.model.KassenzettelPosition;
import thw.edu.javaII.port.warehouse.model.exception.NegativeStockException;

public interface IStorage {

	void initLager(List<Lager> list);

	void addLager(Lager model);

	void updateLager(Lager model);

	void deleteLager(Lager model);

	List<Lager> getLagers();

	void initLagerPlatz(List<LagerPlatz> list);

	void addLagerPlatz(LagerPlatz model);

	void updateLagerPlatz(LagerPlatz model);

	void deleteLagerPlatz(LagerPlatz model);

	List<LagerPlatz> getLagerPlatzs();

	void initLagerBestand(List<LagerBestand> list);

	void addLagerBestand(LagerBestand model);

	void updateLagerBestand(LagerBestand model);

	void deleteLagerBestand(LagerBestand model);

	List<LagerBestand> getLagerBestands();

	void initProdukt(List<Produkt> list);

	void addProdukt(Produkt model);

	Produkt getProduktByModel(Produkt model);

	void updateProdukt(Produkt model);

	void deleteProdukt(Produkt model);

	List<Produkt> getProdukts();
	
	void initDemo(List<DemoModel> list);
	
	void addDemo(DemoModel model);
	
	List<DemoModel> getDemos();

	// Kassen-Erweiterungen
	void initKassenTabellen();

	Kassierer getKassiererByNummer(int nummer);

	void saveKassenzettel(Kassenzettel kassenzettel);

	void saveKassenabschluss(Kassenabschluss abschluss);

	Kassenabschluss getLastKassenabschluss();

	void reduceLagerbestand(int produktId, int anzahl) throws NegativeStockException;

	double getBargeldEinnahmenSeitLetztemAbschluss();

}