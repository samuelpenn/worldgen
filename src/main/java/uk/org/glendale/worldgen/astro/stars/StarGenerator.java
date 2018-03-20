/**
 * StarSystemType.java
 *
 * Copyright (c) 2011, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.sectors.SectorGenerator;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

/**
 * Generates stars for a star system. Created stars are automatically persisted upon creation.
 *
 * @author Samuel Penn
 */
public class StarGenerator {
    private static final Logger logger = LoggerFactory.getLogger(StarGenerator.class);
    private final WorldGen worldgen;
    private final StarFactory factory;

    private StarSystem system;
    private boolean multipleStars;
    private Star primary, secondary, tertiary;

    private static final String FIRST_SUFFIX = " Alpha";
    private static final String SECOND_SUFFIX = " Beta";
    private static final String THIRD_SUFFIX = " Gamma";

    /**
     * Star Generator for a system that has only a single star.
     *
     * @param worldgen
     * @param system
     */
    public StarGenerator(WorldGen worldgen, StarSystem system) {
        this.worldgen = worldgen;
        this.system = system;
        this.multipleStars = false;
        this.factory = worldgen.getStarFactory();
    }

    /**
     * Star Generator for a system that might have multiple stars.
     *
     * @param worldgen
     * @param system
     * @param multipleStars
     */
    public StarGenerator(WorldGen worldgen, StarSystem system, boolean multipleStars) {
        this.worldgen = worldgen;
        this.system = system;
        this.multipleStars = multipleStars;
        this.factory = worldgen.getStarFactory();
    }

    private Star generateStar(String name, Luminosity luminosity, SpectralType type) {
        Star star = new Star();
        star.setSystem(system);
        star.setName(name);
        star.setLuminosity(luminosity);
        star.setSpectralType(type);
        star.setStandardMass();

        return star;
    }

    /**
     * Generates a specific type of star as the primary.
     *
     * @param luminosity
     *            The general size of the star.
     * @param type
     *            Spectral type.
     * @return      Created and persisted star.
     */
    public Star generatePrimary(Luminosity luminosity, SpectralType type) throws DuplicateStarException {
        primary = generateStar(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""), luminosity, type);
        factory.persist(primary);

