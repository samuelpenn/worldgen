/**
 * StarSystemFactory.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

import org.hibernate.exception.ConstraintViolationException;
import uk.org.glendale.worldgen.astro.sectors.DuplicateSectorException;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.Star;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

public class StarSystemFactory {
    private final EntityManager session;

    private static String BY_SECTOR_QUERY = "FROM StarSystem WHERE sectorId = :sector  ORDER BY x, y";
    private static String BY_XY_QUERY = "FROM StarSystem WHERE sectorId = :sector AND x = :x AND y = :y";
    private static String BY_NAME_QUERY = "FROM StarSystem WHERE sectorId = :sector AND name = :name";


    public StarSystemFactory(EntityManager session) {
        this.session = session;
    }

    public StarSystem createStarSystem(Sector sector, String name, int x, int y, StarSystemType type) throws DuplicateStarSystemException {
        StarSystem system = new StarSystem(sector, name, x, y, type, Zone.GREEN);
        persist(system);

        return system;
    }

    public List<StarSystem> getStarSystems(Sector sector) {
        List<StarSystem> list = null;
        Query query = session.createQuery(BY_SECTOR_QUERY);
        query.setParameter("sector", sector.getId());

        return (List<StarSystem>) query.getResultList();
    }

    /**
     * Gets a count of the total number of star systems.
     *
     * @return      Number of star systems.
     */
    public int getStarSystemCount() {
        Query query = session.createNativeQuery("SELECT COUNT(*) FROM systems");
        List<BigInteger> count = (List<BigInteger>) query.getResultList();

        return (int) count.get(0).intValue();
    }

    public void persist(StarSystem system) throws DuplicateStarSystemException {
        try {
            session.persist(system);
            session.flush();
        } catch (ConstraintViolationException e) {
            throw new DuplicateStarSystemException(system);
        }
    }

    /**
     * Gets the star system identified by its unique id.
     *
     * @param id    Id of the star system to find.
     * @return      A star system if one is found.
     *
     * @throws NoSuchStarSystemException    Thrown if the system does not exist.
     */
    public StarSystem getStarSystem(int id) throws NoSuchStarSystemException {
        StarSystem system = session.find(StarSystem.class, id);

        if (system == null) {
            throw new NoSuchStarSystemException(id);
        }
        return system;
    }

    /**
     * Gets the star system at the given location in a sector.
     *
     * @param sector    Sector to look in.
     * @param x         X coordinate in the sector (1-32).
     * @param y         Y coordinate in the sector (1-40).
     * @return          Star system if one is found.
     * @throws NoSuchStarSystemException    Thrown if the system does not exist.
     */
    public StarSystem getStarSystem(Sector sector, int x, int y) throws NoSuchStarSystemException {
        Query query = session.createQuery(BY_XY_QUERY);
        query.setParameter("sector", sector.getId());
        query.setParameter("x", x);
        query.setParameter("y", y);

        try {
            return (StarSystem) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchStarSystemException(sector, x, y);
        }
    }

    /**
     * Gets the named star system in a sector. Each system in a given sector will have
     * a unique name. System names aren't guaranteed to be unique across all sectors.
     *
     * @param sector    Sector to look in.
     * @param name      Name of the system.
     * @return          Star system if one is found.
     * @throws NoSuchStarSystemException    Thrown if the system does not exist.
     */
    public StarSystem getStarSystem(Sector sector, String name) throws NoSuchStarSystemException {
        Query query = session.createQuery(BY_NAME_QUERY);
        query.setParameter("sector", sector.getId());
        query.setParameter("name", name);

        try {
            return (StarSystem) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchStarSystemException(sector, name);
        }
    }

    /**
     * Is there an existing star system at the given coordinates?
     *
     * @param sector    Sector to look in.
     * @param x         X coordinate in the sector (1-32).
     * @param y         Y coordinate in the sector (1-40).
     * @return          True iff a star system is defined at these coordinates, false otherwise.
     */
    public boolean hasStarSystem(Sector sector, int x, int y) {
        Query query = session.createQuery(BY_XY_QUERY);
        query.setParameter("sector", sector.getId());
        query.setParameter("x", x);
        query.setParameter("y", y);

        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            // No system found.
        }
        return false;
    }

    private static String getRoman(int value) {
        switch (value) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
        }
        if (value > 10) {
            return "X" + getRoman(value - 10);
        } else {
            return "";
        }
    }

    private static String getLetter(int value) {
        final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (value > 0 && value < 27)  {
            return LETTERS.substring(value - 1, value);
        } else if (value > 26) {
            return getLetter(value / 26) + getLetter(value % 26);
        }
        return "";
    }

    private static void validateCoord(String coord) {
        if (coord != null && coord.length() == 4) {
            try {
                //noinspection ResultOfMethodCallIgnored
                Integer.parseInt(coord);
            } catch (NumberFormatException e) {
                // Invalid format.
            }
            return;
        }
        throw new IllegalArgumentException(String.format("Invalid coordinate format [%s], must be XXYY", coord));
    }

    public static int getXCoord(String coord) {
        validateCoord(coord);

        int x = Integer.parseInt(coord.substring(0, 2));
        if (x >= 1 && x <= 32) {
            return x;
        }
        throw new IllegalArgumentException("X coordinate must be within 1..32");
    }

    public static int getYCoord(String coord) {
        validateCoord(coord);

        int y = Integer.parseInt(coord.substring(2, 4));
        if (y >= 1 && y <= 40) {
            return y;
        }
        throw new IllegalArgumentException("Y coordinate must be within 1..40");
    }

    /**
     * Gets the name of a planet given its orbital position. Generally a planet is named
     * after the star, with a roman numeral suffix (I, II, III) from innermost to outermost.
     * The third planet around the Sol star would be Sol III.
     *
     * @param star      Star planet is in orbit around.
     * @param orbit     Orbit number, from 1 (innermost) upwards.
     * @return          The name of the planet.
     */
    public static String getPlanetName(Star star, int orbit) {
        return star.getName() + " " + getRoman(orbit);
    }

    /**
     * Gets the name of a belt given its orbital position. Generally a belt is named after
     * the star, with the innermost belt having a suffix of "Belt A", and going upwards.
     * The first belt around the Sol star would be Sol Belt A.
     *
     *
     * @param star      Star belt is in orbit around.
     * @param orbit     Orbit number, from 1 (innermost) upwards.
     * @return          The name of the belt.
     */
    public static String getBeltName(Star star, int orbit) {
        return star.getName() + " Belt " + getLetter(orbit);
    }

    /**
     * Gets the name of a moon around a planet. Moons count from 'a' innermost to outermost.
     * So the first moon of the third planet of the Sol system would be Sol IIIa.
     *
     * @param baseName  Name of the planet the moon is in orbit around.
     * @param moon      Orbit number, from 1 (innermost) upwards.
     * @return          The name of the moon.
     */
    public static String getMoonName(String baseName, int moon) {
        return baseName + getLetter(moon).toLowerCase();
    }

    /**
     * Gets the name of a planetoid in a belt. Planetoids that are large enough to be named
     * are numbered using lower case roman numerals, starting at i. So the innermost named
     * planetoid of a belt might be Sol Belt A-i
     *
     * @param baseName  Name of the belt the moon is in orbit around.
     * @param orbit     Orbit number, from 1 (innermost) upwards.
     * @return          The name of the planetoid.
     */
    public static String getPlanetoidName(String baseName, int orbit) {
        return baseName + "-" + getRoman(orbit).toLowerCase();
    }
}
