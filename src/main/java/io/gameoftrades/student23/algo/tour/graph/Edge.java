package io.gameoftrades.student23.algo.tour.graph;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;

/**
 * An implementation of an undirected graph edge.
 */
public class Edge implements Comparable<Edge> {

    // vertices
    private Coordinaat from, to;

    // weight
    private int weight;
    private Pad path;

    /**
     * Constructor, needs both vertices and a path. The path will be used to calculate the weight and preserve the
     * path to be used later.
     * @param from vertex 1
     * @param to vertex 2
     * @param path the path
     */
    public Edge(Coordinaat from, Coordinaat to, Pad path) {
        this.from = from;
        this.to = to;
        setWeight(path);
    }

    public void setWeight(Pad path) {
        this.path = path;
        this.weight = path != null ? path.getTotaleTijd() : 0;
    }

    public int getWeight() {
        return weight;
    }

    public Pad getPath() {
        return path;
    }

    public Coordinaat getFrom() {
        return from;
    }

    public Coordinaat getTo() {
        return to;
    }

    public Edge reverse() {
        return new Edge(to, from, path.omgekeerd());
    }

    public boolean has(Coordinaat vertex) {
        return to.equals(vertex) || from.equals(vertex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge that = (Edge) obj;

            return this.has(that.getFrom()) && this.has(that.getTo());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge(" + from + ", " + to + ", " + weight + ")";
    }

    @Override
    public int compareTo(Edge o) {
        return o.getWeight() - weight;
    }
}
