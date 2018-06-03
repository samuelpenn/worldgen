/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.*;
import uk.org.glendale.worldgen.astro.systems.*;
import uk.org.glendale.worldgen.civ.CivilisationGenerator;
import uk.org.glendale.worldgen.civ.civilisation.FreeSettlers;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate a barren star system. These are devoid of native life and habitable planets. Most are
 * G/K/M stars with a few barren worlds orbiting them. Most worlds will lack an atmosphere, and
 * have no surface water.
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
            case 5:
                createSmallDwarfPair(system);
                break;
            case 6:
                createAsteroidBelt(system);
                break;
        }
        updateStarSystem(system);

        colonise(system);

        return system;
    }

    public void colonise(StarSystem system) {
        CivilisationGenerator generator = new FreeSettlers(worldgen, system);
        logger.info("Colonise star system " + system.getName());

        for (Planet planet : system.getPlanets()) {
            logger.info("Colonise " + planet.getName());
        }
    }

    /**
     * Creates a single main-sequence star with a single asteroid belt around it. The star will mostly
     * likely be a K-type star, with a chance of cool G or warm M. Depending on the temperature of the
     * belt, it may be a Vulcanian or Ice belt rather than a typical asteroid belt.
     *
     * @param system        Star System to create belt in.
     */
    @SuppressWarnings("WeakerAccess")
    public void createAsteroidBelt(StarSystem system) throws DuplicateObjectException {
        logger.info(String.format("Generating [Barren] [AsteroidBelt] system [%s]", system.getName()));
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        // On average a cool main-sequence star will be generated.
        Star primary = starGenerator.generatePrimary(Luminosity.V,
                SpectralType.K7.getSpectralType(Die.dieV(8)));
        system.addStar(primary);
        system.setType(StarSystemType.SINGLE);

        // Place the belt around 1AU from the star.
        long distance = Physics.AU + Die.dieV(50_000_000);
        String name = StarSystemFactory.getBeltName(primary, 1);
        String type = "AsteroidBelt";

        logger.info(String.format("Creating barren system [%s] with belt at [%d]km (%d)",
                primary.toString(), distance, primary.getHotDistance()));

        List<Planet> allPlanets;
        if (distance < primary.getHotDistance()) {
            allPlanets = addVulcanianBelt(system, name, distance);
            type = "VulcanianBelt";
        } else if (distance < primary.getSnowLineDistance()) {
            allPlanets = addAsteroidBelt(system, name, distance);
        } else {
            int coldness = (int) (distance / primary.getSnowLineDistance());
            switch (Die.d6(coldness)) {
                case 1: case 2: case 3: case 4: case 5:
                    allPlanets = addAsteroidBelt(system, name, distance);
                    break;
                default:
                    allPlanets = addIceBelt(system, name, distance);
                    type = "IceBelt";
            }
        }

        system.addTradeCode(StarSystemCode.Ba);
        if (Die.d6() == 1) {
            system.addTradeCode(StarSystemCode.Sf);
            system.setZone(Zone.AMBER);
        }

        system.setPlanets(allPlanets);
        setDescription(system, type);
    }

    private List<Planet> addVulcanianBelt(StarSystem system, String name, long distance) {
        return worldgen.getPlanetFactory().createPlanet(system, system.getStars().get(0),
                name, PlanetType.VulcanianBelt, distance);
    }

    private List<Planet> addAsteroidBelt(StarSystem system, String name, long distance) {
        return worldgen.getPlanetFactory().createPlanet(system, system.getStars().get(0),
                name, PlanetType.AsteroidBelt, distance);
    }

    private List<Planet> addIceBelt(StarSystem system, String name, long distance) {
        return worldgen.getPlanetFactory().createPlanet(system, system.getStars().get(0),
                name, PlanetType.IceBelt, distance);
    }

    /**
     * Create a single cool (K or M) dwarf star with a few barren worlds around it.
     *
     * @param system    Star system to generate.
     *
     * @throws DuplicateStarException   If duplicate star is created.
     */
    @SuppressWarnings("WeakerAccess")
    public void createSmallDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        PlanetFactory factory = worldgen.getPlanetFactory();

        Star primary = starGenerator.generatePrimary(Luminosity.V,
                SpectralType.K6.getSpectralType(Die.dieV(6)));
        system.addStar(primary);
        system.setType(StarSystemType.SINGLE);

        logger.info(String.format("Generating [Barren] [SmallDwarf] system [%s]", primary));

        int  numPlanets = Die.d3() + 2;
        long distance = primary.getMinimumDistance() * 2;
        logger.info(String.format("Constant [%f] Distance [%,d]", Physics.getSolarConstant(primary), distance));
        for (int p = 0; p < numPlanets; p++) {
            int orbitTemperature = Physics.getOrbitTemperature(primary, distance);
            logger.info(String.format("Orbit Temperature is %dK at distance %,dkm", orbitTemperature, distance));

            List<Planet> planets = null;
            if (Die.d6() == 1) {
                // No planet in this orbit.
            } else if (orbitTemperature > 350) {
                // Mercury
                String name = StarSystemFactory.getPlanetName(primary, p+1);
                planets = factory.createPlanet(system, primary, name, PlanetType.Hermian, distance);
            } else if (orbitTemperature > 275) {
                // Venus
                String name = StarSystemFactory.getPlanetName(primary, p+1);
                planets = factory.createPlanet(system, primary, name, PlanetType.Cytherean, distance);
            } else if (orbitTemperature > 225) {
                // Earth
                String name = StarSystemFactory.getPlanetName(primary, p+1);
                planets = factory.createPlanet(system, primary, name, PlanetType.EuArean, distance);
            } else if (orbitTemperature > 200) {
                // Mars
                String name = StarSystemFactory.getPlanetName(primary, p+1);
                planets = factory.createPlanet(system, primary, name, PlanetType.EuArean, distance);
            } else {
                // Colder
                String name = StarSystemFactory.getPlanetName(primary, p+1);
                planets = factory.createPlanet(system, primary, name, PlanetType.EuArean, distance);
            }

            system.addPlanets(planets);

            distance *= (9.0 + Die.d3(3)) / 10;
            distance += 5_000_000;
        }

        system.addTradeCode(StarSystemCode.Ba);
        if (Die.d10() == 1) {
            system.addTradeCode(StarSystemCode.Sf);
        }

        setDescription(system, null);
    }

    /**
     * Creates a small dwarf star with a proto-planetary disc.
     */
    @SuppressWarnings("WeakerAccess")
    public void createProtoDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star primary = null;

        primary = starGenerator.generateDwarfPrimary();
        addProtoWorlds(system, primary);

        setDescription(system, null);
    }

    /**
     * Creates a brown dwarf star. Most will be without any planets, but a few will have
     * some cold minor worlds.
     */
    public void createBrownDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        List<Planet> planets = new ArrayList<Planet>();
        Star primary = starGenerator.generateBrownDwarfPrimary();

        if (Die.d3() == 1) {
            PlanetFactory planetFactory = worldgen.getPlanetFactory();
            String planetName;
            int distance = 5 + Die.d6(2);
            int orbit = 1;

            planetName = StarSystemFactory.getPlanetName(primary, orbit++);
            planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt, distance);
        }

        system.setPlanets(planets);
        setDescription(system, null);
    }

    /**
     * Creates a system with a couple of close binary dwarf stars. The two stars are very similar
     * in mass, if not identical, and orbit a common centre of gravity.
     *
     * @param system        Star system to add stars to.
     * @throws DuplicateStarException  If stars already exist.
     */
    @SuppressWarnings("WeakerAccess")
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

        setDescription(system, null);
    }

    /**
     * Create a red giant system. Such systems will have had their inner planets swallowed or
     * burnt to a cinder long ago, leaving a warmed outer system of planets.
     *
     * @param system    System to create stars and planets in.
     * @throws DuplicateStarException If stars already exist.
     */
    @SuppressWarnings("WeakerAccess")
    public void createRedGiant(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);

        Star primary = starGenerator.generateRedGiantPrimary();
        addBarrenWorlds(system, primary);
        setDescription(system, null);
    }

    /**
     * Creates a proto-planetary disc around the star.
     *
     * @param system    Star System worlds are being added to.
     * @param star      Star worlds are orbiting around.
     */
    private void addProtoWorlds(StarSystem system, Star star) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        String      planetName;
        int         orbit = 1;
        long        distance = star.getMinimumDistance();
        List<Planet> allPlanets = new ArrayList<Planet>();
        List<Planet> planets;

        switch (Die.d6()) {
            case 1: case 2: case 3:
                // Large proto-planetary disc.
                distance *= 10;
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.DustDisc, distance);
                distance += planets.get(0).getRadius() * 2;
                allPlanets.addAll(planets);
                break;
            case 4: case 5:
                distance *= 3;
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.DustDisc, distance);
                distance += planets.get(0).getRadius() * 2;
                allPlanets.addAll(planets);

                planetName = StarSystemFactory.getPlanetName(star, orbit++);
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
                planets = planetFactory.createPlanet(system, star, planetName, type, distance, planets.get(0));
                distance *= 2;
                allPlanets.addAll(planets);
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.DustDisc, distance, planets.get(0));
                allPlanets.addAll(planets);
                break;
            case 6:
                // Large planetesimal disc.
                distance *= 10;
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.PlanetesimalDisc, distance);
                allPlanets.addAll(planets);
                break;
        }

        system.setPlanets(allPlanets);
    }

    /**
     * Creates planets in a barren system.
     *
     * @param system    System to create planets in.
     * @param star      Parent star of these planets.
     */
    private void addBarrenWorlds(StarSystem system, Star star) {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        String          planetName;
        int             orbit = 1;
        long            distance = star.getMinimumDistance() + Die.d10(2);
        List<Planet> allPlanets = new ArrayList<Planet>();
        List<Planet> planets;

        switch (Die.d6()) {
            case 1:
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.VulcanianBelt, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += planets.get(0).getRadius() * 3;
                allPlanets.addAll(planets);

                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.Saturnian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += (planets.get(0).getRadius() / 500) + Die.dieV(20);
                allPlanets.addAll(planets);
                break;
            case 2: case 3:
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.Hermian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                allPlanets.addAll(planets);
                break;
            case 4: case 5:
                planetName = StarSystemFactory.getPlanetName(star, orbit++);
                planets = planetFactory.createPlanet(system, star, planetName, PlanetType.Ferrinian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                allPlanets.addAll(planets);
                break;
            case 6:
                distance += 50 + Die.d20(3);
                break;
        }

        if (Die.d2() == 1) {
            planetName = StarSystemFactory.getPlanetName(star, orbit++);
            planets = planetFactory.createPlanet(system, star, planetName, PlanetType.AsteroidBelt, distance);
            logger.info(String.format("Created world [%s]", planetName));
            distance += planets.get(0).getRadius() * 3;
            allPlanets.addAll(planets);
        }

        system.setPlanets(allPlanets);
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
        String          planetName;
        int             orbit = 1;
        long            distance = primary.getMinimumDistance() + secondary.getMinimumDistance();
        distance += secondary.getDistance() + Die.d6(3);
        List<Planet> allPlanets = new ArrayList<Planet>();
        List<Planet> planets;

        Star center = new Star(system.getName(), system, 0, 0,
                primary.getLuminosity(), primary.getSpectralType());
        center.setMass(primary.getMass() + secondary.getMass());

        switch (Die.d6()) {
            case 1:
                planetName = StarSystemFactory.getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.VulcanianBelt, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += planets.get(0).getRadius() * 3;
                allPlanets.addAll(planets);

                planetName = StarSystemFactory.getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Saturnian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += (planets.get(0).getRadius() / 400) + Die.dieV(20);
                allPlanets.addAll(planets);
                break;
            case 2: case 3:
                planetName = StarSystemFactory.getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Hermian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                allPlanets.addAll(planets);
                break;
            case 4: case 5:
                planetName = StarSystemFactory.getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Ferrinian, distance);
                logger.info(String.format("Created world [%s]", planetName));
                distance += Die.d20(3);
                allPlanets.addAll(planets);
                break;
            case 6:
                distance += 50 + Die.d20(3);
                break;
        }

        if (Die.d2() == 1) {
            planetName = StarSystemFactory.getPlanetName(primary, orbit++);
            planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt, distance);
            logger.info(String.format("Created world [%s]", planetName));
            distance += planets.get(0).getRadius() * 3;
            allPlanets.addAll(planets);
        }

        system.setPlanets(allPlanets);
    }
}
