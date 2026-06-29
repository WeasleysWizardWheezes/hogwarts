---
name: domaenenwissen
description: Verwende wenn du den fachlichen Kontext oder die Geschäftsdomäne verstehen musst bevor du Features implementierst
---

# Domänenwissen – Feuerwehr-Geräteverwaltung

## Zielgruppe

- **Primär:** Ehrenamtliche Gerätewarte kleiner/mittlerer Freiwilliger Feuerwehren
- **Sekundär:** Mannschaft (Checklisten), Wehrführung (Übersicht)
- **Problem:** Bestehende Software zu teuer, überladen, Desktop-orientiert

## Wichtiges Wissen

- **Gerät/Ausrüstung:** Atemschutz, Schläuche, Funkgeräte, Beladungsteile
- **Fahrzeug:** Feuerwehrfahrzeug mit nummerierten Geräteräumen
- **Beladung:** Gesamte Ausrüstung auf einem Fahrzeug, strukturiert nach Geräteräumen
- **Prüffrist:** Gesetzlich vorgeschriebener Zeitpunkt für nächste Prüfung eines Geräts
- **Status:** Einsatzbereit / In Prüfung / Defekt / Ausgesondert
- **Gerätewart:** Verantwortlicher für Wartung und Prüfung der Ausrüstung

## Geschäftsregeln

- Gesetzliche Prüfintervalle MÜSSEN eingehalten werden (haftungsrelevant)
- Jedes Gerät hat einen eindeutigen Status zu jedem Zeitpunkt
- Statusänderungen werden mit Zeitstempel und Verursacher protokolliert
- Prüfintervalle sind pro Gerätetyp unterschiedlich (z.B. Atemschutz jährlich, Schlauchprüfung alle 12 Monate)
- Nach Einsatz/Übung: Beladungskontrolle Pflicht

## Fachbegriffe (Ubiquitous Language)

| Begriff    | Bedeutung |
|------------|-----------|
| Gerätewart | Ehrenamtlicher Verantwortlicher für Ausrüstung |
| Beladung   | Gesamte Ausrüstung auf einem Fahrzeug |
| Geräteraum | Nummerierter Abschnitt eines Feuerwehrfahrzeugs |
| Prüffrist  | Gesetzlich vorgeschriebener nächster Prüfzeitpunkt |
| AAO        | Alarm- und Ausrückeordnung |
| FMS        | Funkmeldesystem (Fahrzeugstatus) |
| G26.3      | Arbeitsmedizinische Vorsorge Atemschutz |
| DGUV       | Deutsche Gesetzliche Unfallversicherung |

## Architektur-Implikationen

- Mobile-First: Hauptnutzung auf Smartphone in der Fahrzeughalle
- Einfach: Ehrenamtliche mit wenig IT-Affinität
- Schnell: Gerät identifizieren → Info in Sekunden
