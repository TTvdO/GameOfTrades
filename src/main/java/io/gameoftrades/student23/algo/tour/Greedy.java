package io.gameoftrades.student23.algo.tour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.student23.algo.tour.graph.Graph;
import io.gameoftrades.student23.algo.tour.graph.Edge;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Tim
 */
public class Greedy implements StedenTourAlgoritme, Debuggable{

    private List<Coordinaat> cities;
    private List<Coordinaat> closestCoords;
    private Graph graph;

    @Override
    public List<Stad> bereken(Kaart kaart, List<Stad> list) {
        //Nieuwe Graph om mee te werken.
        graph = new Graph();
        //Alle ArrayLists initialiseren.
        cities = new ArrayList<>();
        closestCoords = new ArrayList<>();

        // voeg alle steden in een lijst met coordinaten en hussel
        List<Stad> copy = new ArrayList<>(list);
        fillCities(copy);
        Collections.shuffle(cities);

        //Zorg dat de Graph klasse zijn eigen arraylists vult op basis van de kaart en de coordinaten van cities.
        graph.makeComplete(kaart, cities);

        // greedy geeft de totale tour lengte
        int totalWeight = greedy();
        
        // Zet lijst van coordinaten om naar lijst van steden.
        List<Stad> citiesInOrder = getCityList(closestCoords, copy);

        System.out.println(totalWeight);
        debugger.debugSteden(kaart, citiesInOrder);

        return citiesInOrder;
    }

    /**
     * Maakt de tour.
     * @return de totale lengte van de tour
     */
    private int greedy() {
        Coordinaat currentStad = cities.get(0);
        closestCoords.add(currentStad);
        int totalWeight = 0;
        while(closestCoords.size() != cities.size()){
            //Add alle neighbours van een coordinaat in een nieuwe arraylist om in een volgende for loop te behandelen
            List<Coordinaat> neighbours = graph.getNeighboursOf(currentStad);

            //Initialisatie van de lowestWeight en de currentBest.
            Edge currentBest = getBestEdge(currentStad, neighbours);

            //Telkens van elke route die je aanmaakt tussen twee steden deze bewegingspunten optellen bij de totale bewegingspunten om aan het einde uit te printten.
            totalWeight += currentBest.getWeight();

            //Remove alle referenties(edge, coordinaat) van de stad waar je vandaan kwam, zodat je hier niet terug naartoe kan bewegen.
            graph.removeVertex(currentStad);

            // ga naar de volgende stad en voeg toe aan volgorde
            currentStad = currentBest.getTo().equals(currentStad) ? currentBest.getFrom() : currentBest.getTo();
            closestCoords.add(currentStad);
        }

        return totalWeight;
    }

    /**
     * Selecteert de Edge met de kortste lengte vanaf de huidige stad
     * @param currentStad
     * @param neighbours
     * @return
     */
    private Edge getBestEdge(Coordinaat currentStad, List<Coordinaat> neighbours) {
        //Sla telkens tijdelijk de stad op die tot nu toe de optimale bestemming is
        int lowestWeight = Integer.MAX_VALUE;
        Edge currentBest = null;

        //Vind van alle neighbours van een coordinaat de neighbour waarbij je de minste bewegingspunten gebruikt.
        for(Coordinaat coordinates : neighbours){
            Edge edge = graph.getEdge(currentStad, coordinates);
            if(edge.getWeight() < lowestWeight){
                lowestWeight = edge.getWeight();
                currentBest = edge;
            }
        }

        return currentBest;
    }

    Debugger debugger = new DummyDebugger();
    
    @Override
    public void setDebugger(Debugger dbgr) {
        this.debugger = dbgr;
    }

    /**
     * Vult de cities lijst met de coordinaten van de gegeven steden
     * @param list
     */
    private void fillCities(List<Stad> list){
        for(Stad city : list){
            cities.add(city.getCoordinaat());
        }
    }

    /**
     * Creeert een lijst van steden in de volgorde van de coordinaten lijst, waarbij de coordinaten stadcoordinaten zijn.
     * @param coords
     * @param list
     * @return
     */
    private List<Stad> getCityList(List<Coordinaat> coords, List<Stad> list){
        List<Stad> citiesInOrder = new ArrayList<>();
        for(Coordinaat coord : coords){
            for(Stad stad : list){
                if(stad.getCoordinaat().equals(coord)){
                    citiesInOrder.add(stad);
                }
            }
        }
        return citiesInOrder;
    }
}
