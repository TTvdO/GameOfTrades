package io.gameoftrades.student23.kaart;

import io.gameoftrades.model.kaart.*;

import java.util.ArrayList;
import java.util.Arrays;


public class PadImpl implements Pad {

    private Kaart kaart;
    private Coordinaat start;
    private ArrayList<Richting> richtingen;

    public PadImpl(Kaart kaart, Coordinaat start) {
        this(kaart, start, new Richting[0]);
    }

    public PadImpl(Kaart kaart, Coordinaat start, Richting[] richtingen) {
        this.kaart = kaart;
        this.start = start;
        this.richtingen = new ArrayList<>(Arrays.asList(richtingen));
    }

    @Override
    public int getTotaleTijd() {
        int sum = 0;
        Terrein current = kaart.getTerreinOp(start);

        for (Richting richting : richtingen) {
            current = kaart.kijk(current, richting);
            sum += current.getTerreinType().getBewegingspunten();
        }

        return sum;
    }

    @Override
    public Richting[] getBewegingen() {
        Richting[] richtingenArray = new Richting[richtingen.size()];
        return richtingen.toArray(richtingenArray);
    }

    @Override
    public Pad omgekeerd() {
        // create new Pad with this end coord as start
        PadImpl reverse = new PadImpl(kaart, volg(start));

        // reverse iterate over list, add reverse direction to the reverse path
        for (int i = richtingen.size() - 1; i >= 0; i--) {
            reverse.addRichting(richtingen.get(i).omgekeerd());
        }

        return reverse;
    }

    @Override
    public Coordinaat volg(Coordinaat coordinaat) {
        ArrayList<Richting> copy = new ArrayList<>(richtingen);
        Coordinaat next = coordinaat;

        while (!copy.isEmpty()) {
            next = next.naar(copy.remove(0));
        }

        return next;
    }

    public void addRichting(Richting richting) throws IllegalArgumentException {
        if (richting == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        richtingen.add(richting);
    }
}
