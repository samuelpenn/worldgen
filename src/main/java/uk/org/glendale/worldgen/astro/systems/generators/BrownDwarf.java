/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.DuplicateStarException;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.stars.StarGenerator;
import uk.org.glendale.worldgen.astro.systems.*;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

import static uk.org.glendale.worldgen.astro.Physics.MKM;

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
                createClosePair(system);
                break;
            case 18:
                createConjoinedTwins(system);
                break;
            default:
                createLoneDwarf(system);
                break;
        }

        updateStarSystem(system);
        return system;
    }

    public void colonise(StarSystem system) {

    }

    /**
     * Create a single small brown dwarf star with no planets or rings.
     *
     * @param system    System this star is created in.
     * @throws DuplicateStarException   Internal error, shouldn't occur.
     */
    @SuppressWarnings("WeakerAccess")
    public void createLoneDwarf(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        starGenerator.generateBrownDwarfPrimary();

        setDescription(system, null);
    }

    /**
     * Creates a single small brown dwarf star with a single dust ring.
     *
     * @param system    System this star is created in.
     * @throws DuplicateStarException   Internal error, shouldn't occur.
     */
    @SuppressWarnings("WeakerAccess")
    public void createDwarfWithRing(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star dwarf = starGenerator.generateBrownDwarfPrimary();

        String planetName = StarSystemFactory.getBeltName(dwarf, 1);
        long   distance = Die.d20(5) * MKM + Die.die(1_000_000);

        planetFactory.createPlanet(system, dwarf, planetName, PlanetType.DustDisc, Physics.round(distance));
    }

    /**
     * Creates a single small brown dwarf star with a single asteroid in orbit.
     *
     * @param system    System this star is created in.
     * @throws DuplicateStarException   Internal error, shouldn't occur.
     */
    @SuppressWarnings("WeakerAccess")
    public void createDwarfWithAsteroid(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        Star dwarf = starGenerator.generateBrownDwarfPrimary();

        String      planetName = StarSystemFactory.getPlanetName(dwarf, 1);
        int         distance = 20 + Die.d20(3);
        PlanetType  type;

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

    /**
     * Creates two very similar stars in very tight orbit around each other, close enough that their
     * atmospheres merge.
     *
     * @param system    System to create stars in.
     * @throws DuplicateStarException   If there is an error.
     */
    @SuppressWarnings("WeakerAccess")
    public void createConjoinedTwins(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.CONJOINED_BINARY);
        system.setZone(Zone.AMBER);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, true);
        Star primary = starGenerator.generateBrownDwarfPrimary();
        Star secondary = new Star(primary);

        secondary.setSpectralType(secondary.getSpectralType().getColder());
        StarGenerator.calculateBrownDwarf(secondary);

        starGenerator.generateSecondary(secondary);
        starGenerator.persist(secondary);

        primary.setParentId(StarSystem.PRIMARY_COG);
        secondary.setParentId(StarSystem.PRIMARY_COG);

        // Work out orbital distance and period of the pair.
        double totalMass = primary.getMass() + secondary.getMass();
        int    radius = primary.getRadius() + secondary.getRadius();
        radius *= (1.0 + Die.d10() / 100.0);

        long period = Physics.getOrbitalPeriod(totalMass * Physics.SOL_MASS, radius * 1000);
        primary.setPeriod(period);
        secondary.setPeriod(period);

        primary.setDistance((long)(radius * secondary.getMass() / totalMass));
        secondary.setDistance((long)(radius * primary.getMass() / totalMass));

        starGenerator.persist(primary);
        starGenerator.persist(secondary);

        String      planetName = StarSystemFactory.getBeltName(primary, 1);
        long        distance = Physics.round((3 + Die.d6(3)) * MKM + Die.die(1000000));

        planetFactory.createPlanet(system, primary, planetName, PlanetType.DustDisc, distance);
    }

    /**
     * Creates two stars in close orbit around each other, a few million kilometres in separation.
     *
     * @param system
     * @throws DuplicateStarException
     */
    @SuppressWarnings("WeakerAccess")
    public void createClosePair(StarSystem system) throws DuplicateStarException {
        PlanetFactory planetFactory = worldgen.getPlanetFactory();

        system.setType(StarSystemType.CLOSE_BINARY);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, true);
        Star primary = starGenerator.generateBrownDwarfPrimary();
        Star secondary = new Star(primary);

        while (Die.d2() == 1) {
            secondary.setSpectralType(secondary.getSpectralType().getColder());
        }
        StarGenerator.calculateBrownDwarf(secondary);
        starGenerator.generateSecondary(secondary);
        starGenerator.persist(secondary);

        primary.setParentId(StarSystem.PRIMARY_COG);
        secondary.setParentId(StarSystem.PRIMARY_COG);

        // Work out orbital distance and period of the pair.
        double totalMass = primary.getMass() + secondary.getMass();
        int    radius = 250000 + Die.die(1000000, 5);

        long period = Physics.getOrbitalPeriod(totalMass * Physics.SOL_MASS, radius * 1000);
        primary.setPeriod(period);
        secondary.setPeriod(period);

        primary.setDistance(Physics.round(radius * secondary.getMass() / totalMass));
        secondary.setDistance(Physics.round(radius * primary.getMass() / totalMass));

        starGenerator.persist(primary);
        starGenerator.persist(secondary);

        String      planetName = StarSystemFactory.getBeltName(primary, 1);
        long        distance = Physics.round((radius * 3 + Die.die(10000000)));

        planetFactory.createPlanet(system, primary, planetName, PlanetType.DustDisc, distance);
    }
}
