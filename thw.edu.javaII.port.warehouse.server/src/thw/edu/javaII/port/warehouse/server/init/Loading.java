package thw.edu.javaII.port.warehouse.server.init;

import thw.edu.javaII.port.warehouse.init.Initizilaizer;
import thw.edu.javaII.port.warehouse.server.data.IStorage;

/**
 * Diese Klasse ist dafür zuständig, die Datenbank mit Startwerten zu füllen.
 * Sie funktioniert wie eine Brücke: Sie holt sich die fest einprogrammierten Testdaten
 * aus dem Initializer und schreibt sie über die Schnittstelle sauber in die Datenbank.
 * * @author juan.de.souza.leao
 */
public class Loading {

	/**
	 * Führt die komplette Initialisierung der Datenbank durch.
	 * Hierbei werden alte Testdaten (falls vorhanden) meist überschrieben und durch
	 * einen frischen Satz an Lagern, Produkten, Plätzen und Beständen ersetzt.
	 * * @param store Die Datenbankschnittstelle (z. B. unsere SQLite-Datenbank),
	 * in die die frischen Startdaten gespeichert werden sollen.
	 */
	public void initLoading(IStorage store) {
		Initizilaizer init = new Initizilaizer();
		store.initLager(init.getLager());
		store.initLagerBestand(init.getLagerbestand());
		store.initLagerPlatz(init.getLagerplatz());
		store.initProdukt(init.getProdukt());
		store.initDemo(init.getDemo());
	}

}