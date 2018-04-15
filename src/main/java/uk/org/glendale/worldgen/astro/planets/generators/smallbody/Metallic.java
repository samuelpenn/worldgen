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
 * A Metallic asteroid with metal content greater than 50%.
 */
public class Metallic extends SmallBody {
    public Metallic(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.Metallic);
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet =  definePlanet(name, type);
        planet.setRadius(10 + Die.d10(3));

        // Set default day length to be 2-3 hours.
        planet.setDayLength(3600 + Die.die(3600, 3));

        addPrimaryResource(planet, FerricOre);
        addSecondaryResource(planet, HeavyMetals);
        if (Die.d2() == 1) {
            addSecondaryResource(planet, RareMetals);
        }
        if (Die.d2() == 1) {
            addSecondaryResource(planet, PreciousMetals);
        }
        if (Die.d2() == 1) {
            addTertiaryResource(planet, Radioactives);
        }

        return planet;
    }
}
