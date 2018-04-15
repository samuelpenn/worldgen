/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.smallbody;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.commodities.CommodityName;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * An Aggregate asteroid is made up of smaller rocks collected together into a 'pile' which is
 * loosely held together by gravity. They tend to be fragile and unstable. They may contain
 * a variety of different resources, though lack any internal structure to have formed a
 * distinct core and crust, preventing formation of more complex materials.
 */
public class Aggregate extends SmallBody {
    public Aggregate(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.Aggregate);
    }

    /**
     * Creates an exceptional example of an Aggregate Asteroid, a few tens of kilometres in
     * radius.
     *
     * @param name      Name of the planet.
     * @param type      Type, always PlanetType.Aggregate.
     * @return          A defined planet.
     */
    public Planet getPlanet(String name, PlanetType type) {
        if (type != PlanetType.Aggregate) {
            throw new IllegalArgumentException(String.format("Class Aggregate does not support type [%s]", type));
        }
        Planet planet =  definePlanet(name, type);
        planet.setRadius(10 + Die.d6(3));

        // Set default day length to be 2-3 hours.
        planet.setDayLength(18000 + Die.die(3600, 5));

        addSecondaryResource(planet, SilicateOre);
        if (planet.getTemperature() > 500) {
            addSecondaryResource(planet, FerricOre);
            addTertiaryResource(planet, CarbonicOre);
            addTraceResource(planet, Radioactives);
        } else if (planet.getTemperature() > 400) {
            addSecondaryResource(planet, CarbonicOre);
            addTertiaryResource(planet, FerricOre);
        } else if (planet.getTemperature() > 300) {
            addSecondaryResource(planet, CarbonicOre);
            addTertiaryResource(planet, FerricOre);
            addTraceResource(planet, Water);
        } else if (planet.getTemperature() > 200) {
            addSecondaryResource(planet, Water);
            addTertiaryResource(planet, CarbonicOre);
            addTraceResource(planet, FerricOre);
        } else if (planet.getTemperature() > 100) {
            addSecondaryResource(planet, Water);
            addTraceResource(planet, CarbonicOre);
        }

        return planet;
    }
}
