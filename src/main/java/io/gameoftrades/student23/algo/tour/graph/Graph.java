package io.gameoftrades.student23.algo.tour.graph;

import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.student23.algo.pathfinding.AStar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementatie van een ongerichte graaf.
 */
public class Graph {

    private List<Coordinaat> vertices;
    private List<Edge> edges;
    private Map<Coordinaat, List<Edge>> neighbours;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        neighbours = new HashMap<>();
    }

    /**
     * Getter voor edges.
     * @return edges
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Getter voor vertices.
     * @return vertices
     */
    public List<Coordinaat> getVertices() {
        return vertices;
    }

    /**
     * Getter voor neighbours
     * @return map met vertex->verbinding
     */
    public Map<Coordinaat, List<Edge>> getNeighbours() {
        return neighbours;
    }

    /**
     * Voegt een verbinding toe aan de graaf.
     * @param edge de verbinding toe te voegen
     */
    public void addEdge(Edge edge) {
        if (edge != null) {
            if (!edges.contains(edge)) {
                // nieuwe verbinding, voeg toe aan lijst met verbindingen
                edges.add(edge);

                // zet in lijst met naburige vertices voor beide de vanaf en naar vertices
                addNeighbour(edge.getFrom(), edge);
                addNeighbour(edge.getTo(), edge.reverse());
            } else {
                // verbinding staat al in de lijst
                Edge contained = edges.get(edges.indexOf(edge));
                if (contained.getWeight() > edge.getWeight()) {
                    // update als de lengte van de nieuwe verbinding korter is
                    if (contained.getTo().equals(edge.getTo())) {
                        contained.setWeight(edge.getPath());
                    } else {
                        contained.setWeight(edge.getPath().omgekeerd());
                    }
                }
            }
        }
    }

    /**
     * Voeg een naburige verbinding toe voor een vertex.
     * @param vetrex de vertex waaraan de verbinding toegevoegd moet worden
     * @param edge de verbinding
     */
    private void addNeighbour(Coordinaat vetrex, Edge edge) {
        if (vetrex != null && edge != null) {
            List<Edge> neighbourEdges = neighbours.get(vetrex);
            if (neighbourEdges != null && !neighbourEdges.contains(edge)) {
                neighbourEdges.add(edge);
            } else if (neighbourEdges == null) {
                neighbourEdges = new ArrayList<>();
                neighbourEdges.add(edge);
                neighbours.put(edge.getFrom(), neighbourEdges);
            }
        }
    }

    /**
     * Voegt een vertex toe aan de graaf.
     * @param vertex toe te voegen vertex
     */
    public void addVertex(Coordinaat vertex) {
        if (vertex != null && !vertices.contains(vertex)) {
            vertices.add(vertex);
        }
    }

    /**
     * Geeft de verbinding tussen twee coordinaten.
     * @param from coordinaat vanaf
     * @param to coordinaat naar
     * @return de verbinding, null als er geen verbinding is
     */
    public Edge getEdge(Coordinaat from, Coordinaat to){
        if (from != null && to != null && !from.equals(to)) {
            for (Edge edge : edges) {
                if (edge.has(from) && edge.has(to)) {
                    return edge;
                }
            }
        }
        return null;
    }

    /**
     * Verwijderd een verbinding uit de lijst
     * @param edge
     * @return
     */
    public boolean removeEdge(Edge edge) {
        if (edge != null && edges.contains(edge)) {
            removeNeighbourReferences(edge);

            return edges.remove(edge);
        }
        return false;
    }

    /**
     * Verwijderd alle referenties naar een verbinding uit de neighbours lijst
     * @param edge te verwijderen verbinding
     */
    private void removeNeighbourReferences(Edge edge) {
        for (Map.Entry<Coordinaat, List<Edge>> ref : neighbours.entrySet()) {
            ref.getValue().remove(edge);
        }
    }

    /**
     * Verwijderd een vertex uit de graaf.
     * @param vertex de te verwijderen vertex
     * @return true als vertex is verwijderd, false als niet of als de vertex niet bestaat in de graaf
     */
    public boolean removeVertex(Coordinaat vertex) {
        if (vertex != null) {
            for (Edge edge : neighbours.remove(vertex)) {
                removeEdge(edge);
            }

            return vertices.remove(vertex);
        }
        return false;
    }

    /**
     * Geeft een lijst met alle vertices waarbij een verbinding is vanaf een gegeven vertex.
     * @param vertex de vertex vanaf
     * @return lijst met verbonden vertices
     */
    public List<Coordinaat> getNeighboursOf(Coordinaat vertex) {
        if (neighbours.containsKey(vertex)) {
            List<Coordinaat> neighbouringVertices = new ArrayList<>();
            for (Edge edge : neighbours.get(vertex)) {
                // the neighbours map always has (origin, destination) format
                neighbouringVertices.add(edge.getTo());
            }

            return neighbouringVertices;
        }
        return null;
    }

    /**
     * Creeert een Graph object aan de hand van een kaart en een lijst met steden. In dit object worden alle steden met
     * elkaar verbonden. Gebruikt het A* algoritme om het kortste pad tussen steden te vinden.
     * @param map de kaart
     * @param coords lijst met coordinaten
     * @return een volledige verbonden graaf
     */
    public void makeComplete(Kaart map, List<Coordinaat> coords) {
        if (map != null && coords != null) {
            // voeg alle steden toe aan graaf
            for (Coordinaat coord : coords) {
                addVertex(coord);
            }
            // creeer snelstepadalgoritme
            SnelstePadAlgoritme pathfinder = new AStar();

            // verbind all steden aan elkaar met paden
            for (Coordinaat vertex : getVertices()) {
                for (Coordinaat otherVertex : getVertices()) {
                    // nooit een vertex aan zichzelf verbinden
                    if (!vertex.equals(otherVertex)) {
                        // bereken pad, voeg toe aan graaf
                        Pad path = pathfinder.bereken(map, vertex, otherVertex);

                        Edge edge = new Edge(vertex, otherVertex, path);
                        addEdge(edge);
                    }
                }
            }
        }
    }
}
