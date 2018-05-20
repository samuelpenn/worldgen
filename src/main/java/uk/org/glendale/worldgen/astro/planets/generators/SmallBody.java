/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

/**
 * The SmallBody group contains asteroids, comets and similar bodies of small size.
 * They are not often recorded on surveys, unless they are of unusual size or the
 * only thing of note in a system. Most are rarely more than a few kilometres in radius.
 *
 * Any SmallBody objects that are actually defined though are considered to be
 * exceptional members of the group, so may be tens of kilometres in radius, up
 * to 100km or so.
 */
public class SmallBody extends PlanetGenerator {

    public enum SmallBodyFeature implements PlanetFeature {
        TINY,       // 3km+
        SMALL,      // 10km+
        MEDIUM,     // 30km+ (standard)
        LARGE,      // 100km+ (~Vesta)
        HUGE,       // 300km+ (~Ceres)
        GIGANTIC,   // 1000km+
        OBLONG,
        EGG
    }

    public SmallBody(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);

    }

    protected int getRadius(Planet planet) {
        if (planet.hasFeature(SmallBodyFeature.TINY)) {
            return 3 + Die.d6();
        } else if (planet.hasFeature(SmallBodyFeature.SMALL)) {
            return 10 + Die.d8(2);
        } else if (planet.hasFeature(SmallBodyFeature.LARGE)) {
            return 100 + Die.d100(2);
        } else if (planet.hasFeature(SmallBodyFeature.HUGE)) {
            return 300 + Die.die(300, 2);
        } else if (planet.hasFeature(SmallBodyFeature.GIGANTIC)) {
            return 1000 + Die.die(500);
        }
        // Standard size for a recorded asteroid, if nothing else is specified.
        return 30 + Die.d20(3);
    }

    /**
     * Get a generated planet. Can't be called directly on the SmallBody class, because
     * we don't know exactly what type of planet to create.
     *
     * Call getPlanet(String, PlanetType) instead.
     *
     * @param name  Name of planet to be generated.
     * @return      Always throws UnsupportedException().
     */
    public Planet getPlanet(String name) {
        throw new UnsupportedException("Must define planet type");
    }

    @Override
    public Planet getPlanet(String name, PlanetType type) {
        Planet planet = definePlanet(name, type);
        planet.setRadius(Die.d10(3));

        return planet;
    }
}
