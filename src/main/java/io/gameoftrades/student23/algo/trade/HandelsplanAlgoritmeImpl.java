package io.gameoftrades.student23.algo.trade;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.*;
import io.gameoftrades.student23.algo.tour.graph.Edge;
import io.gameoftrades.student23.algo.tour.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HandelsplanAlgoritmeImpl implements HandelsplanAlgoritme, Debuggable {

    private HashMap<Stad, List<Handel>> offers;
    private Graph graph;
    private HandelsPositie handelsPositie;
    private List<Actie> actions;
    private Wereld world;

    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie handelsPositie) {
        this.world = wereld;
        this.handelsPositie = handelsPositie;
        initLists();

        Stad currentCity = handelsPositie.getStad();
        // hoofdloop, er moeten genoeg actiepunten beschikbaar zijn
        while (currentCity != null && this.handelsPositie.getTotaalActie() + 2 < this.handelsPositie.getMaxActie()) {
            Handel[] bestTrade = getBestTrade(currentCity);
            Handel bestOffer = bestTrade[0];
            Handel bestDemand = bestTrade[1];

            // if trade was found
            if (bestOffer != null && bestDemand != null) {
                // do action
                currentCity = buyMoveAndSell(bestOffer, bestDemand);
            } else {
                // no trade found
                currentCity = noTrades(currentCity);
            }
        }
        Handelsplan hp = new Handelsplan(actions);
        debug.speelPlanAf(hp, handelsPositie);
        return hp;
    }

    /**
     * Selecteert de beste handelsoptie voor de gegeven stad.
     *
     * Er wordt een waarde berekent door de winst van een handelsoptie te delen door het aantal actiepunten die de
     * uitgevoerde acties kosten, de optie met de hoogste waarde is de beste optie.
     * @param city
     * @return array {Handel aanbod, Handel vraag}
     */
    private Handel[] getBestTrade(Stad city) {
        Handel bestBuy = null, bestSell = null;
        double bestRatio = Double.MIN_VALUE;

        for (Handel offer : offers.get(city)) {
            String offerName = offer.getHandelswaar().getNaam();

            for (Handel demand : world.getMarkt().getVraag()) {
                String demandName = demand.getHandelswaar().getNaam();

                // als producten overeenkomen en winst kan worden gemaakt
                if (offerName.equals(demandName) && demand.getPrijs() > offer.getPrijs()) {
                    double ratio = getRatio(offer, demand);

                    if (bestRatio < ratio) {
                        bestRatio = ratio;
                        bestBuy = offer;
                        bestSell = demand;
                    }
                }
            }
        }

        return new Handel[]{bestBuy, bestSell};
    }

    private Pad getPath(Stad from, Stad to) {
        Edge edge = graph.getEdge(from.getCoordinaat(), to.getCoordinaat());

        if (edge != null && edge.getPath() != null) {
            return reversePathIfNeeded(edge.getPath(), from, to);
        }
        return null;
    }

    /**
     * Berekent een ratio winst / totale actiepunten.
     * @param offer aandbod Handel object
     * @param demand vraag Handel object
     * @return double ratio of Double.MIN_VALUE als `graph` het pad niet heeft
     */
    private double getRatio(Handel offer, Handel demand) {
        int profit = demand.getPrijs() - offer.getPrijs();
        Edge edge = graph.getEdge(offer.getStad().getCoordinaat(), demand.getStad().getCoordinaat());

        if (edge != null) {
            int totalActionCost = edge.getWeight() + 2;
            int actionPointsLeft = handelsPositie.getMaxActie() - handelsPositie.getTotaalActie();

            if (totalActionCost <= actionPointsLeft) {
                return (double) profit / (double) totalActionCost;
            }
        }
        return Double.MIN_VALUE;
    }

    /**
     * Methode voor wanneer er geen geldige handelsacties voor een stad uitgevoerd kunnen worden.
     * Verplaatst de handelspositie naar de dichtsbijzijnste stad en geeft deze stad. Geeft null als er niet genoeg
     * actiepunten meer zijn, of als er geen dichtsbijzijnste stad is.
     * @param currentCity
     * @return
     */
    private Stad noTrades(Stad currentCity) {
        Stad nextCity  = getClosestCity(currentCity);
        if (nextCity != null) {
            // neem het pad van huidige naar volgende stad
            Edge edge = graph.getEdge(currentCity.getCoordinaat(), nextCity.getCoordinaat());

            if (edge != null) {
                Pad path = reversePathIfNeeded(edge.getPath(), currentCity, nextCity);

                // beweeg als er genoeg actiepunten zijn
                if (handelsPositie.getTotaalActie() + edge.getWeight() < handelsPositie.getMaxActie()) {
                    move(currentCity, nextCity);
                    return handelsPositie.getStad();
                }
            }
        }
        // niet genoeg actiepunten of geen volgende stad
        return null;
    }

    /**
     *
     * @param path
     * @param from
     * @param to
     * @return
     */
    private Pad reversePathIfNeeded(Pad path, Stad from, Stad to) {
        Coordinaat coordFrom = from.getCoordinaat();
        Coordinaat coordTo = to.getCoordinaat();

        if (!path.volg(coordFrom).equals(coordTo)) {
            return path.omgekeerd();
        }
        return path;
    }

    /**
     * Voert een koop, beweeg en verkoop actie uit in die volgorde. Geeft de huidige stad van de handelspositie.
     * @param offer
     * @param demand
     * @return
     */
    private Stad buyMoveAndSell(Handel offer, Handel demand) {
        // creeer acties
        KoopActie buyAction = new KoopActie(offer);
        VerkoopActie sellAction = new VerkoopActie(demand);

        // voer koop actie uit
        handelsPositie = buyAction.voerUit(handelsPositie);
        actions.add(buyAction);

        // creeer pad en beweeg
        move(offer.getStad(), demand.getStad());

        // voert verkoop actie uit
        handelsPositie = sellAction.voerUit(handelsPositie);
        actions.add(sellAction);

        return handelsPositie.getStad();
    }

    /**
     * Creert een beweegactie, zet deze om in navigeeracties en voert de acties uit.
     * @param currentCity
     * @param nextCity
     */
    private void move(Stad currentCity, Stad nextCity) {
        Pad path = getPath(currentCity, nextCity);

        // maak beweegactie en zet om naar navigatieacties
        List<NavigeerActie> moves = new BeweegActie(world.getKaart(), currentCity, nextCity, path).naarNavigatieActies();

        // voer navigeeracties uit
        for (NavigeerActie action : moves) {
            handelsPositie = action.voerUit(handelsPositie);
            actions.add(action);
        }
    }

    /**
     * Geeft de stad die het dichts bij de gegeven stad ligt
     * @param city
     * @return
     */
    private Stad getClosestCity(Stad city) {
        Edge shortestEdge = null;

        // selecteer Edge met kleinste gewicht
        for (Edge edge :graph.getNeighbours().get(city.getCoordinaat())) {
            if (shortestEdge == null
                    || shortestEdge.getWeight() > edge.getWeight()) {
                shortestEdge = edge;
            }
        }

        // geef de volgende stad
        if (shortestEdge != null) {
            for (Stad nextCity : world.getSteden()) {
                if (nextCity.getCoordinaat().equals(shortestEdge.getTo())) {
                    return nextCity;
                }
            }
        }

        // geen meest dichtsbijzijnde stad
        return null;
    }

    /**
     * Initialiseert lijst die gebruikt worden
     */
    private void initLists() {
        actions = new ArrayList<>();
        offers = new HashMap<>();
        graph = new Graph();

        // vul map met handel per stad
        initTrades();

        // bereken alle paden
        List<Coordinaat> coords = new ArrayList<>();
        for (Stad city : world.getSteden()) {
            coords.add(city.getCoordinaat());
        }

        graph.makeComplete(world.getKaart(), coords);
    }

    /**
     * Initialiseert de map die alle handel voor een stad bevat.
     */
    private void initTrades() {
        for (Handel trade : world.getMarkt().getAanbod()) {
            Stad city = trade.getStad();
            List<Handel> currentCityTrades;
            if (offers.containsKey(city)) {
                currentCityTrades = offers.get(city);
            } else {
                currentCityTrades = new ArrayList<>();
            }
            currentCityTrades.add(trade);
            offers.put(city, currentCityTrades);
        }

        // voeg alle steden toe die geen handel hebben
        for (Stad city : world.getSteden()) {
            if (!offers.containsKey(city)) {
                offers.put(city, new ArrayList<>());
            }
        }
    }

    private Debugger debug = new DummyDebugger();

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }
}
