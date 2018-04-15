/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.belt;

import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Belt;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * A Planetesimal Disc is a Circumstellar class object of the Belt group. It represents a wide ring
 * of small planetesimals not more than a few metres in radius.
 */
public class PlanetesimalDisc extends Belt {
    public PlanetesimalDisc(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.PlanetesimalDisc);
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet = definePlanet(name, PlanetType.PlanetesimalDisc);
        int radius = (int) (distance / 3);

        radius = checkDistance(radius);
        planet.setRadius(radius);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        addSecondaryResource(planet, SilicateOre);
        addSecondaryResource(planet, CarbonicOre);
        if (planet.getTemperature() < 400) {
            addTertiaryResource(planet, Water);
        }

        return planet;
    }
}
