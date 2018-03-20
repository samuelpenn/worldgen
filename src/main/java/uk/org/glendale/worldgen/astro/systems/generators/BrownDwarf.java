/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
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
import uk.org.glendale.worldgen.astro.stars.DuplicateStarException;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.stars.StarGenerator;
import uk.org.glendale.worldgen.astro.systems.*;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

/**
 * A Brown Dwarf is not quite a star, not having enough mass to properly undergo fusion. A system with a
 * brown dwarf primary is cold and dark, and often doesn't have much in the way of a planetary system.
 *
 * Brown dwarfs have a Luminosity of V, putting them on the main sequence. However, they are not truly
 * stars, and have a spectral type of L, Y or T.
 */
public class BrownDwarf extends StarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(BrownDwarf.class);

    public BrownDwarf(WorldGen worldgen) {
        super(worldgen);
    }

    @Override
    public StarSystem generate(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        StarSystem system = createEmptySystem(sector, name, x, y);

        switch (Die.d6(3)) {
            case 10: case 11:
                createLoneDwarf(system);
                break;
            case 12: case 13:
                createDwarfWithRing(system);
                break;
            case 14: case 15:
                createDwarfWithAsteroid(system);
                break;
            case 16: case 17:
                createConjoinedTwins(system);
                break;
            default:
                createLoneDwarf(system);
                break;
        }

        updateStarSystem(system);
        return system;
    }

    /**
     * Create a single small brown dwarf star with no planets or rings.
     *
     * @param system    System this star is created in.
     * @throws DuplicateStarException   Internal error, shouldn't occur.
     */
    public void createLoneDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        starGenerator.generateBrownDwarfPrimary();
    }

    /**
     * Creates a single small brown dwarf star with a single dust ring.
     *
     * @param system    System this star is created in.
     * @throws DuplicateStarException   Internal error, shouldn't occur.
     */
    public void createDwarfWithRing(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star dwarf = starGenerator.generateBrownDwarfPrimary();

        String planetName = StarSystemFactory.getPlanetName(dwarf, 1);
        int    distance = 30 + Die.d20(5);

        planetFactory.createPlanet(system, dwarf, planetName, PlanetType.DustDisc, distance);
    }

    /**
     * Creates a single small brown dwarf star with a single asteroid in ordit.
     *
     * @param system    System this star is created in.
     * @throws DuplicateStarException   Internal error, shouldn't occur.
     */
    public void createDwarfWithAsteroid(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star dwarf = starGenerator.generateBrownDwarfPrimary();

        String      planetName = StarSystemFactory.getPlanetName(dwarf, 1);
        int         distance = 20 + Die.d20(3);
        PlanetType  type = null;

        switch (Die.d6()) {
            case 1:
                type = PlanetType.Aggregate;
                break;
            case 2: case 3:
                type = PlanetType.Gelidaceous;
                break;
            default:
                type = PlanetType.Carbonaceous;
                break;
        }

        planetFactory.createPlanet(system, dwarf, planetName, type, distance);
    }

    public void createConjoinedTwins(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.CONJOINED_BINARY);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, true);
        Star primary = starGenerator.generateBrownDwarfPrimary();
        Star secondary = new Star(primary);

        starGenerator.generateSecondary(secondary);
        starGenerator.persist(secondary);

        primary.setParentId(secondary.getId());
        secondary.setParentId(primary.getId());

        // Work out orbital distance and period of the pair. The stored distance is in MKm,
        // and the actual value is probably less than 1Mkm, so will get rounded down to zero.
        // Work out period before rounding down, so that this is at least accurate.
        double totalMass = primary.getMass() + secondary.getMass();
        int    radius = primary.getRadius() + secondary.getRadius();

        long period = Physics.getOrbitalPeriod(totalMass * Physics.SOL_MASS, radius * 1000);
        primary.setPeriod(period);
        secondary.setPeriod(period);

        starGenerator.persist(primary);
        starGenerator.persist(secondary);


        String      planetName = StarSystemFactory.getPlanetName(primary, 1);
        int         distance = 20 + Die.d20(3);
        PlanetType  type = null;
    }

    public void createClosePair(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.CONJOINED_BINARY);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, true);
        Star primary = starGenerator.generateBrownDwarfPrimary();
        Star secondary = new Star(primary);

        while (Die.d2() == 1) {
            secondary.setSpectralType(secondary.getSpectralType().getColder());
            secondary.setRadius(secondary.getRadius() - 500);
        }
        secondary.setStandardMass();
        starGenerator.generateSecondary(secondary);
        starGenerator.persist(secondary);
        System.out.println(system.getStars().size());

        System.out.println("Have secondary " + secondary.getName() + " with " + secondary.getId());
        primary.setParentId(secondary.getId());
        secondary.setParentId(primary.getId());

        // Work out orbital distance of the pair.
        double totalMass = primary.getMass() + secondary.getMass();
        int    radius = primary.getRadius() + secondary.getRadius();

        long period = Physics.getOrbitalPeriod(totalMass * Physics.SOL_MASS, radius * 1000);

        primary.setPeriod(period);
        secondary.setPeriod(period);

        starGenerator.persist(primary);
        starGenerator.persist(secondary);


        String      planetName = StarSystemFactory.getPlanetName(primary, 1);
        int         distance = 20 + Die.d20(3);
        PlanetType  type = null;
    }
}
