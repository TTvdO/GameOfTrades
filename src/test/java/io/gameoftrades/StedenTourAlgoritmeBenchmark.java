package io.gameoftrades;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student23.WereldLaderImpl;
import io.gameoftrades.student23.algo.tour.Greedy;
import io.gameoftrades.student23.algo.tour.TwoOpt;

import java.util.List;

public class StedenTourAlgoritmeBenchmark {

    public static void main(String... args) {
        WereldLaderImpl lader = new WereldLaderImpl();
        Wereld wereld = lader.laad("/kaarten/westeros-kaart.txt");

        TwoOpt twoopt = new TwoOpt();
        // first run is slow for some reason
        twoopt.bereken(wereld.getKaart(), wereld.getSteden());

        testMostEfficientTwoOpt(twoopt, wereld);

        Greedy greedy = new Greedy();

        System.out.println("GREEDY: ");

        run(greedy, wereld);

        System.out.println("TWOOPT: ");

        twoopt.setLoopAmount(50);

        run(twoopt, wereld);

    }

    private static void testMostEfficientTwoOpt(TwoOpt twoopt, Wereld wereld) {
        twoopt.setLoopAmount(1000);
        System.out.println("1000 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(500);
        System.out.println("500 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(250);
        System.out.println("250 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(125);
        System.out.println("125 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(50);
        System.out.println("50 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(25);
        System.out.println("25 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(10);
        System.out.println("10 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(5);
        System.out.println("5 loops: ");
        run(twoopt, wereld);

        twoopt.setLoopAmount(1);
        System.out.println("1 loops: ");
        run(twoopt, wereld);

    }

    private static void run(StedenTourAlgoritme algoritme, Wereld wereld) {
        long start = System.currentTimeMillis();

        // run 10 times
        for (int i = 0; i < 10; i++) {
            algoritme.bereken(wereld.getKaart(), wereld.getSteden());
            long now = System.currentTimeMillis();
            System.out.println("Tijd: " + (now - start));
            start = now;
        }
    }

}
