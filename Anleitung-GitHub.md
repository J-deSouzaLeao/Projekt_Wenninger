# 🚀 Team-Collaboration: Git & GitHub Workflow

Diese Anleitung erklärt kurz und knapp, wie wir gemeinsam an diesem Projekt arbeiten, ohne uns gegenseitig Code zu überschreiben.

---

## 1. Das Grundprinzip
Wir nutzen ein **zentrales Repository** auf GitHub. Jeder arbeitet lokal auf seinem Rechner in einer eigenen Kopie (**Branch**). Wenn ein Feature fertig ist, wird es zurück in den Hauptzweig (**main**) geführt.

---

## 2. Der tägliche Workflow in IntelliJ

### 🟢 Schritt 1: Projekt aktualisieren
Bevor du startest, hol dir die Änderungen der Kollegen:
*   Klicke oben rechts auf den **blauen Pfeil nach unten** (Update Project) oder drücke `Strg + T`.

### 🟡 Schritt 2: Einen Branch erstellen
Arbeite **nie direkt im main-Branch**.
1.  Klicke unten rechts in der Statusleiste auf `main`.
2.  Wähle `+ New Branch`.
3.  Benenne ihn nach dem Schema: `feature/dein-name-beschreibung` (z.B. `feature/max-login-fix`).

### 🔵 Schritt 3: Änderungen speichern & hochladen
Wenn dein Code-Teil fertig ist:
1.  Drücke `Strg + K` (Commit).
2.  Wähle deine Dateien aus und schreibe eine kurze Nachricht (was hast du geändert?).
3.  Klicke auf den kleinen Pfeil bei "Commit" und wähle **Commit and Push** (`Strg + Alt + K`).
4.  Bestätige den Dialog mit **Push**. Dein Branch ist nun auf GitHub sichtbar.

### 🔴 Schritt 4: Zusammenführen (Pull Request)
Damit dein Code in das Hauptprojekt fließt:
1.  Gehe auf GitHub zum Repository.
2.  Klicke auf den gelben Button **Compare & pull request**.
3.  Erstelle den Pull Request. Sobald ein Kollege drübergeschaut hat, kann er "Merged" werden.

---

## 3. Goldene Regeln für reibungslose Arbeit
*   **Kleine Schritte:** Lieber viele kleine Commits als ein riesiger am Ende der Woche.
*   **Aussagekräftige Nachrichten:** "Bugfix im Header" ist besser als "fix".
*   **Keine Angst vor Konflikten:** Wenn IntelliJ sagt "Merge Conflict", keine Panik. Nutze das Tool von IntelliJ (`Merge...`), um zu entscheiden, welcher Code behalten werden soll (Links = Du, Rechts = Die anderen, Mitte = Das Ergebnis).

---
*Viel Erfolg beim Coden!* 💻