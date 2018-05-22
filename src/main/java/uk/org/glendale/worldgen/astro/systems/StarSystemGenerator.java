/**
 * StarSystemFactory.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.*;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

import java.util.List;

/**
 * Abstract Generator for star systems. An actual star system generator will extend this. Each
 * generator defines a different type of star system. Each must define a generate() method which
 * creates a new random star system at the given coordinates within a sector.
 */
public abstract class StarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(StarSystemGenerator.class);

    protected final WorldGen worldgen;
    protected final StarSystemFactory factory;
    protected final StarFactory starFactory;

    public StarSystemGenerator(WorldGen worldgen) {
        this.worldgen = worldgen;
        this.factory = worldgen.getStarSystemFactory();
        this.starFactory = worldgen.getStarFactory();
    }

    /**
     * Abstract class which must be extended by a star system generator.
     *
     * @param sector    Sector to generate star system in.
     * @param name      Name of star system to be generated.
     * @param x         X coordinate (01..32) in the sector.
     * @param y         Y coordinate (01..40) in the sector.
     * @return          Newly created star system.
     * @throws DuplicateObjectException     If a system already exists at this position.
     */
    public abstract StarSystem generate(Sector sector, String name, int x, int y) throws DuplicateObjectException;


    public abstract void colonise(StarSystem system);

    protected void updateStarSystem(StarSystem system) {
        List<Planet> planets = worldgen.getPlanetFactory().getPlanets(system);
        system.setSystemData(planets);
        try {
            factory.persist(system);
        } catch (DuplicateStarSystemException e) {
            logger.error("Unable to set system data on system", e);
        }
    }

    /**
     * Creates an empty star system with no stars or planets. This can be used either as a marker for
     * a location, or as a basis for a more complicated star system.
     *
     * @param sector    Sector to generate system in.
     * @param name      Name of the system.
     * @param x         X coordinate of the system in the sector (1-32).
     * @param y         Y coordinate of the system in the sector (1-40).
     *
     * @return          Created and persisted star system.
     * @throws DuplicateObjectException     If duplicate system is created.
     */
    protected StarSystem createEmptySystem(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        if (sector == null || sector.getId() == 0) {
            throw new IllegalArgumentException("StarSystem must be part of an existing Sector");
        }

        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("StarSystem name cannot be empty");
        }

        if (x < 1 || x > Sector.WIDTH || y < 1 || y > Sector.HEIGHT) {
            throw new IllegalArgumentException(
                    String.format("StarSystem [%s] at [%d,%d] is outside of normal sector boundary",
                            name, x, y));
        }
        logger.info(String.format("createEmptySystem: [%s] [%02d%02d] [%s]", sector.getName(), x, y, name));

        return factory.createStarSystem(sector, name.trim(), x, y, StarSystemType.EMPTY);
    }
}
