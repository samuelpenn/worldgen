/**
 * SectorFactory.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.sectors;

import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Class for finding and persisting Sectors from the database.
 *
 * A sector is identified by its coordinate. Positive Y is Rimward (down), negative Y is Coreward (up).
 */
public class SectorFactory {
    private final EntityManager session;

    private static String NAME_QUERY = "FROM Sector WHERE name = :name";
    private static String COORD_QUERY = "FROM Sector WHERE x = :x AND y = :y";

    /**
     * Constructor using a session object.
     *
     * @param session   Persistence session to use.
     */
    public SectorFactory(final EntityManager session) {
        if (session == null || !session.isOpen()) {
            throw new IllegalArgumentException("Session object must be open and non-null.");
        }
        this.session = session;
    }

    /**
     * Gets the number of a given Sector according to it's X,Y coordinates.
     * Sectors are numbered in a spiral, starting at 1 at the centre and
     * spiralling out up and clockwise. This number can be used to determine a
     * default name for the sector.
     *
     * Sector 2 has coordinates 0,-1, Sector 3 is 1,-1, Sector 4 1,0 etc.
     *
     * @param x     X-coordinate of the sector.
     * @param y     Y-coordinate of the sector.
     * @return      Number of the sector.
     */
    public static int getSectorNumber(int x, int y) {
        int number;
        if (x == 0 && y == 0) {
            number = 1;
        } else if (Math.abs(x) >= Math.abs(y)) {
            int n = Math.abs(x);
            if (x < 0) {
                number = 4 * (n * n + n) - (n - 1);
                number -= y;
            } else {
                number = 4 * (n * n) - (n - 1);
                number += y;
            }
        } else {
            int n = Math.abs(y);
            if (y < 0) {
                number = 4 * (n * n - n) + (n + 1);
                number += x;
            } else {
                number = 4 * (n * n) + (n + 1);
                number -= x;
            }
        }

        return number;
    }

    /**
     * Checks to see if the given sector identifier is a unique id. Unique ids are
     * always positive integers.
     *
     * @param identifier    String value of identifier.
     * @return              True iff identifier could be a valid Sector id.
     */
    public static boolean isId(String identifier) {
        if (identifier != null && identifier.trim().length() > 0) {
            try {
                if (Integer.parseInt(identifier.trim()) > 0) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // Not a number.
            }
        }
        return false;
    }

    /**
     * Checks to see if the given sector identifier is a coordinate. Coordinates
     * are of the form x,y, where 'x' and 'y' are integers which can be positive
     * or negative.
     *
     * @param identifier    String value of the identifier.
     * @return              True iff identifier could be a valid Sector coordinate.
     */
    public static boolean isCoord(String identifier) {
        if (identifier != null && identifier.trim().length() > 0) {
            if (identifier.trim().matches("-?[0-9]+,-?[0-9]+")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the given sector identifier is a name. Sector names
     * can't be valid ids or coordinates. They must start with a letter, and
     * may contain [a-zA-Z0-9'^"!.,;()*_ %+-] as following characters. They
     * must be less than 64 characters in length.
     *
     * @param identifier    String value of the identifier.
     * @return              True iff identifier is a valid sector name.
     */
    public static boolean isName(String identifier) {
        if (identifier != null && identifier.trim().length() > 0 && identifier.trim().length() < 64) {
            if (!isId(identifier) && !isCoord(identifier)) {
                if (identifier.trim().matches("[a-zA-Z][a-zA-Z0-9'^\"!.,;()*_ %+-]*")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getXCoord(String identifier) {
        if (isCoord(identifier)) {
            return Integer.parseInt(identifier.replaceAll(",.*", ""));
        }
        throw new IllegalArgumentException(String.format("Sector coordinate [%s] is not valid", identifier));
    }

    public static int getYCoord(String identifier) {
        if (isCoord(identifier)) {
            return Integer.parseInt(identifier.replaceAll(".*,", ""));
        }
        throw new IllegalArgumentException(String.format("Sector coordinate [%s] is not valid", identifier));
    }

    public Sector getSectorByIdentifier(String identifier) throws NoSuchSectorException {
        if (SectorFactory.isId(identifier)) {
            return getSector(Integer.parseInt(identifier));
        } else if (SectorFactory.isCoord(identifier)) {
            return getSector(SectorFactory.getXCoord(identifier), SectorFactory.getYCoord(identifier));
        } else if (SectorFactory.isName(identifier)) {
            return getSector(identifier);
        }
        throw new IllegalArgumentException(String.format("Sector identifier [%s] is invalid format", identifier));
    }

    /**
     * Create a new sector and persist it to the database. The newly created sector is returned with
     * its id populated.
     *
     * @param name  Name of the new sector.
     * @param x     X coordinate of the sector.
     * @param y     Y coordinate of the sector.
     * @return      New Sector object.
     * @throws DuplicateSectorException If sector already exists.
     */
    public Sector createSector(final String name, final int x, final int y) throws DuplicateSectorException {
        Sector sector = new Sector(name, x, y);

        persist(sector);

        return sector;
    }

    /**
     * Gets a list of all defined sectors.
     *
     * @return  List of sectors.
     */
    public List<Sector> getSectors() {
        return (List<Sector>) session.createQuery("FROM Sector").getResultList();
    }

    /**
     * Gets a sector according to its unique identifier.
     *
     * @param id    Unique id of Sector to return.
     *
     * @return      Valid Sector object.
     */
    public Sector getSector(int id) throws NoSuchSectorException {
        if (id < 1) {
            throw new IllegalArgumentException("Sector id must be a strictly positive integer.");
        }
        Sector sector = (Sector) session.find(Sector.class, id);

        if (sector == null) {
            throw new NoSuchSectorException(id);
        }

        return sector;
    }

    /**
     * Gets whether a sector exists at the given coordinates. Y coordinates are positive in the Rimward (down)
     * direction, and negative in the Coreward (up) direction. X coordinates are positive for Trailing (right)
     * and negative for Spinward (left).
     *
     * @param x     X coordinate to look.
     * @param y     Y coordinate to look.
     * @return      True iff sector exists, otherwise false.
     */
    public boolean hasSector(int x, int y) {
        try {
            getSector(x, y);
            return true;
        } catch (NoSuchSectorException e) {
            return false;
        }
    }


    public Sector getSector(String name) throws NoSuchSectorException {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Sector name must be non-empty.");
        }

        if (isId(name)) {
            try {
                // If this is an integer, get the Sector by id.
                return getSector(Integer.parseInt(name));
            } catch (NumberFormatException e) {
                // Not a number.
            }
        } else if (isCoord(name)) {
            return getSector(getXCoord(name), getYCoord(name));
        }

        Query query = session.createQuery(NAME_QUERY);
        query.setParameter("name", name);

        try {
            Sector sector = (Sector) query.getSingleResult();
            return sector;
        } catch (NoResultException e) {
            throw new NoSuchSectorException(name);
        }
    }

    public Sector getSector(int x, int y) throws NoSuchSectorException {
        Query query = session.createQuery(COORD_QUERY);
        query.setParameter("x", x);
        query.setParameter("y", y);

        try {
            Sector sector = (Sector) query.getSingleResult();
            if (sector == null) {
                throw new NoSuchSectorException(x, y);
            }

            return sector;
        } catch (NoResultException e) {
            throw new NoSuchSectorException(x, y);
        }
    }

    public void persist(Sector sector) throws DuplicateSectorException {
        try {
            session.persist(sector);
            session.flush();
        } catch (ConstraintViolationException e) {
            throw new DuplicateSectorException(sector);
        }
    }

}
