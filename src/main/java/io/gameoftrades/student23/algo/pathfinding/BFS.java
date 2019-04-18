package io.gameoftrades.student23.algo.pathfinding;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.student23.kaart.PadImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/**
 *
 * @author Tim
 */
public class BFS implements SnelstePadAlgoritme, Debuggable{

    private LinkedList<Coordinaat> unvisited;
    private LinkedList<Coordinaat> visited;
    private Map<Coordinaat, PadImpl> paths;
    
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat end){
        // initialiseer lijsten
        unvisited = new LinkedList<>();
        visited = new LinkedList<>();
        paths = new HashMap<>();

        // voeg startpunt toe aan lijsten
        paths.put(start, new PadImpl(kaart, start));
        unvisited.add(start);

        while(!unvisited.isEmpty()){
            // neem het eerste coordinaat uit de onbezochte lijst en het pad daar naartoe
            Coordinaat current = unvisited.remove();
            PadImpl currentPath = paths.get(current);

            // als het huidige coordinaat het eindpunt is, einde algoritme, geef pad
            if(current.equals(end)){
                debugger.debugPad(kaart, start, currentPath);
                return currentPath;
            }

            // kijk in mogelijke richtingen
            Richting[] mogelijkeRichtingen = kaart.getTerreinOp(current).getMogelijkeRichtingen();
            for(Richting r : mogelijkeRichtingen){
                // neem het coordinaat in de richting
                Coordinaat next = current.naar(r);

                // als het coordinaat nog niet bezocht is
                if(!visited.contains(next) && !unvisited.contains(next)){
                    // voeg toe aan onbezocht
                    unvisited.add(next);

                    // kopieer het huidige pad en voeg richting toe
                    PadImpl pad = new PadImpl(kaart, start, currentPath.getBewegingen());
                    pad.addRichting(r);

                    // zet nieuwe pad in de lijst
                    paths.put(next, pad);
                }
            }
            // markeer huidige coordinaat als bezocht
            visited.add(current);
            
            debugger.debugCoordinaten(kaart, visited);
        }

        // geen pad gevonden
        return null;
    }
    
    private Debugger debugger = new DummyDebugger();
    
    @Override
    public void setDebugger(Debugger dbgr) {
        this.debugger = dbgr;
    }
}

