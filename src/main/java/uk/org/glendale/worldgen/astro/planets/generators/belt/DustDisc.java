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
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.civ.CivilisationFeature;
import uk.org.glendale.worldgen.civ.CivilisationGenerator;
import uk.org.glendale.worldgen.civ.civilisation.Hermits;
import uk.org.glendale.worldgen.civ.civilisation.Research;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.HeavyMetals;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.PreciousMetals;

/**
 * A DustDisc is a circumstellar class object of the Belt group. It represents a wide ring
 * of dust, gas and planetesimals that doesn't contain a significant number of objects of
 * sufficient size to be named or numbered. The majority of objects are in the millimetre
 * or centimetre size, rather than metres or kilometres.
 */
public class DustDisc extends Belt {
    public DustDisc(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.DustDisc);
        int radius = (int) (distance / 3);

        radius = (int) Physics.round(checkDistance(radius), 4);
        planet.setRadius(radius);

        // Resources
        addTertiaryResource(planet, Hydrogen);
        if (planet.getTemperature() < 300 && Die.d2() == 1) {
            addSecondaryResource(planet, Water);
        } else if (planet.getTemperature() < 400 && Die.d2() == 1) {
            addTertiaryResource(planet, Water);
        } else if (planet.getTemperature() < 400) {
            addTraceResource(planet, Water);
        }
        if (Die.d3() == 1) {
            addTraceResource(planet, SilicateOre);
        }
        if (Die.d6() == 1) {
            addTraceResource(planet, CarbonicOre);
        }

        return planet;
    }

    public long colonise(Planet planet, CivilisationFeature... features) {
        CivilisationGenerator coloniser = null;

        switch (Die.d6(2)) {
            case 7: case 8: case 9:
                coloniser = new Hermits(worldGen, system, planet);
                break;
            case 10: case 11: case 12:
                coloniser = new Research(worldGen, system, planet);
                break;
        }

        if (coloniser != null) {
            coloniser.generate(features);
        }

        return planet.getPopulation();
    }
}
