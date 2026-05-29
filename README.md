# MyLagerMain - Lager- und Kassenverwaltung (Port Warehouse)

## Projektbeschreibung
Dieses Projekt ist eine umfangreiche Java-Client-Anwendung mit einer grafischen Benutzeroberfläche (Java Swing). Sie dient der effizienten Verwaltung von Lagerbeständen (Produkte, Lagerplätze) sowie der Abwicklung von Verkäufen über ein vollständig integriertes Kassensystem (Point of Sale). Die Anwendung kommuniziert über eine Client-Server-Architektur (TCP-Sockets) mit einem zentralen Backend, welches die Datenhaltung und Geschäftslogik sicherstellt.

## Starten des Programms

* `Server.java` – Muss als erstes gestartet werden und immer laufen.
* `KassenLoginTest.java` – Haupteinstiegspunkt für das Kassen-Modul (POS).
* `Oberflaeche.java` – Haupteinstiegspunkt für die Lagerverwaltung

## Anmeldedaten
### Kassierer
**Max Mustermann:**
* ID: 1001
* PIN: 1234

**Chef Autorisierung (Manager):**
* ID: 9999
* PIN: 0000

### Lagerverwaltung
**User1:**
* Benutzer: user
* Passwort: pass

**User2:**
* Benutzer: jsh
* Passwort: jsh123

**User3:**
* Benutzer: mmn
* Passwort: mmn123

## Features & Funktionsumfang

### Lagerbetrieb & Stammdaten
* **Produkte & Lagerplätze:** Intuitives Anlegen, Bearbeiten und Löschen von Produkten und Lagerstandorten.
* **Bestandsführung:** Schnelles Ein- und Ausbuchen von Warenmengen inklusive Live-Preisberechnung.
* **Übersicht & Suche:** Tabellarische Bestandsansichten mit intelligenten Live-Filtern (z. B. kombinierte Suche nach ID, Name oder Datum).
* **Statistiken:** Automatische Auswertungen (z. B. Top- und Flop-Artikel).

### Kassensystem (Point of Sale)
* **Touch-optimierte Kassen-UI:** Dynamisches Numpad zur schnellen Eingabe von Artikelnummern und Mengen – ideal für Touchscreens.
* **Bezahlvorgang & Wechselgeld:** Automatische Rückgeldberechnung bei Barzahlung inkl. Sicherheitsprüfung, ob ausreichend Wechselgeld in der Kasse vorhanden ist.
* **Storno-Sicherheit:** Integrierter Schutzmechanismus, der nach mehreren Stornierungen automatisch eine Manager-PIN anfordert.
* **Tagesabschluss:** Interaktiver Kassenabschluss mit Zählhilfe für Scheine und Münzen inkl. Soll-/Ist-Abgleich.
* **Rechte- & Personalverwaltung:** Kassierer-Logins, Manager-Freigaben, PIN-Reset-Funktion und ein Admin-Panel zur Verwaltung der Mitarbeiter.

## Technologien & Architektur
* **Programmiersprache:** Java 17 (JDK)
* **GUI-Framework:** Java Swing (inkl. `MigLayout` für responsive Formulare)
* **Netzwerk:** Echte Client-Server-Kommunikation über `java.net.Socket`.
* **Datenübertragung:** Verwendung von Object-Streams (`ObjectInputStream` / `ObjectOutputStream`) und eigens definierten Data Exchange Objects (DEOs) für sichere Zonen- und Kommando-Trennung.
* **Threading:** Asynchrone Starter-Threads und saubere Trennung über den Event Dispatch Thread (EDT) zur Vermeidung von UI-Freezes.

## Projektstruktur (Auszug wichtiger Klassen)

* `Obeflaeche.java` – Haupteinstiegspunkt für das Lagerverwaltungs-System.
* `KassenLoginTest.java` – Haupteinstiegspunkt für das Kassen-Modul (POS).
* `BackendClient.java` – Der zentrale Kommunikations-Proxy (Netzwerk-Schicht) zum Server.
* `LagerUI.java` / `KassenUI.java` – Die jeweiligen Hauptfenster der beiden Programmteile.
* `LagerUIHandler.java` – Zentraler Controller für die Menüsteuerung und den dynamischen Ansichten-Wechsel.

## Autoren & Mitwirkende
* **Ursprünglicher Ersteller:** Tobias Wenninger
* **Entwicklung, Refactoring & Dokumentation:** Juan de Souza Leao & Barbara Liegnitz