        return primary;
    }

    public Star generatePrimary(Star star) throws DuplicateStarException {
        star.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));
        this.primary = star;
        factory.persist(primary);

        return primary;
    }

    public Star generateSecondary(Luminosity luminosity, SpectralType type) throws DuplicateStarException {
        secondary = generateStar(system.getName() + ((multipleStars) ? SECOND_SUFFIX : ""), luminosity, type);
        factory.persist(secondary);

        return secondary;
    }

    public Star generateSecondary(Star star) throws DuplicateStarException {
        star.setName(system.getName() + SECOND_SUFFIX);
        this.secondary = secondary;
        factory.persist(primary);

        return secondary;
    }

    /**
     * Generate a random main sequence 'yellow' star. This will be of type  F, G or K.
     * G type will be most common. F will only be cooler F spectra, K will be warmer K spectra.
     *
     * @return Newly created random 'yellow' star.
     */
    public Star generateYellowStar() {
        Star            star;
        Luminosity      luminosity = Luminosity.V;
        SpectralType    type = SpectralType.G2;

        switch (Die.d6()) {
            case 1:
                // A cool F type star.
                type = SpectralType.valueOf("F" + (Die.d3() + 6));
                break;
            case 2: case 3:
                // A warm K type star.
                type = SpectralType.valueOf("K" + (Die.d4() - 1));
                break;
            case 4: case 5: case 6:
                // A G type star.
                type = SpectralType.valueOf("G" + (Die.d10() - 1));
                break;
        }

        return generateStar("unnamed", luminosity, type);
    }

    /**
     * Generate a random main sequence 'orange' star. These are cooler stars still on the main
     * sequence, with a bias towards 'K' type, with some chance of cool 'G' or warm 'M'.
     *
     * @return      New randomly generated main sequence star.
     */
    public Star generateOrangeStar() {
        Star            star;
        Luminosity      luminosity = Luminosity.V;
        SpectralType    type = SpectralType.K5;

        switch (Die.d6()) {
            case 1:
                // A cool G type star.
                type = SpectralType.valueOf("G" + (Die.d3() + 6));
                break;
            case 2: case 3:
                // A warm M type star.
                type = SpectralType.valueOf("M" + (Die.d3() - 1));
                break;
            case 4: case 5: case 6:
                // A G type star.
                type = SpectralType.valueOf("K" + (Die.d10() - 1));
                break;
        }

        return generateStar("unnamed", luminosity, type);
    }

    /**
     * Generate a random main sequence 'red' star. These are the coolest stars still on the
     * main sequence, with a bias towards 'M' type, with some chance of cool 'K' or warm 'L'.
     *
     * @return      New randomly generate main sequence star.
     */
    public Star generateRedStar() {
        Star            star;
        Luminosity      luminosity = Luminosity.V;
        SpectralType    type = SpectralType.M5;

        switch (Die.d6()) {
            case 1:
                // A warm L type star.
                type = SpectralType.valueOf("L" + (Die.d3() - 1));
                break;
            case 2: case 3:
                // A cool K type star.
                type = SpectralType.valueOf("K" + (Die.d4() + 5));
                break;
            case 4: case 5: case 6:
                // An M type star.
                type = SpectralType.valueOf("M" + (Die.d10() - 1));
                break;
        }

        return generateStar("unnamed", luminosity, type);
    }

    public Star generateWhiteDwarf() {
        Luminosity      luminosity = Luminosity.VII;
        SpectralType    type = SpectralType.valueOf("D" + (Die.d6() - 1));

        return generateStar("unnamed", luminosity, type);
    }

    /**
     * Generate a primary star for a simple star system. A simple system
     * is very boring, so always has a single Class V star.
     *
     * @return	A suitable, random class V star.
     */
    public Star generateSimplePrimary() throws DuplicateStarException {
        Star star = generateYellowStar();
        star.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        return generatePrimary(star);
    }

    /**
     * Generates a dwarf star, most likely class VI, but a chance of VII or V as well.
     *
     * @return  Created and persisted star.
     * @throws DuplicateStarException If there is a duplicate.
     */
    public Star generateDwarfPrimary() throws DuplicateStarException {
        Star star;

        switch (Die.d6(2)) {
            case 2: case 3: case 4:
                star = generateOrangeStar();
                break;
            case 11: case 12:
                star = generateYellowStar();
                break;
            default:
                star = generateRedStar();
                break;
        }
        star.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        return generatePrimary(star);
    }

    /**
     * Generates a brown dwarf star.
     *
     * @return  Created and persisted star.
     * @throws DuplicateStarException If there is a duplicate.
     */
    public Star generateBrownDwarfPrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));
        primary.setLuminosity(Luminosity.V);

        int digit = 0;
        switch (Die.d6()) {
            case 1: case 2:
                // About as hot as a brown dwarf can get.
                digit = 6 + Die.d3();
                primary.setSpectralType(SpectralType.getSpectralType('L', digit));
                primary.setRadius(95000 + (9 - digit) * 250);
                break;
            case 3: case 4: case 5:
                // Methane dwarfs. Often magenta in colour.
                digit = Die.d10() - 1;
                primary.setSpectralType(SpectralType.getSpectralType('T', digit));
                primary.setRadius(90000 + (9 - digit) * 500);
                break;
            case 6:
                // Coolest type of brown dwarf.
                digit = Die.d6() - 1;
                primary.setSpectralType(SpectralType.getSpectralType('Y', digit));
                primary.setRadius(85000 + (9 - digit) * 1000);
                break;
        }
        primary.setStandardMass();
        factory.persist(primary);

        return primary;
    }

    public void persist(Star star) throws DuplicateStarException {
        factory.persist(star);
    }

    /**
     * Generates a random red giant star. Size of Giant is randomly determined, but
     * will be at least Luminosity class III, and has a small chance of being a Ia
     * super giant. Spectral Type will always be M.
     *
     * @return  Primary star that was generated.
     * @throws DuplicateStarException
     */
    public Star generateRedGiantPrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        switch (Die.d6(2)) {
            case 2:
                primary.setLuminosity(Luminosity.Ia);
                break;
            case 3:
                primary.setLuminosity(Luminosity.Ib);
                break;
            case 4: case 5: case 6: case 7:
                primary.setLuminosity(Luminosity.II);
                break;
            default:
                primary.setLuminosity(Luminosity.III);
                break;
        }

        primary.setSpectralType(SpectralType.valueOf("M" + Die.rollZero(10)));
        primary.setStandardMass();

        factory.persist(primary);

        return primary;
    }


    /**
     * Generates a stellar remnant. Most likely to be a white dwarf star. May
     * be a neutron star or (very unlikely) a black hole.
     *
     * @return  Created and persisted star.
     * @throws DuplicateStarException
     */
    public Star generateRemnantPrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        switch (Die.d6(3)) {
            case 3:
                primary.setLuminosity(Luminosity.B);
                primary.setSpectralType(SpectralType.X3);
                break;
            case 4: case 5: case 6:
                primary.setLuminosity(Luminosity.N);
                primary.setSpectralType(SpectralType.X5);
                break;
            default:
                primary.setLuminosity(Luminosity.VII);
                primary.setSpectralType(SpectralType.D3);
                break;
        }
        primary.setStandardMass();

        factory.persist(primary);

        return primary;
    }

    public Star generatePrimary() throws DuplicateStarException {
        Star star = null;
        primary.setSystem(system);

        Luminosity luminosity = null;

        // Select the general class of the star. Smaller numbers
        // are larger stars.
        switch (Die.d6(3)) {
            case 3:
                star = generateRedStar();
                break;
            case 4:
            case 5:
                star = generateRedStar();
                break;
            case 6:
            case 7:
                star = generateRedStar();
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                star = generateOrangeStar();
                break;
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                star = generateYellowStar();
                break;
        }
        generatePrimary(star);

        return primary;
    }

    public Star generateSecondary(int distance) throws DuplicateStarException {
        if (!multipleStars) {
            throw new IllegalStateException("This system has only one star");
        }
        if (primary == null || primary.getId() == 0) {
            throw new IllegalStateException("Primary star has not been defined");
        }
        Star star = generateRedStar();
        star.setParentId(primary.getId());
        star.setDistance(distance);
        generateSecondary(star);

        return secondary;
    }

    public Star generateTertiary() {
        if (!multipleStars) {
            throw new IllegalStateException("This system has only one star");
        }
        if (secondary == null || secondary.getId() == 0) {
            throw new IllegalStateException(
                    "Secondary star has not been defined");
        }
        tertiary = new Star();
        tertiary.setSystem(system);
        tertiary.setName(system.getName() + " Gamma");

        tertiary.setLuminosity(Luminosity.VII);
        tertiary.setSpectralType(SpectralType.D7);

        tertiary.setParentId(secondary.getId());
        tertiary.setDistance(Die.d10(5) * 1000);
        tertiary.setStandardMass();

        return tertiary;
    }
}
