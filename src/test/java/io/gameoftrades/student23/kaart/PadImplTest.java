package io.gameoftrades.student23.kaart;

import io.gameoftrades.model.kaart.*;
import io.gameoftrades.student23.kaart.PadImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class PadImplTest {

    private Kaart kaart;

    @Before
    public void setup() {
        // maak een simpele kaart
        kaart = new Kaart(10, 1);
        for (int i = 0; i < kaart.getBreedte(); i++) {
            new Terrein(kaart, Coordinaat.op(i, 0), TerreinType.GRASLAND);
        }
    }

    @Test
    public void volgTest() {
        // maak nieuw pad vanaf (0,0)
        Coordinaat start = Coordinaat.op(0,0);
        Pad pad = new PadImpl(kaart, start);

        // voeg 9 x oost toe
        for (int i = 0; i < kaart.getBreedte() - 1; i++) {
            ((PadImpl) pad).addRichting(Richting.OOST);
        }

        Coordinaat eind = Coordinaat.op(9,0);

        Assert.assertEquals("Pad zou naar (9,9) moeten gaag",
                eind, pad.volg(start));
    }

    @Test
    public void omgekeerdTest() {
        // maak nieuw pad vanaf (0,0)
        Coordinaat start = Coordinaat.op(0,0);
        Pad pad = new PadImpl(kaart, start);

        // voeg 9 x oost toe
        for (int i = 0; i < kaart.getBreedte() - 1; i++) {
            ((PadImpl) pad).addRichting(Richting.OOST);
        }

        Coordinaat eind = Coordinaat.op(9,0);

        Assert.assertEquals("Omgekeerd pad zou naar (0,0) moeten gaan",
                start, pad.omgekeerd().volg(eind));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullRichtingThrowsExceptionTest() {
        PadImpl pad = new PadImpl(kaart, Coordinaat.op(0,0));
        pad.addRichting(null);
        fail("method did not throw exception");
    }

    @Test
    public void totaleTijdTest() {
        // maak nieuw pad vanaf (0,0)
        Coordinaat start = Coordinaat.op(0,0);
        Pad pad = new PadImpl(kaart, start);

        // voeg 9 x oost toe
        for (int i = 0; i < kaart.getBreedte() - 1; i++) {
            ((PadImpl) pad).addRichting(Richting.OOST);
        }

        Assert.assertEquals("Pad zou 9 punten moeten zijn", pad.getTotaleTijd(), 9);
    }

}
