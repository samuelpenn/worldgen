/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.belt;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Belt;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.HeavyMetals;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.PreciousMetals;

/**
 * A DustDisc is a Circumstellar class object of the Belt group. It represents a wide ring
 * of dust, gas and planetesimals that doesn't contain a significant number of objects of
 * sufficient size to be named or numbered. The majority of objects are in the millimetre
 * or centimetre size, rather than metres or kilometres.
 */
public class DustDisc extends Belt {
    public DustDisc(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.DustDisc);
        int radius = distance / 3;

        radius = checkDistance(radius);
        planet.setRadius(radius);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        addTertiaryResource(planet, Hydrogen);
        if (planet.getTemperature() < 400) {
            addTertiaryResource(planet, Water);
        }

        return planet;
    }
}
