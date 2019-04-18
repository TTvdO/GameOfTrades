package io.gameoftrades.student23;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.Markt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WereldLaderImpl implements WereldLader {

    @Override
    public Wereld laad(String resource) {
        // Bestand inladen
        InputStream in = this.getClass().getResourceAsStream(resource);
        Scanner scanner = new Scanner(in);

        // eerste lijn geeft de breedte en hoogte
        String widthHeight = scanner.nextLine();

        // split nummers in 2 strings
        String[] ar = widthHeight.split(",");

        if (ar.length != 2) {
            throw new IllegalArgumentException("Kaartgrootte onleesbaar");
        }

        // Strings naar integers omzetten
        int width = Integer.parseInt(ar[0].trim());
        int height = Integer.parseInt(ar[1].trim());

        // creeer kaart instantie
        Kaart map = new Kaart(width, height);

        // lees de kaart, steden en handel
        laadKaart(scanner, map);
        List<Stad> cities = laadSteden(scanner, map);
        List<Handel> trades = laadHandel(scanner, cities);

        // creeer markt met de ingelezen handel objecten
        Markt markt = new Markt(trades);

        // creeer en geef de ingelezen wereld terug
        return new Wereld(map, cities, markt);
    }

    private void laadKaart(Scanner scanner, Kaart map) {
        // vul de kaart
        for (int i = 0; i < map.getHoogte(); i++) {
            String terrainString = scanner.nextLine().trim();

            if (terrainString.length() != map.getBreedte()) {
                throw new IllegalArgumentException("Kaart-breedte komt niet overeen.");
            }

            for (int j = 0; j < map.getBreedte(); j++) {
                // krijg de letter van het terreintype op coordinaat j
                char terrainTypeChar = terrainString.charAt(j);

                // krig het terreintpe met de gelezen letter
                TerreinType terrainType = TerreinType.fromLetter(terrainTypeChar);

                // creeer coordinaat
                Coordinaat coord = Coordinaat.op(j,i);

                // creer het terrein object, deze voegt zichzelf toe aan de kaart
                new Terrein(map, coord, terrainType);
            }
        }
    }

    private List<Stad> laadSteden(Scanner scanner, Kaart map) {
        if (!scanner.hasNextInt()) {
            throw new IllegalArgumentException("Kan stedenaantal niet laden");
        }
        int numCities = scanner.nextInt();
        scanner.nextLine(); // skip naar volgende lijn
        // maak lijst aan voor wereld
        List<Stad> cities = new ArrayList<>();

        // lees elke stad uit
        String line;
        for (int i = 0; i < numCities; i++) {
            line = scanner.nextLine().trim();
            // split lijn op komma's
            String[] cityStrings = line.split(",");

            if (cityStrings.length != 3) {
                throw new IllegalArgumentException("Aantal argumenten voor stad incorrect");
            }

            // vul variabelen
            String name = cityStrings[2].trim();
            int x = safeIntParse(cityStrings[0].trim());
            int y = safeIntParse(cityStrings[1].trim());

            // controlleer of opgegeven coordinaten binnen de map vallen
            if (x < 1 || x > map.getBreedte() || y < 1 || y > map.getHoogte()) {
                throw new IllegalArgumentException("Coordinaten voor stad " + name + " vallen buiten de kaart");
            }

            // creeer Coordinaat object
            Coordinaat coord = Coordinaat.op(x - 1,y - 1);
            cities.add(new Stad(coord, name));
        }
        return cities;
    }

    private List<Handel> laadHandel(Scanner scanner, List<Stad> cities) {
        if (!scanner.hasNextInt()) {
            throw new IllegalArgumentException("Kan aantal handels neit laden");
        }
        int numTrades = scanner.nextInt();
        scanner.nextLine(); // skip naar volgende lijn

        // maakt lijst aan voor wereld
        List<Handel> trades = new ArrayList<>();
        for (int i = 0; i < numTrades; i++) {
            // lees lijn en splits in woorden
            String[] tradeStrings = scanner.nextLine().trim().split(",");

            if (tradeStrings.length != 4) {
                throw new IllegalArgumentException("Aantal argumenten voor handel incorrect");
            }

            // vul de variabelen
            String cityName = tradeStrings[0].trim();
            String tradeTypeString = tradeStrings[1].trim();
            String tradeName = tradeStrings[2].trim();
            int price = safeIntParse(tradeStrings[3].trim());

            // selecteer de stad van dit handel object aan de hand van de ingelezen naam
            Stad tradeCity = getCity(cities, cityName);

            // selecteer de ingelezen handeltype
            HandelType type = getTradeType(tradeTypeString);

            // creeer handelswaar
            Handelswaar item = new Handelswaar(tradeName);

            // creeer handel object en voeg toe aan de lijst
            Handel trade = new Handel(tradeCity, type, item, price);
            trades.add(trade);
        }

        return trades;
    }

    private HandelType getTradeType(String typeString) {
        try {
            return HandelType.valueOf(typeString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Handeltype " + typeString + " is ongeldig");
        }
    }

    private Stad getCity(List<Stad> cities, String cityName) {
        Stad city = null;
        for (Stad potentialCity : cities) {
            if (potentialCity.getNaam().equals(cityName)) {
                return potentialCity;
            }
        }

        // stad niet gevonden, gooi exc
        throw new IllegalArgumentException("Stad " + cityName + " is ongeldig");
    }


    private int safeIntParse(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Kan nummer niet goed laden: " + str);
        }

    }
}
