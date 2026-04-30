# Lagerverwaltung System

Dieses Projekt ist eine Client-Server-Anwendung zur Lagerverwaltung, basierend auf Java und einer grafischen Benutzeroberfläche (Swing/MigLayout).

## 🛠 Voraussetzungen
* **Java SDK:** Java 17 (oder neuer)
* **IDE:** IntelliJ IDEA (empfohlen)

---

## 🚀 Setup-Anleitung für das Team

Da projektspezifische IntelliJ-Konfigurationen nicht auf GitHub geteilt werden, müssen nach dem Klonen des Repositories ein paar kurze Handgriffe erledigt werden, damit das Projekt fehlerfrei kompiliert.

### 1. Projekt klonen & vertrauen
* Klone das Repository in IntelliJ.
* Klicke beim ersten Öffnen zwingend auf **"Trust Project"**.

### 2. Grundkonfiguration (SDK & Sources)
1. Gehe in IntelliJ auf **File -> Project Structure...** (oder `Strg + Alt + Shift + S`).
2. Prüfe unter **Project**, ob bei **SDK** die korrekte Java-Version (z.B. Java 17) ausgewählt ist.
3. *Falls der `src`-Ordner nicht automatisch als Code erkannt wird:* Rechtsklick auf den `src`-Ordner im Projektbaum -> **Mark Directory as -> Sources Root**. (Der Ordner muss blau sein).

### 3. Bibliotheken einbinden (WICHTIG!)
Das Projekt benötigt zwei externe Bibliotheken aus dem `lib`-Ordner, die den Modulen explizit zugewiesen werden müssen, sonst stürzt das Programm beim Start ab.

Gehe in IntelliJ auf **File -> Project Structure...** und wechsle links auf **Modules**.

**A) MigLayout für die GUI einbinden:**
1. Wähle in der mittleren Spalte das Modul `thw.edu.javaII.port.warehouse.ui` aus.
2. Gehe rechts auf den Reiter **Dependencies**.
3. Klicke auf das **`+`** Symbol -> **JARs or Directories...**
4. Wähle die Datei `miglayout15-swing.jar` aus dem `lib`-Ordner aus.
5. Setze den Haken daneben.

**B) SQLite-Datenbank für den Server einbinden:**
1. Wähle in der mittleren Spalte das Modul `thw.edu.javaII.port.warehouse.server` aus.
2. Gehe rechts auf den Reiter **Dependencies**.
3. Klicke auf das **`+`** Symbol -> **JARs or Directories...**
4. Wähle die Datei `sqlite-jdbc.jar` (oder ähnlich) aus dem `lib`-Ordner aus.
5. Setze den Haken daneben.
6. Bestätige alles mit **Apply** und **OK**.

### 4. Projekt neu bauen
Bevor du das Programm startest, zwinge IntelliJ dazu, die neuen Pfade zu laden:
* Klicke oben im Menü auf **Build -> Rebuild Project**.

---

## 🏃‍♂️ Das Programm starten

Das Projekt besteht aus zwei Teilen. **Der Server muss zwingend zuerst laufen**, da die GUI sonst ins Leere funkt.

1. **Server starten:** Führe die Main-Methode in der Server-Klasse (im `server`-Package) aus. Warte, bis die Meldung kommt, dass der Server läuft.
2. **GUI starten:** Führe die Main-Methode in der Klasse `Oberflaeche` (im `ui`-Package) aus.

Das Anmeldefenster sollte sich nun öffnen und du kannst dich einloggen!