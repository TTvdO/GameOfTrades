package io.gameoftrades.student23.algo.pathfinding;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.student23.kaart.PadImpl;

import java.util.*;

/**
 * Een implementatie van Dijksta's algoritme voor de Game of Trades wereld.
 *
 * Hier volgt een beschrijving van het algoritme en hoe deze wordt toegepast op de aangeleverde wereldstructuur.
 *
 * In veel implementaties spreekt men over 'Nodes' of 'Vertices', deze komen overeen met de Coordinaat objecten.
 *
 * Dit algoritme heeft een aantal elementen:
 * - Een lijst met bezochte nodes
 * - Een lijst met onbezochte nodes
 * - Een lijst met afstanden tot het beginpunt
 *
 * Stap 1:  Laat alle nodes onbezocht zijn.
 * Stap 2:  Zet een voorlopige afstand voor elke node, 0 voor de initiele node, oneindig voor elke andere node.
 *          Laat de initiele node de huidige node zijn.
 * Stap 3:  Voor de huidige node, bereken de voorlopige afstand tot zijn aangrenzende nodes. Vergelijk de nieuw
 *          berekende afstand met de huidige afstand tot de node, laat de kleinste waarde de nieuwe afstand zijn.
 *          Deze afstand is de afstand vanaf het beginpunt.
 * Stap 4:  Markeer de huidige node als bezocht. Een bezochte node zal niet opnieuw worden bekeken.
 * Stap 5:  Wanneer de bestemming gemarkeerd is als bezocht of als er geen verbinging tussen de begin- en eind-node is,
 *          kan het algoritme gestopt worden. Anders kan deze stap worden overgeslagen.
 * Stap 6:  Neem de node met de kortste afstand tot de de begin node als huidige node. Ga naar stap 3.
 *
 */
public class Dijkstra implements SnelstePadAlgoritme, Debuggable {

    private List<Coordinaat> unvisited;
    private List<Coordinaat> visited;
    private Map<Coordinaat, Integer> distances;

    // the map
    private Kaart kaart;

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat end) {
        // initialisatie, stap 1
        this.kaart = kaart;
        distances = new HashMap<>();
        visited = new ArrayList<>();
        unvisited = new ArrayList<>();

        // stap 2: zet afstand op de eerste node naar 0
        distances.put(start, 0);

        // stap 3
        Coordinaat current = dijkstra(start, end);

        if (end.equals(current)) {
            // maak een pad met backtracking
            Pad path = backtrack(start, end);
            debug.debugPad(kaart, start, path);

            return path;
        } else {
            return new PadImpl(kaart, start);
        }
    }

    private Coordinaat dijkstra(Coordinaat start, Coordinaat end) {
        // laat de eerste node de huidige node zijn
        Coordinaat current = start;
        while (current != null && !end.equals(current)) { // stap 5: stop algoritme als de bestemming is bezocht
            // stap 3: bereken de afstanden van de aangrenzende nodes
            calculateNeighbourDistances(current);

            // stap 4: zet node als bezocht
            visited.add(current);

            // selecteer de node met de korste afstand tot start
            Coordinaat minDistCoord = getMinimalDistanceCoordinate();
            unvisited.remove(minDistCoord);

            // zet huidige node naar de kortste
            current = minDistCoord;

            debug.debugCoordinaten(kaart, distances);
        }
        // einde algoritme
        return current;
    }

    private Coordinaat getMinimalDistanceCoordinate() {
        Coordinaat minDistCoord = null;
        for (Coordinaat coord : unvisited) {
            if (!visited.contains(coord) &&
                    (minDistCoord == null || distances.get(minDistCoord) > distances.get(coord))) {
                minDistCoord = coord;
            }
        }
        return minDistCoord;
    }

    /**
     * Rekent het kortste pad van begin naar eind.
     * @param start start Coordinaat
     * @param end eind Coordinaat
     * @return het kortste pad
     */
    private Pad backtrack(Coordinaat start, Coordinaat end) {
        PadImpl pad = new PadImpl(kaart, end);

        // neem bestemming als huidige node
        Coordinaat current = end;

        while (!start.equals(current)) {
            // voeg richting toe met de kleinste afstand
            pad.addRichting(getLeastDistanceDirection(current));

            // zet huidige node naar de laatste in het pad
            current = pad.volg(end);
        }

        // neem het omgekeerde pad, dus van start naar eind
        return pad.omgekeerd();
    }

    /**
     * Geeft de richting naar het coordinaat met de kortste afstand uit de `distances` kaart vanaf het gegeven
     * coordinaat.
     * @param from vanaf Coordinaat
     * @return de richting met de
     */
    private Richting getLeastDistanceDirection(Coordinaat from) {
        Richting minDirection = null;
        int distance = Integer.MAX_VALUE;

        // voor alle mogelijke richting vanaf het coordinaat
        for (Richting direction : kaart.getTerreinOp(from).getMogelijkeRichtingen()) {
            Coordinaat coord = from.naar(direction);
            // neem kleinste afstand
            if (distances.containsKey(coord) && (minDirection == null ||  distances.get(coord)< distance)) {
                minDirection = direction;
                distance = distances.get(coord);
            }
        }

        return minDirection;
    }

    /**
     * Berekend de afstand voor de naburige coordinaten en zet deze in de distances map.
     * @param from vanaf Coordinaat
     */
    private void calculateNeighbourDistances(Coordinaat from) {
        Terrein terrain = kaart.getTerreinOp(from);

        for (Richting direction : terrain.getMogelijkeRichtingen()) {
            // neem het nieuwe terrein in de richting
            Terrein newTerrain = kaart.kijk(terrain, direction);
            Coordinaat newCoord = newTerrain.getCoordinaat();

            // als het coordinaat nog niet bezocht is
            if (!visited.contains(newCoord)) {
                // voeg toe aan unvisited als deze er nog niet instaat
                if (!unvisited.contains(newCoord)) {
                    unvisited.add(newCoord);
                }

                // bereken afstand dmv bewegingspunten
                int distance = distances.get(from) + newTerrain.getTerreinType().getBewegingspunten();

                if (!distances.containsKey(newCoord)
                        || (distances.containsKey(newCoord) && distances.get(newCoord) > distance)) {
                    distances.put(newTerrain.getCoordinaat(), distance);
                }
            }
        }
    }

    // DEBUG

    private Debugger debug = new DummyDebugger();

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }
}
