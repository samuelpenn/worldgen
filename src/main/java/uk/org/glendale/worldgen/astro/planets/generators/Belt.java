/**
 * Belt.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.generators;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

/**
 * A Belt is the high level Group for all types of asteroid belts, planetary rings and
 * similar natural objects which consist of a large collection of very small bodies.
 */
public class Belt extends PlanetGenerator {

    public Belt(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    protected int checkDistance(int radius) {
        int previousDistance = getPreviousDistance();

        if (previousDistance + (int)(radius * 1.3) > distance) {
            distance = previousDistance + (int)(radius * 1.3);
        }

        if (radius > distance / 3) {
            radius = distance / 3;
        }

        return radius;
    }

    /**
     * Get a generated planet. Can't be called directly on the Belt class, because
     * we don't know exactly what type of planet to create.
     * Call getPlanet(String, PlanetType) instead.
     *
     * @param name  Name of planet to be generated.
     * @return      Always throws UnsupportedException().
     */
    public Planet getPlanet(String name) {
        throw new UnsupportedException("Must define planet type");
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet = definePlanet(name, type);

        // Radius of a Belt is in millions of km, and represents its width.
        int radius = Die.d6(3);

        if (distance < 10) {
            radius /= 2;
        } else if (distance < 25) {
            // No modifier.
        } else if (distance < 100) {
            radius *= 3;
            radius += Die.d3(); // Not always a multiple of 3.
        } else {
            radius *= 5;
            radius += Die.d10(); // Not always a multiple of 5.
        }
        radius = checkDistance(radius);
        planet.setRadius(radius);

        return planet;
    }
}
