/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.smallbody;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * A Silicaceous asteroid is mostly silicate material. They are fairly common in most systems.
 */
public class Silicaceous extends SmallBody {
    public Silicaceous(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.Silicaceous);
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet =  definePlanet(name, type);
        planet.setRadius(30 + Die.d12(3));

        // Set default day length to be 2-3 hours.
        planet.setDayLength(3600 + Die.die(3600, 2));

        addSecondaryResource(planet, SilicateOre);
        addTertiaryResource(planet, SilicateCrystals);
        addTraceResource(planet, FerricOre);

        return planet;
    }
}
