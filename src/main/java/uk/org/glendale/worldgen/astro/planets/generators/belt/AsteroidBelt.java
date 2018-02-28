/**
 * AsteroidBelt.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.generators.belt;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Belt;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * Generates an asteroid belt. Mostly rocky, with some volatiles.
 */
public class AsteroidBelt extends Belt {

    public AsteroidBelt(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.AsteroidBelt);
        int radius = Die.d6(4 * 5);

        radius = checkDistance(radius);
        planet.setRadius(radius);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        addPrimaryResource(planet, SilicateOre);
        addPrimaryResource(planet, CarbonicOre);
        addSecondaryResource(planet, FerricOre);
        addTertiaryResource(planet, Water);
        addTertiaryResource(planet, HeavyMetals);
        addTertiaryResource(planet, PreciousMetals);

        return planet;
    }
}
