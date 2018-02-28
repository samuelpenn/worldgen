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

    public static final String FIRST_SUFFIX = " Alpha";
    public static final String SECOND_SUFFIX = " Beta";
    public static final String THIRD_SUFFIX = " Gamma";

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
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));
        primary.setLuminosity(luminosity);
        primary.setSpectralType(type);

        factory.persist(primary);

        return primary;
    }

    /**
     * Generates a specific class of star, and randomly generates its spectral type from that.
     *
     * @param luminosity    The general size of the star.
     * @return              Created and persisted star.
     */
    public Star generatePrimary(Luminosity luminosity) throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));
        primary.setLuminosity(luminosity);
        primary.setSpectralType(luminosity.getSpectralType());

        factory.persist(primary);

        return primary;
    }

    /**
     * Generate a primary star for an empty star system. An empty system has a single star with no planets,
     * and the star type will generally be a dwarf star of some kind.
     *
     * @return	A suitable random cool dwarf star.
     */
    public Star generateEmptyPrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        if (multipleStars) {
            primary.setName(system.getName() + FIRST_SUFFIX);
        } else {
            primary.setName(system.getName());
        }

        switch (Die.d6(2)) {
            case 2: case 3:
                primary.setLuminosity(Luminosity.D);
                break;
            case 4: case 5: case 6:
                if (multipleStars) {
                    primary.setLuminosity(Luminosity.D);
                } else {
                    primary.setLuminosity(Luminosity.VII);
                }
                break;
            case 7: case 8: case 9:
                primary.setLuminosity(Luminosity.VI);
                break;
            case 10:
                primary.setLuminosity(Luminosity.V);
                break;
            case 11:
                primary.setLuminosity(Luminosity.II);
                break;
            case 12:
                primary.setLuminosity(Luminosity.Ib);
                break;
        }
        primary.setSpectralType(primary.getLuminosity().getSpectralType());
        factory.persist(primary);

        return primary;
    }


    /**
     * Generate a primary star for an empty star system. An empty system has a single star with no planets,
     * and the star type will generally be a dwarf star of some kind.
     *
     * @return	A suitable random cool dwarf star.
     */
    public Star generateEmptySecondary() throws DuplicateStarException {
        if (primary.getLuminosity().getCompanionStar() != null) {
            secondary = new Star();
            secondary.setSystem(system);
            secondary.setName(system.getName() + SECOND_SUFFIX);

            secondary.setLuminosity(primary.getLuminosity().getCompanionStar());
            secondary.setSpectralType(secondary.getLuminosity().getSpectralType());

            // Distance, in millions of kilometres.
            secondary.setDistance(100 + Die.d20(2) * 100);
            secondary.setParentId(primary.getId());
            factory.persist(secondary);
        }

        return secondary;
    }


    /**
     * Generate a primary star for a simple star system. A simple system
     * is very boring, so always has a single Class V star.
     *
     * @return	A suitable, random class V star.
     */
    public Star generateSimplePrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        primary.setLuminosity(Luminosity.V);
        primary.setSpectralType(Luminosity.V.getSpectralType());

        return primary;
    }

    /**
     * Generates a dwarf star, most likely class VI, but a chance of VII or V as well.
     *
     * @return  Created and persisted star.
     * @throws DuplicateStarException
     */
    public Star generateDwarfPrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        switch (Die.d6(2)) {
            case 2: case 3: case 4:
                primary.setLuminosity(Luminosity.VII);
                break;
            case 5: case 6: case 7: case 8: case 9: case 10:
                primary.setLuminosity(Luminosity.VI);
                break;
            case 11: case 12:
                primary.setLuminosity(Luminosity.V);
                break;
        }
        primary.setSpectralType(primary.getLuminosity().getSpectralType());
        factory.persist(primary);

        return primary;
    }

    /**
     * Generates a brown dwarf star. There is a small chance of a type VI instead.
     *
     * @return  Created and persisted star.
     * @throws DuplicateStarException
     */
    public Star generateBrownDwarfPrimary() throws DuplicateStarException {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? FIRST_SUFFIX : ""));

        switch (Die.d6(2)) {
            case 2: case 3: case 4: case 5:
                primary.setLuminosity(Luminosity.VI);
                break;
            default:
                primary.setLuminosity(Luminosity.VII);
                break;
        }
        primary.setSpectralType(primary.getLuminosity().getSpectralType());
        factory.persist(primary);

        return primary;
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
                break;
            case 4: case 5: case 6:
                primary.setLuminosity(Luminosity.N);
                break;
            default:
                primary.setLuminosity(Luminosity.D);
                break;
        }
        primary.setSpectralType(primary.getLuminosity().getSpectralType());
        factory.persist(primary);

        return primary;
    }

    public Star generatePrimary() {
        primary = new Star();
        primary.setSystem(system);
        primary.setName(system.getName() + ((multipleStars) ? " Alpha" : ""));

        Luminosity luminosity = null;

        // Select the general class of the star. Smaller numbers
        // are larger stars.
        switch (Die.d6(3)) {
            case 3:
                luminosity = Luminosity.II;
                break;
            case 4:
            case 5:
                luminosity = Luminosity.III;
                break;
            case 6:
            case 7:
                luminosity = Luminosity.IV;
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                luminosity = Luminosity.V;
                break;
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                luminosity = Luminosity.VI;
                break;
        }
        primary.setLuminosity(luminosity);
        primary.setSpectralType(luminosity.getSpectralType());

        return primary;
    }

    public Star generateSecondary(int distance) throws DuplicateStarException {
        if (!multipleStars) {
            throw new IllegalStateException("This system has only one star");
        }
        if (primary == null || primary.getId() == 0) {
            throw new IllegalStateException("Primary star has not been defined");
        }
        secondary = new Star();
        secondary.setSystem(system);
        secondary.setName(system.getName() + " Beta");

        secondary.setLuminosity(primary.getLuminosity().getCompanionStar());
        secondary.setSpectralType(secondary.getLuminosity().getSpectralType());

        // This is a place holder value.
        secondary.setParentId(primary.getId());
        secondary.setDistance(distance);

        factory.persist(secondary);

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

        tertiary.setLuminosity(Luminosity.D);
        tertiary.setSpectralType(SpectralType.D7);

        tertiary.setParentId(secondary.getId());
        tertiary.setDistance(Die.d10(5) * 1000);

        return tertiary;
    }
}
