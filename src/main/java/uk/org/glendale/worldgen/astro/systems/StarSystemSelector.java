/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.systems.generators.Barren;
import uk.org.glendale.worldgen.astro.systems.generators.BlueGiant;
import uk.org.glendale.worldgen.astro.systems.generators.Simple;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

import java.util.function.Consumer;

/**
 * Selects what type of star system to generate.
 */
public class StarSystemSelector {
    private static final Logger logger = LoggerFactory.getLogger(StarSystemSelector.class);

    private final WorldGen worldgen;

    public StarSystemSelector(WorldGen worldgen) {
        this.worldgen = worldgen;
    }

    private void validate(Sector sector, String name, int x, int y) {
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
    }

    public StarSystem createSimpleSystem(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        logger.info(String.format("createSimpleSystem: [%s] [%02d%02d] [%s]", sector.getName(), x, y, name));

        validate(sector, name, x, y);

        StarSystemGenerator generator = new Simple(worldgen);

        return generator.generate(sector, name, x, y);
    }

    /**
     * Generate a complete random star system. This selects a type of star system from all the
     * available types, so provides the most diverse range of options.
     *
     * @param sector    Sector to create star system in.
     * @param name      Name of system to be created.
     * @param x         X coordinate within the sector (1-32)
     * @param y         Y coordinate within the sector (1-40)
     *
     * @return          A newly created star system.
     * @throws DuplicateObjectException     Star system already exists at location.
     */
    public StarSystem createRandomSystem(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        logger.info(String.format("createRandomSystem: [%s] [%02d%02d] [%s]", sector.getName(), x, y, name));

        validate(sector, name, x, y);

        StarSystemGenerator generator = null;
        switch (Die.d6(2)) {
            case 2:
                generator = new BlueGiant(worldgen);
                break;
            case 3:
                generator = new Barren(worldgen);
                break;
            case 4:
                generator = new Barren(worldgen);
                break;
            case 5:
                generator = new Simple(worldgen);
                break;
            case 6:
                generator = new Simple(worldgen);
                break;
            case 7:
                generator = new Simple(worldgen);
                break;
            case 8:
                generator = new Simple(worldgen);
                break;
            case 9:
                generator = new Simple(worldgen);
                break;
            case 10:
                generator = new Barren(worldgen);
                break;
            case 11:
                generator = new Barren(worldgen);
                break;
            case 12:
                generator = new Barren(worldgen);
                break;
        }
        return generator.generate(sector, name, x, y);
    }
}
