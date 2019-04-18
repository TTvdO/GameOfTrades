package io.gameoftrades;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Terrein;
import io.gameoftrades.model.kaart.TerreinType;
import io.gameoftrades.student23.WereldLaderImpl;
import io.gameoftrades.student23.algo.pathfinding.AStar;
import io.gameoftrades.student23.algo.pathfinding.Dijkstra;

public class SnelstePadAlgoritmeBenchmark {

    public static void main(String... args) {
        System.out.println("\tDijkstra\tAstar");

        benchmarkEmptyMap(10, 10);
        benchmarkEmptyMap(25, 25);
        benchmarkEmptyMap(50, 50);
        benchmarkEmptyMap(75, 75);
        benchmarkEmptyMap(100, 100);
        benchmarkEmptyMap(125, 125);
        benchmarkEmptyMap(150, 150);
        benchmarkEmptyMap(175, 175);
        benchmarkEmptyMap(200, 200);

        benchmarkWesteros();
    }

    private static void benchmarkWesteros() {
        WereldLaderImpl lader = new WereldLaderImpl();
        Kaart map = lader.laad("/kaarten/westeros-kaart.txt").getKaart();

        // uiterste punten
        Coordinaat topLeftCorner = Coordinaat.op(1,0);
        Coordinaat topRightCorner = Coordinaat.op(18, 0);
        Coordinaat bottomLeftCorner = Coordinaat.op(4, 47);
        Coordinaat bottomRightCorner = Coordinaat.op(21, 48);

        System.out.println("Times for westeros map\n-----");

        System.out.print("Linksboven naar rechtsboven\t");
        printTimesNanos(map, topLeftCorner, topRightCorner);

        System.out.print("Linksboven naar linksonder\t");
        printTimesNanos(map, topLeftCorner, bottomLeftCorner);

        System.out.print("Linksboven naar rechtsonder\t");
        printTimesNanos(map, topLeftCorner, bottomRightCorner);

        System.out.print("Rechtsboven naar rechtsonder\t");
        printTimesNanos(map, topRightCorner, bottomRightCorner);

        System.out.print("Rechtsboven naar linksonder\t");
        printTimesNanos(map, topRightCorner, bottomLeftCorner);

        System.out.print("Linksonder naar rechtsonder\t");
        printTimesNanos(map, bottomLeftCorner, bottomRightCorner);

    }

    private static void benchmarkEmptyMap(int width, int height) {
        // maak een kleine kaart
        Kaart map = new Kaart(width, height);

        // vul met gras
        for (int i = 0; i < map.getBreedte(); i++) {
            for (int j = 0; j < map.getHoogte(); j++) {
                new Terrein(map, Coordinaat.op(i, j), TerreinType.GRASLAND);
            }
        }

        Coordinaat start = Coordinaat.op(0,0);
        Coordinaat end = Coordinaat.op(width - 1, height - 1);

        printTimesMillis(map, start, end);
    }

    private static void printTimesMillis(Kaart map, Coordinaat from, Coordinaat to) {
        Dijkstra dijkstra = new Dijkstra();
        AStar aStar = new AStar();

        System.out.print("" + map.getBreedte() + "x" + map.getHoogte());
        long nowDijkstra, nowAStar, diffDijkstra, diffAStar;

        nowDijkstra = System.currentTimeMillis();
        dijkstra.bereken(map, from, to);
        diffDijkstra = System.currentTimeMillis() - nowDijkstra;

        System.out.print("\t" + diffDijkstra);

        nowAStar = System.currentTimeMillis();
        aStar.bereken(map, from, to);
        diffAStar = System.currentTimeMillis() - nowAStar;

        System.out.println("\t" + diffAStar);
    }

    private static void printTimesNanos(Kaart map, Coordinaat from, Coordinaat to) {
        Dijkstra dijkstra = new Dijkstra();
        AStar aStar = new AStar();

        System.out.print("" + map.getBreedte() + "x" + map.getHoogte());
        long nowDijkstra, nowAStar, diffDijkstra, diffAStar;

        nowDijkstra = System.nanoTime();
        dijkstra.bereken(map, from, to);
        diffDijkstra = System.nanoTime() - nowDijkstra;

        System.out.print("\t" + diffDijkstra);

        nowAStar = System.nanoTime();
        aStar.bereken(map, from, to);
        diffAStar = System.nanoTime() - nowAStar;

        System.out.println("\t" + diffAStar);
    }

}
