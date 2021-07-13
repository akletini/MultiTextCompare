# README - MultiTextCompare

## Voraussetzungen

- Eclipse IDE Luna SR2
- JDK 7u75 mit gesetztem JAVA_HOME
- Git >2.26

---

**Note**: Java 1.7 ist empfohlen, Java 1.8 ist die höchste getestete Java-Version um die Anwendung über die JAR-Datei zu starten.

---

## Setup

Clonen Sie folgendes Repository:

```
$ git clone https://gerona.nt.fh-koeln.de/gitlab/syp20/team06.git
```

oder verwenden Sie das per Google Drive übertragene Workspace.

## Run-Commands

Starten Sie MultiTextCompare wie folgt falls Sie eine 64 Bit JVM verwenden:

```
$ java -jar MultiTextCompare.jar
```

ansonsten kann es sein, dass bei einer 32 Bit JVM Ressourcen (CPU, RAM) nicht vollständig ausgenutzt werden.
Dies kann aber mit

```
$ java -Xms512m -Xmx2g -XX:MaxHeapSize=1g -jar MultiTextCompare.jar
```

auch umgangen werden.

Falls die Anwendung über Eclipse gestartet wird, kann die Limitation der Ressourcen unter Run -> Run configuration -> VM arguments = [-Xms512m -Xmx2g -XX:MaxHeapSize=1g] geändert werden.

---

**Note**: Bei einem Start über die JAR-Datei muss diese im Verzeichnis `MultiTextCompare` liegen.

---

## Code-Base

Die Sourcen befinden sich im Verzeichnis `MultiTextCompare/src/de/thkoeln/syp/mtc/*`.

Die Test-Cases & Files sind unter `MultiTextCompare/src/test/*` zu finden.

Die Konfigurationsdatei liegt standardmässig unter `MultiTextCompare/configs/config.properties`.

Aus den Javadoc Kommentaren kann in Eclipse über einen Rechtsklick auf `MultiTextCompare -> Export -> Java -> Javadoc` eine aktuelle HTML Dokumentation erzeugt werden. Diese sollte in den Ordner `MultiTextCompare/doc/*` exportiert werden und kann über die Anwendung MultiTextCompare selbst eingesehen werden..

Aus den JUnit Testfällen kann in Eclipse über einen Rechtsklick auf den Ordner `MultiTextCompare -> Export -> General -> Ant Buildfiles` eine aktuelle HTML Dokumentation erzeugt werden. Diese sollte in den Ordner `MultiTextCompare/junit/*` exportiert werden.

Eine ausführbare JAR-Datei kann über `MultiTextComapre -> Export -> Java -> Runnable JAR file` erzeugt werden.
