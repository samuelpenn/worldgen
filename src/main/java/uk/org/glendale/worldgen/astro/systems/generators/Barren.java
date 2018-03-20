/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.*;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemGenerator;
import uk.org.glendale.worldgen.astro.systems.StarSystemType;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

/**
 * Generate a barren star system. These are devoid of native life and habitable planets.
 */
public class Barren extends StarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Barren.class);

    public Barren(WorldGen worldgen) {
        super(worldgen);
    }

    /**
     * Creates a barren star system. Such a system has one or more stars, with planets, but the
     * planets are barren with no civilisation or life of any kind. If the system has multiple stars,
     * the second star is much smaller and a long way away.
     *
     * @param sector    Sector to generate system in.
     * @param name      Name of the system.
     * @param x         X coordinate of the system in the sector (1-32).
     * @param y         Y coordinate of the system in the sector (1-40).
     *
     * @return          Created and persisted star system.
     * @throws DuplicateObjectException     If a duplicate system is created.
     */
    public StarSystem generate(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        StarSystem system = createEmptySystem(sector, name, x, y);

        switch (Die.d6()) {
            case 1:
                createRedGiant(system);
                break;
            case 2: case 3:
                createSmallDwarf(system);
                break;
            case 4:
                createProtoDwarf(system);
                break;
            case 5: case 6:
                createSmallDwarfPair(system);
                break;
        }

        updateStarSystem(system);
        return system;
    }

    /**
     * Create a single small dwarf star with a few barren worlds around it.
     *
     * @param system
     * @throws DuplicateStarException
     */
    public void createSmallDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star primary = null;

        // Options
        switch (Die.d6()) {
            case 1: case 2: case 3: case 4:
                // Small dwarf star with a small star system.
                primary = starGenerator.generateDwarfPrimary();
                break;
            case 5: case 6:
                // Brown dwarf star with no formed planets.
                primary = starGenerator.generateBrownDwarfPrimary();
                break;
        }

        switch (Die.d6()) {
            case 1: case 2: case 3: case 4: case 5:
                addBarrenWorlds(system, primary);
                break;
            case 6:
                addProtoWorlds(system, primary);
                break;
        }
    }

    /**
     * Creates a small dwarf star with a proto-planetary disc.
     */
    public void createProtoDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star primary = null;

        primary = starGenerator.generateDwarfPrimary();
        addProtoWorlds(system, primary);
    }

    /**
     * Creates a brown dwarf star. Most will be without any planets, but a few will have
     * some cold minor worlds.
     */
    public void createBrownDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star primary = null;

        primary = starGenerator.generateBrownDwarfPrimary();

        if (Die.d3() == 1) {
            PlanetFactory planetFactory = worldgen.getPlanetFactory();
            String planetName;
            Planet planet;
            int distance = 5 + Die.d6(2);
            int orbit = 1;

            planetName = factory.getPlanetName(primary, orbit++);
            planet = planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt, distance);
        }
    }

    /**
     * Creates a system with a couple of close binary dwarf stars. The two stars are very similar
     * in mass, if not identical, and orbit a common centre of gravity.
     *
     * @param system        Star system to add stars to.
     * @throws DuplicateStarException  If stars already exist.
     */
    public void createSmallDwarfPair(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.CLOSE_BINARY);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, true);

        int distance = 5 + Die.d6(2);
        Star primary = starGenerator.generatePrimary(Luminosity.VI, SpectralType.G5);
        primary.setDistance(distance);

        // Get a possibly cooler variant of the primary star. There is a 50% chance that
        // it is at least one step cooler, and 50% chance for each step down beyond that.
        SpectralType hr = primary.getSpectralType();
        while (Die.d2() == 1) {
            hr = hr.getColder();
        }
        Star secondary = starGenerator.generateSecondary(Luminosity.VI, hr);

        distance = (int)(distance * primary.getMass() / secondary.getMass());
        secondary.setDistance(distance);

        primary.setParentId(secondary.getId());
        secondary.setParentId(primary.getId());

        addBarrenWorlds(system, primary, secondary);
    }

    /**
     * Create a red giant system. Such systems will have had their inner planets swallowed or
     * burnt to a cinder long ago, leaving a warmed outer system of planets.
     *
     * @param system    System to create stars and planets in.
     * @throws DuplicateStarException If stars already exist.
     */
    public void createRedGiant(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);

        Star primary = starGenerator.generateRedGiantPrimary();
        addBarrenWorlds(system, primary);
    }

    /**
     * Creates a proto-planetary disc around the star.
     *
     * @param system    Star System worlds are being added to.
     * @param star      Star worlds are orbiting around.
     * @return          Orbit distance of the most distant planet.
     */
    private int addProtoWorlds(StarSystem system, Star star) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        Planet      planet;
        String      planetName;
        int         orbit = 1;
        int         distance = star.getMinimumDistance();

        switch (Die.d6()) {
            case 1: case 2: case 3:
                // Large proto-planetary disc.
                distance *= 10;
                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.DustDisc, distance);
                distance += planet.getRadius() * 2;
                break;
            case 4: case 5:
                distance *= 3;
                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.DustDisc, distance);
                distance += planet.getRadius() * 2;

                planetName = factory.getPlanetName(star, orbit++);
                PlanetType type = null;
                switch (Die.d6()) {
                    case 1: case 2: case 3:
                        type = PlanetType.Carbonaceous;
                        break;
                    case 4: case 5:
                        type = PlanetType.Silicaceous;
                        break;
                    case 6:
                        if (star.getOrbitTemperature(distance) < 270) {
                            type = PlanetType.Gelidaceous;
                        } else {
                            type = PlanetType.Aggregate;
                        }
                        break;
                }
                planet = planetFactory.createPlanet(system, star, planetName, type, distance, planet);

                distance *= 2;
                planetName = factory.getPlanetName(star, orbit++);
                planetFactory.createPlanet(system, star, planetName, PlanetType.DustDisc, distance, planet);
                break;
            case 6:
                // Large planetesimal disc.
                distance *= 10;
                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.PlanetesimalDisc, distance);
                break;
        }

        return distance;
    }

    /**
     * Creates planets in a barren system.
     *
     * @param system    System to create planets in.
     * @param star      Parent star of these planets.
     */
    private int addBarrenWorlds(StarSystem system, Star star) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        Planet planet;
        String          planetName;
        int             orbit = 1;
        int             distance = star.getMinimumDistance() + Die.d10(2);

        switch (Die.d6()) {
            case 1:
                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.VulcanianBelt, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += planet.getRadius() * 3;

                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.Saturnian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += (planet.getRadius() / 500) + Die.dieV(20);
                break;
            case 2: case 3:
                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.Hermian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                break;
            case 4: case 5:
                planetName = factory.getPlanetName(star, orbit++);
                planet = planetFactory.createPlanet(system, star, planetName, PlanetType.Ferrinian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                break;
            case 6:
                distance += 50 + Die.d20(3);
                break;
        }

        if (Die.d2() == 1) {
            planetName = factory.getPlanetName(star, orbit++);
            planet = planetFactory.createPlanet(system, star, planetName, PlanetType.AsteroidBelt, distance);
            logger.info(String.format("Created world [%s]", planetName));
            distance += planet.getRadius() * 3;
        }

        return distance;
    }

    /**
     * Adds worlds to a close binary star system.
     *
     * @param system
     * @param primary
     * @param secondary
     */
    private void addBarrenWorlds(StarSystem system, Star primary, Star secondary) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        Planet planet;
        String          planetName;
        int             orbit = 1;
        int             distance = primary.getMinimumDistance() + secondary.getMinimumDistance();
        distance += secondary.getDistance() + Die.d6(3);

        Star center = new Star(system.getName(), system, 0, 0,
                primary.getLuminosity(), primary.getSpectralType());
        center.setMass(primary.getMass() + secondary.getMass());

        switch (Die.d6()) {
            case 1:
                planetName = factory.getPlanetName(primary, orbit++);
                planet = planetFactory.createPlanet(system, primary, planetName, PlanetType.VulcanianBelt, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += planet.getRadius() * 3;

                planetName = factory.getPlanetName(primary, orbit++);
                planet = planetFactory.createPlanet(system, primary, planetName, PlanetType.Saturnian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += (planet.getRadius() / 500) + Die.dieV(20);
                break;
            case 2: case 3:
                planetName = factory.getPlanetName(primary, orbit++);
                planet = planetFactory.createPlanet(system, primary, planetName, PlanetType.Hermian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                break;
            case 4: case 5:
                planetName = factory.getPlanetName(primary, orbit++);
                planet = planetFactory.createPlanet(system, primary, planetName, PlanetType.Ferrinian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                break;
            case 6:
                distance += 50 + Die.d20(3);
                break;
        }

        if (Die.d2() == 1) {
            planetName = factory.getPlanetName(primary, orbit++);
            planet = planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt, distance);
            logger.info(String.format("Created world [%s]", planetName));
            distance += planet.getRadius() * 3;
        }
    }
}
