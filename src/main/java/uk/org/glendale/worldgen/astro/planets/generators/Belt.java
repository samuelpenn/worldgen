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
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

import static uk.org.glendale.worldgen.astro.Physics.MKM;

/**
 * A Belt is the high level Group for all types of asteroid belts, planetary rings and
 * similar natural objects which consist of a large collection of very small bodies.
 */
public class Belt extends PlanetGenerator {

    public Belt(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    /**
     * Checks that the width of the belt is not greater than the distance to the primary.
     * If it is, returns a reduced belt width which is compatible with the distance.
     *
     * @param radius    Radius of the belt in kilometres.
     *
     * @return          New suggested radius in kilometres.
     */
    protected int checkDistance(int radius) {
        long previousDistance = getPreviousDistance();

        if (previousDistance + (int)(radius * 1.3) > distance) {
            distance = previousDistance + (int)(radius * 1.3);
        }

        if (radius > distance / 3) {
            radius = (int) distance / 3;
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

        // Radius of a Belt is in km, and represents its width.
        int radius = (int) (Die.d6(3) * MKM);

        if (distance < 10 * MKM) {
            radius /= 2;
        } else if (distance < 25 * MKM) {
            // No modifier.
        } else if (distance < 100 * MKM) {
            radius *= 3;
            radius += Die.d3(); // Not always a multiple of 3.
        } else {
            radius *= 5;
            radius += Die.d10() * MKM; // Not always a multiple of 5.
        }
        radius = checkDistance(radius);
        planet.setRadius(radius);

        return planet;
    }
}
