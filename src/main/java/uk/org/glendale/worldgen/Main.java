/**
 * Main.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.Universe;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.codes.Temperature;
import uk.org.glendale.worldgen.astro.sectors.*;
import uk.org.glendale.worldgen.astro.stars.DuplicateStarException;
import uk.org.glendale.worldgen.astro.stars.Luminosity;
import uk.org.glendale.worldgen.astro.stars.SpectralType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.*;
import uk.org.glendale.worldgen.web.ConfigController;
import uk.org.glendale.worldgen.web.Controller;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final SessionFactory sessionFactory;
    private static final Config configuration;

    static {
        configuration = Config.getConfiguration();

        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        cfg.getProperties().setProperty("hibernate.connection.url", configuration.getDatabaseURL());
        cfg.getProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
        cfg.getProperties().setProperty("hibernate.connection.username", configuration.getDatabaseUsername());
        cfg.getProperties().setProperty("hibernate.connection.password", configuration.getDatabasePassword());

        sessionFactory = cfg.buildSessionFactory();
    }

    static public Config getConfiguration() {
        return configuration;
    }

    /**
     * Gets a singleton session factory object for accessing the persistence layer.
     * If one doesn't yet exist, then it is automatically created.
     *
     * @return SessionFactory singleton.
     */
    private synchronized static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected static EntityManager getSession() {
        return sessionFactory.createEntityManager();
    }

    /**
     * Gets a WorldGen object for this session. This object has it's own session transaction,
     * and should be closed once finished, either by it going out of scope, or calling close().
     * This is designed to be called on each call to the web server, so each web session has
     * its own database session.
     *
     * @return  New WorldGen session manager.
     */
    public static WorldGen getWorldGen() {
        return new WorldGen(getSession(), configuration);
    }

    private void createAllSectors() {
        try (WorldGen worldGen = getWorldGen()) {
            SectorFactory sectorFactory = worldGen.getSectorFactory();
            Universe        universe = worldGen.getUniverse();

            for (int y = universe.getMinY(); y <= universe.getMaxY(); y++) {
                for (int x = universe.getMinX(); x <= universe.getMaxX(); x++) {
                    Sector sector1 = null;
                    try {
                        sector1 = sectorFactory.getSector(x, y);
                    } catch (NoSuchSectorException e) {
                        sector1 = new Sector("Sector " + SectorFactory.getSectorNumber(x, y), x, y);
                        sectorFactory.persist(sector1);
                        new SectorGenerator(worldGen).createSectorByDensity(sector1, null);
                    }
                    logger.debug("Sector 1: " + sector1.getId());
                }
            }

        } catch (DuplicateSectorException e) {
            logger.error("Failed to create new Sector", e.getMessage());
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }

    private int createSimple(WorldGen worldGen) {
        try {
            SectorFactory sectorFactory = worldGen.getSectorFactory();
            StarSystemFactory systemFactory = worldGen.getStarSystemFactory();

            int x= 2;
            int y = 0;
            Sector sector = null;
            try {
                sector = sectorFactory.getSector(x, y);
            } catch (NoSuchSectorException e) {
                sector = new Sector("Sector " + SectorFactory.getSectorNumber(x, y), x, y);
                sectorFactory.persist(sector);
            }
            logger.debug(sector.getName() + ": " + sector.getId());

            StarSystemSelector selector = new StarSystemSelector(worldGen);
            StarSystem system;

            x = Die.die(32);
            y = Die.die(40);
            try {
                systemFactory.getStarSystem(sector, x, y);
            } catch (NoSuchStarSystemException e) {
                system = selector.createSimpleSystem(sector,
                    worldGen.getStarSystemNameGenerator().generateName(), x, y);

                return system.getId();
            }
        } catch (DuplicateSectorException e) {
            logger.error("Failed to create new Sector", e.getMessage());
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }

        return 0;
    }

    private void drawSystemMap(int id) {
        try (WorldGen worldGen = getWorldGen()) {
            StarSystemFactory systemFactory = worldGen.getStarSystemFactory();

            StarSystem system = systemFactory.getStarSystem(id);

            StarSystemImage image = new StarSystemImage(worldGen, system);
            image.setWidth(2048);
            image.draw().save(new File("/home/sam/system.jpg"));

        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }

    public static void main(String args[]) {
        logger.info("== WorldGen ==");

        Main main = new Main();

        //main.createAllSectors();

        WorldGen wg = Main.getWorldGen();
        for (int i=0; i < 1; i++) {
            main.createSimple(wg);
        }
        System.exit(0);

        //main.drawSystemMap(146115);

        long currentTime = wg.getCurrentTime();
        logger.info("Current time: " + currentTime);
        currentTime += 1;
        logger.info("Current time: " + currentTime);
        wg.setCurrentTime(currentTime);
        wg.close();

        System.exit(0);

        PlanetFactory factory = wg.getPlanetFactory();
        int id = main.createSimple(wg);

        if (id > 0) {
            main.drawSystemMap(id);
        }
/*
        Star star = new Star();
        star.setLuminosity(Luminosity.V);
        star.setSpectralType(SpectralType.G2);

        for (int d = 25; d < 1000; d += 25) {
            System.out.println("Distance [" + d + " Mkm] Temperature [" + star.getOrbitTemperature(d) + "]");
        }
*/
        // Ensure all threads are closed as well.
        System.exit(0);
    }
}
