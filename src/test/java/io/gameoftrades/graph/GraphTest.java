package io.gameoftrades.graph;

import io.gameoftrades.model.kaart.*;
import io.gameoftrades.student23.algo.pathfinding.AStar;
import io.gameoftrades.student23.algo.tour.graph.Edge;
import io.gameoftrades.student23.algo.tour.graph.Graph;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphTest {

    private Graph graph;

    @Before
    public void before() {
        graph = new Graph();
    }

    @Test
    public void shouldCreateCompleteGraph() {
        Kaart kaart = new Kaart(10, 10);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                new Terrein(kaart, Coordinaat.op(i, j), TerreinType.GRASLAND);
            }
        }

        List<Coordinaat> coords = Arrays.asList(Coordinaat.op(1,1), Coordinaat.op(1,9),
                Coordinaat.op(9,1), Coordinaat.op(9,9));

        graph.makeComplete(kaart, coords);

        // check all edges
        for (Coordinaat from : coords) {
            for (Coordinaat to : coords) {
                if (!from.equals(to) && graph.getEdge(from, to) == null) {
                    fail("Edge from " + from + " to " + to + " is missing");
                }
            }
        }
    }

    @Test
    public void shouldCreateEdgeAndNeighbourReferences() {
        Kaart kaart = new Kaart(10, 10);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                new Terrein(kaart, Coordinaat.op(i, j), TerreinType.GRASLAND);
            }
        }

        Coordinaat from = Coordinaat.op(1,1);
        Coordinaat to = Coordinaat.op(9,9);

        AStar aStar = new AStar();
        Pad pad = aStar.bereken(kaart, from, to);
        Edge edge = new Edge(from, to, pad);

        graph.addVertex(from);
        graph.addVertex(to);
        graph.addEdge(edge);

        assertEquals(1, graph.getEdges().size());
        assertEquals(1, graph.getNeighbours().get(from).size());
        assertEquals(1, graph.getNeighbours().get(to).size());
        assertEquals(1, graph.getNeighboursOf(from).size());
        assertEquals(1, graph.getNeighboursOf(to).size());
    }

    @Test
    public void createdEdgeShouldRemoveIfVertexIsRemoved() {
        Kaart kaart = new Kaart(10, 10);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                new Terrein(kaart, Coordinaat.op(i, j), TerreinType.GRASLAND);
            }
        }

        Coordinaat from = Coordinaat.op(1,1);
        Coordinaat to = Coordinaat.op(9,9);

        AStar aStar = new AStar();
        Pad pad = aStar.bereken(kaart, from, to);
        Edge edge = new Edge(from, to, pad);

        graph.addVertex(from);
        graph.addVertex(to);
        graph.addEdge(edge);

        graph.removeVertex(from);

        assertEquals(1, graph.getVertices().size());
        assertEquals(0, graph.getEdges().size());
        assertEquals(0, graph.getNeighbours().get(to).size());
        assertEquals(0, graph.getNeighboursOf(to).size());
        assertNull(graph.getNeighboursOf(from));
    }


    @Test
    public void addEdgeDuplicateCouldBeUpdated() {
        Kaart kaart = new Kaart(10, 10);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                new Terrein(kaart, Coordinaat.op(i, j), TerreinType.GRASLAND);
            }
        }

        Coordinaat from = Coordinaat.op(1,1);
        Coordinaat to = Coordinaat.op(9,9);

        AStar aStar = new AStar();
        Pad pad = aStar.bereken(kaart, from, to);
        Pad rev = aStar.bereken(kaart, to, from);
        Edge edge = new Edge(from, to, pad);
        Edge revEdge = new Edge(to, from, rev);

        graph.addVertex(from);
        graph.addVertex(to);
        graph.addEdge(edge);
        graph.addEdge(revEdge);

        assertEquals(1, graph.getEdges().size());
    }

    @Test
    public void addOrRemoveNullVertexShouldDoNothing() {
        Coordinaat from = Coordinaat.op(1,1);
        Coordinaat to = Coordinaat.op(9,9);

        graph.addVertex(from);
        graph.addVertex(to);
        graph.addVertex(null);

        assertEquals(2, graph.getVertices().size());
        assertFalse(graph.removeVertex(null));
        assertEquals(2, graph.getVertices().size());
    }

    @Test
    public void addOrRemoveNullEdgeShouldDoNothing() {
        graph.addEdge(null);

        assertEquals(0, graph.getEdges().size());
        assertFalse(graph.removeEdge(null));
    }

    @Test
    public void invalidGetEdgeShouldReturnNull() {
        assertNull("null vertices should give a null edge",
                graph.getEdge(null, null));
        assertNull("same vertices should give a null edge",
                graph.getEdge(Coordinaat.op(1,1), Coordinaat.op(1,1)));
        assertNull("Edge between unincluded vertices should be null",
                graph.getEdge(Coordinaat.op(1,1), Coordinaat.op(1,2)));
    }
}
