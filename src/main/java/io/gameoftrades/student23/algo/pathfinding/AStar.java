package io.gameoftrades.student23.algo.pathfinding;

import java.util.HashMap;
import java.util.Map;
import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.student23.kaart.PadImpl;

/**
 * Implementatie van het A* snelste pad algoritme voor de Game of Trades wereld.
 *
 * Hier volgt een beschrijving van het algoritme en hoe deze wordt toegepast op de aangeleverde wereldstructuur.
 *
 * Het algoritme heeft een aantal elementen:
 * - een 'open' map, hier komen alle onbezochte coordinaten in met een kostenelement `f`.
 * - een 'closed' map, hier komen alle bezochte coordinaten in met een kostenelement `f`.
 *
 * Het kostenelement van hierboven wordt gegeven door een functie f(x) = g(x) + h(x). Waar x de eerstvolgende node is,
 * g(x) de koste is vanaf het startpunt tot node `x`, en h(x) schatting van de overige koste.
 * h(x) is in deze implementatie een afstandsberekening tot het eindpunt. Hierbij is niet de diagonale afstand genomen,
 * maar de Manhattan-afstand.
 *
 * Het algoritme:
 * Stap 1.  Initialiseer de open lijst
 *
 * Stap 2.  Initialiseer de gesloten lijst
 *     Voeg de startnode toe aan de open lijst met f = 0.
 *
 * Stap 3. [Herhaal] tot de open lijst leeg is:
 *      a)  neem de node met de kleinste `f`, noem deze `q`
 *      b)  haal `q` uit de open lijst
 *      c)  neem alle naburige nodes van `q`
 *      d)  [voor] elke buurnode:
 *          i)      als de buurnode het eindpunt is, stop met zoeken.
 *                  zet g(buurnode) = g(q) + afstand van q naar buurnode.
 *                  zet h(buurnode) = manhattanafstand tot het eindpunt
 *                  zet f(buurnode) = g(buurnode) + h(buurnode)
 *          ii)     als een node op dezelfde positite al in de open lijst staat met een lagere `f`, skip deze buurnode
 *          iii)    als een node op dezelfde positie al in de gesloten lijst staat met een lagere `f`, skip deze buurnode.
 *                  anders, voeg node toe aan open lijst.
 *          einde [voor]
 *      e)  zet q in de gesloten lijst
 *      einde [herhaal]
 *
 */
public class AStar implements SnelstePadAlgoritme, Debuggable {

    private int leastDistance;
    private Map<Coordinaat, Integer>  open;
    private Map<Coordinaat, Integer>  closed;
    private Map<Coordinaat, Pad>  paths;
    private Kaart kaart;

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat end) {
        this.kaart = kaart;
        leastDistance = Integer.MAX_VALUE;

        // stap 1. en 2.

        // initialize maps
        open = new HashMap<>();
        paths = new HashMap<>();
        closed = new HashMap<>();

        // voeg startnode aan open lijst toe
        open.put(start, 0);
        paths.put(start, new PadImpl(kaart, start));

        // Stap 3.
        while (!open.isEmpty()) {
            Pad found = search(start, end);
            if (found != null) {
                return found;
            }
        }

        // Hele kaart is doorzocht, maar geen eindpunt gevonden.
        return null;
    }

    private Pad search(Coordinaat start, Coordinaat end) {
        // neem node met laagste `f`
        Terrein leastDistanceTerrain = kaart.getTerreinOp(getLeastDistanceCoordinate(open));
        Pad leastDistancePath = paths.get(leastDistanceTerrain.getCoordinaat());

        // [voor] elk naburige node
        for (Richting direction : leastDistanceTerrain.getMogelijkeRichtingen()) {
            // zet g(x) = g(q) + afstand q->buur
            PadImpl newPath = new PadImpl(kaart, start, leastDistancePath.getBewegingen());
            newPath.addRichting(direction);

            // Neem het coordinaat
            Coordinaat neighbourCoord = kaart.kijk(leastDistanceTerrain, direction).getCoordinaat();

            // check d.i, d.ii, d.iii
            Pad result = evaluateNeighbour(neighbourCoord, end, newPath);

            // als buurnode het eindpunt is, stop met zoeken. result is null als het einde niet is bereikt.
            if (result != null) {
                // teken pad naar kaart
                debug.debugPad(kaart, start, result);
                return result;
            }
        }

        // voeg q toe aan gesloten lijst
        closed.put(leastDistanceTerrain.getCoordinaat(), leastDistance);

        // teken f naar de kaart
        debug.debugCoordinaten(kaart, open, closed);
        return null;
    }

    /**
     * Voert de stappen in stap 3.d uit op een coordinaat.
     * @param neighbour het te checken coordinaat
     * @param end het eindcoordinaat
     * @param newPath het pad naar het coordinaat `neighbour`
     * @return een Pad als het eindpunt is gevonden, null als dat niet zo is.
     */
    private Pad evaluateNeighbour(Coordinaat neighbour, Coordinaat end, Pad newPath) {
        // if the successor is the goal
        if (end.equals(neighbour)) {
            // stop search
            return newPath;
        }

        // calculate distance from the current coordinate to the end
        int h = (int) getManhattanDistance(neighbour, end);
        int f = h + newPath.getTotaleTijd();

        // if a node is already in the open list and has lower f, skip
        if (open.containsKey(neighbour) && open.get(neighbour) < f) {
            return null;
        }

        // if a node is already in the closed list and has a lower f, skip, else add node to open list
        if (!(closed.containsKey(neighbour) && closed.get(neighbour) < f)) {
            open.put(neighbour, f);
        }

        // add the new path to the list
        if (!paths.containsKey(neighbour)) {
            paths.put(neighbour, newPath);
        }

        return null;
    }

    /**
     * Voert stap 3.a uit. Selecteert het Coordinaat met de laagste `f` uit een map.
     * @return een Coordinaat
     */
    private Coordinaat getLeastDistanceCoordinate(Map<Coordinaat, Integer> list) {
        Map.Entry<Coordinaat, Integer> min = null;
        // selecteer de Coordinaat met de laagste waarde
        for (Map.Entry<Coordinaat, Integer> entry : list.entrySet()) {
            if (min == null || entry.getValue() < min.getValue()) {
                min = entry;
            }
        }

        // pop the coordinate from the open list, set least distance param
        leastDistance = list.remove(min.getKey());

        return min.getKey();
    }

    /**
     * Berekent manhattan-afstand van punt 1 tot punt 2.
     * @param from Coordinaat vanaf
     * @param to Coordinaat naar
     * @return de afstand
     */
    private double getManhattanDistance(Coordinaat from, Coordinaat to) {
        int absDiffX = Math.abs(from.getX() - to.getX());
        int absDiffY = Math.abs(from.getY() - to.getY());
        return absDiffX + absDiffY;
    }

    // DEBUG

    private Debugger debug = new DummyDebugger();

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }
}
