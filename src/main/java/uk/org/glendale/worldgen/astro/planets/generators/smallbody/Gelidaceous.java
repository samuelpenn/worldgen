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
 * A Gelidaceous asteroid is an ice-rich body with volatile content greater than 50%. They tend to be
 * stable, and volatile loss is minimal.
 */
public class Gelidaceous extends SmallBody {
    public Gelidaceous(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.Gelidaceous);
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet =  definePlanet(name, type);
        planet.setRadius(50 + Die.d20(3));

        // Set default day length to be 2-3 hours.
        planet.setDayLength(3600 + Die.die(3600, 2));

        addPrimaryResource(planet, Water);
        addSecondaryResource(planet, SilicateOre);
        addSecondaryResource(planet, CarbonicOre);

        switch (Die.d8()) {
            case 1: case 2:
                addTraceResource(planet, ExoticCrystals);
                break;
            case 3: case 4:
                addTraceResource(planet, SilicateCrystals);
                break;
            case 5: case 6:
                addTraceResource(planet, CarbonicCrystals);
                break;
            default:
                // Nothing special.
        }

        return planet;
    }
}
