package io.gameoftrades.student23.algo.tour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student23.algo.tour.graph.Edge;
import io.gameoftrades.student23.algo.tour.graph.Graph;

import java.util.*;

public class TwoOpt implements StedenTourAlgoritme, Debuggable {

    private Graph graph;
    private int loopAmount = 50;

    @Override
    public List<Stad> bereken(Kaart map, List<Stad> cities) {
        // 1 of minder steden geeft dezelfde lijst
        if (cities != null && cities.size() <= 1) {
            return cities;
        } else if (cities == null) {
            return null;
        }

        // kopieer lijst met steden en randomize
        List<Stad> tour = new ArrayList<>(cities);
        Collections.shuffle(tour);

        // bereken alle paden
        graph = createCompleteGraph(map, tour);

        debugger.debugSteden(map, tour);

        // two opt loop
        tour = twoOpt(map, tour);

        System.out.println(getTotalWeight(tour));
        return tour;
    }

    /**
     * De two-opt loop.
     * @param map Kaart object, wordt alleen gebruikt voor debug
     * @param tour lijst met steden
     */
    private List<Stad> twoOpt(Kaart map, List<Stad> tour) {
        int loops = 0;
        while (loops < loopAmount) {
            // pak de totale lengte van de huidige tour

            int distance = getTotalWeight(tour);
            List<Stad> newTour = improve(map, tour);

            int newDistance = getTotalWeight(newTour);

            // vervang oude tour als nieuwe tour een kortere afstand heeft
            if (newDistance < distance) {
                tour = newTour;
                debugger.debugSteden(map, newTour);
            }

            loops++;
        }
        return tour;
    }

    private List<Stad> improve(Kaart map, List<Stad> tour) {
        int size = tour.size();
        int bestDistance = getTotalWeight(tour);

        for (int i = 1; i < size - 1; i++) {
            for (int k = i + 1; k < size; k++) {
                // wissel steden
                List<Stad> newTour = twoOptSwap(tour, i, k);

                // neem totale lengte
                int newDistance = getTotalWeight(newTour);

                // vervang oude tour als nieuwe tour een kortere afstand heeft
                if (newDistance < bestDistance) {
                    tour = newTour;
                    bestDistance = newDistance;
                    debugger.debugSteden(map, newTour);
                }
            }
        }
        return tour;
    }

    /**
     * Wisselt een aantal steden uit een tour om m.b.v. two-opt swap en geeft de nieuwe tour.
     * @param tour Lijst met steden, de huidige tour
     * @param i eerste wissel index
     * @param k tweede wissel index
     * @return nieuwe tour
     */
    private List<Stad> twoOptSwap(List<Stad> tour, int i, int k) {
        List<Stad> newTour = new ArrayList<>();

        for (int a = 0; a < i; a++) {
            newTour.add(tour.get(a));
        }

        int dec = 0;
        for (int a = i; a <= k; a++) {
            newTour.add(tour.get(k - dec));
            dec++;
        }

        for (int a = k + 1; a < tour.size(); a++) {
            newTour.add(tour.get(a));
        }

        return newTour;
    }

    /**
     * Creeert een Graph object aan de hand van een kaart en een lijst met steden. In dit object worden alle steden met
     * elkaar verbonden. Gebruikt het A* algoritme om het kortste pad tussen steden te vinden.
     * @param map de kaart
     * @param cities lijst met steden
     * @return een volledige verbonden graaf
     */
    private Graph createCompleteGraph(Kaart map, List<Stad> cities) {
        Graph graph = new Graph();

        List<Coordinaat> coords = new ArrayList<>();
        for (Stad city : cities) {
            coords.add(city.getCoordinaat());
        }

        graph.makeComplete(map, coords);

        return graph;
    }

    /**
     * Berekent de totale lengte van een tour aan de hand  van afstanden uit een graaf.
     * @param tour lijst met steden op volgorde
     * @return totale lengte van de tour
     */
    private int getTotalWeight(List<Stad> tour) {
        int total = 0;
        Stad current = tour.get(0);

        for (Stad next : tour) {
            // skip de eerste stad
            if (!current.equals(next)) {
                // pak het pad tussen de steden
                Edge edge = graph.getEdge(current.getCoordinaat(), next.getCoordinaat());
                // tel lengte bij het totaal op als het pad bestaat
                if (edge != null) {
                    total += edge.getWeight();
                }
                current = next;
            }
        }
        return total;
    }

    Debugger debugger = new DummyDebugger();

    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    public void setLoopAmount(int loopAmount) {
        this.loopAmount = loopAmount;
    }
}
