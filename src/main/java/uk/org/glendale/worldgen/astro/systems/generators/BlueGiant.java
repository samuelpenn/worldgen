/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.Luminosity;
import uk.org.glendale.worldgen.astro.stars.SpectralType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemGenerator;
import uk.org.glendale.worldgen.astro.systems.StarSystemType;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

/**
 * Generates a star system with a hot blue or white giant as its primary star. Such systems will have few,
 * if any, planets.
 */
public class BlueGiant extends StarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(BlueGiant.class);
    public BlueGiant(WorldGen worldgen) {
        super(worldgen);
    }

    public StarSystem generate(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        StarSystem system = createEmptySystem(sector, name, x, y);

        updateStarSystem(createSuperGiant(system));

        return system;
    }

    /**
     * Create a typical hot super giant. Such a system is low in metals and has no planets of any type.
     *
     * @param system    System to create star in.
     * @return          Updated star system object.
     */
    private StarSystem createSuperGiant(StarSystem system) throws DuplicateObjectException {
        system.setType(StarSystemType.SINGLE);

        Star primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName());

        SpectralType type = SpectralType.B5;
        switch (Die.d6()) {
            case 1:
                type = SpectralType.valueOf("O" + (Die.d4() + 5));
                break;
            case 2: case 3: case 4:
                type = SpectralType.valueOf("B" + (Die.d10() - 1));
                break;
            case 5: case 6:
                type = SpectralType.valueOf("A" + (Die.d10() - 1));
                break;
        }
        primary.setSpectralType(type);

        switch (Die.d6()) {
            case 1: case 2:
                primary.setLuminosity(Luminosity.Ia);
                break;
            default:
                primary.setLuminosity(Luminosity.Ib);
                break;
        }

        logger.debug(String.format("Creating %s %s super giant",
                primary.getLuminosity(), primary.getSpectralType()));

        starFactory.persist(primary);

        return system;
    }
}
