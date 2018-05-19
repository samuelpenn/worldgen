/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.smallbody;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * A Carbonaceous asteroid is a carbon rich body with varying amounts of silicates and metals. They are
 * often the most common type of body found in a system.
 */
public class Carbonaceous extends SmallBody {
    public Carbonaceous(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.Carbonaceous);
    }

    /**
     * Create an exceptionally sized Carbonaceous asteroid.
     *
     * @param name      Name of the planet.
     * @param type      Type, must be PlanetType.Carbonaceous.
     * @return
     */
    public Planet getPlanet(String name, PlanetType type) {
        if (type != PlanetType.Carbonaceous) {
            throw new IllegalArgumentException(String.format("Class Carbonaceous does not support type [%s]", type));
        }

        Planet planet =  definePlanet(name, type);
        planet.setRadius(100 + Die.d100(4));
        planet.setTemperature(Physics.getOrbitTemperature(star, distance));

        // Set default day length to be 2-3 hours.
        planet.setDayLength(3600 + Die.die(3600, 2));

        addSecondaryResource(planet, SilicateOre);
        addSecondaryResource(planet, CarbonicOre);
        addSecondaryResource(planet, CarbonicCrystals);

        if (planet.getTemperature() > 500) {
            addTertiaryResource(planet, FerricOre);
        }

        if (planet.getTemperature() < 300) {
            addTertiaryResource(planet, Water);
        } else if (planet.getTemperature() < 350) {
            addTraceResource(planet, Water);
        }

        return planet;
    }
}
