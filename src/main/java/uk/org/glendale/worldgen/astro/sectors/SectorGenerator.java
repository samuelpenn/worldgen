/**
 * SectorFactory.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.sectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.systems.*;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

import javax.persistence.EntityManager;


/**
 * Class for generating new sectors.
 */
public class SectorGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SectorGenerator.class);
    private final WorldGen  worldgen;

    /**
     * Constructor for a new Generator.
     *
     * @param worldgen  WorldGen session object.
     */
    public SectorGenerator(WorldGen worldgen) {
        this.worldgen = worldgen;
    }

    public void createEmptySector(String name, int x, int y) throws DuplicateSectorException {
        Sector sector = new Sector(name, x, y);

        worldgen.getSectorFactory().persist(sector);
    }

    public void createRandomSector(Sector sector, int density) {
        StarSystemFactory factory = worldgen.getStarSystemFactory();

        for (int y=1; y <= 40; y++) {
            for (int x=1; x <= 32; x++) {
                if (Die.d100() <= density) {
                    // Create a new star system.
                    try {
                        factory.createStarSystem(sector, "" + Die.die(10000000), x, y, StarSystemType.EMPTY);
                    } catch (DuplicateStarSystemException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Gets the density of a particular point in a sector according to the galaxy density map.
     * The density is a number from 1 to 95, and is the percentage chance of a hex containing
     * a star system. A 'standard' density is considered to be 30.
     *
     * @param sector    Sector
     * @param x         X coordinate within the sector (1..32).
     * @param y         Y coordinate within the sector (1..40).
     * @return          A value between 1 and 95.
     */
    private int getDensity(Sector sector, int x, int y) {
        SimpleImage image = worldgen.getGalaxyMap();

        int     sx = sector.getX();
        int     sy = sector.getY();

        int w = image.getWidth();
        int h = image.getHeight();
        int sw = w / Sector.WIDTH;
        int sh = h / Sector.HEIGHT;

        int ox = sw / 2;
        int oy = sh / 2;
        int px = (ox + sx) * Sector.WIDTH + x - 1;
        int py = (oy + sy) * Sector.HEIGHT + y - 1;

        int colour = image.getColour(px, py) & 0xFF;
        logger.debug(String.format("Colour at [%d,%d] is [%d]", x, y, colour));

        int density = (int)((colour / 250.0) * 100);

        density = Math.max(density, worldgen.getConfig().getDensityMinimum());
        density = Math.min(density, worldgen.getConfig().getDensityMaximum());

        return density;
    }

    /**
     * Fills an existing sector with new star systems, according to the galactic density map.
     * Assumes that the sector is empty, but will work if it already has systems in it. The
     * chance of any given hex having a system is based on the density map.
     *
     * @param sector    Sector to create systems in.
     */
    public void createSectorByDensity(Sector sector) {
        StarSystemFactory systemFactory = worldgen.getStarSystemFactory();

        int count = 0;
        for (int y=1; y <= 40; y++) {
            for (int x=1; x <= 32; x++) {
                System.out.println("createSectorByDensity: " + (int)(100 * ((y-1) * 31 + x) / (32*40)) + "%");
                int density = getDensity(sector, x, y);
                if (Die.d100() <= density) {
                    // Create a new star system.
                    try {
                        if (!systemFactory.hasStarSystem(sector, x, y)) {
                            while (true) {
                                String name = worldgen.getStarSystemNameGenerator().generateName();

                                try {
                                    // See if a system with this name already exists in the system.
                                    systemFactory.getStarSystem(sector, name);
                                } catch (NoSuchStarSystemException e) {
                                    // We have a unique system name.
                                    StarSystemSelector selector = new StarSystemSelector(worldgen);
                                    selector.createRandomSystem(sector, name, x, y);
                                    break;
                                }
                            }
                        }
                    } catch (DuplicateObjectException e) {
                        logger.warn("Duplicate star system creation");
                    }
                    count++;
                }
            }
        }
        logger.info(String.format("Created [%d] systems.", count));
    }
}
