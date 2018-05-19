/**
 * Sector.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.sectors;

import javax.persistence.*;

/**
 * Defines a sector of space. A sector consists of a region 32 parsecs wide by 40 parsecs high.
 * Location 0101 is the top left hex of the sector, and 3240 is the bottom right.
 *
 * Each sector is divided into 16 sub-sectors, labelled A-P, each 8x10 parsecs in size. A parsec
 * is represented on a Sector map as a single hex, which may contain zero or one star systems.
 *
 * A sector can be identified by its name (a string), or by its coordinates. 0,0 is considered
 * to be the origin sector for the whole map, and other sectors will have negative or positive
 * coordinates.
 */
@Entity
@Table(name = "sectors")
public class Sector {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "x")
    private int x;

    @Column(name = "y")
    private int y;

    // Width of a sector.
    public static final int WIDTH = 32;
    // Height of a sector.
    public static final int HEIGHT = 40;
    // Maximum length of sector name.
    private static final int MAX_NAME_LENGTH = 64;

    /**
     * Private empty constructor.
     */
    Sector() {
        this.id = 0;
        this.name = "Unnamed";
        this.x = 0;
        this.y = 0;
    }

    /**
     * Validate and clean the provided name. A sector name cannot be empty or null, and
     * can't be a pure numeric. It is trimmed before being returned.
     *
     * @param name  Name to validate.
     * @return      Validated and cleaned name.
     */
    private String validateName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Sector name must be non-empty.");
        }
        try {
            Long.parseLong(name);
            throw new IllegalArgumentException("Sector name cannot be a numeric.");
        } catch (NumberFormatException e) {
            // This is okay because it wasn't a number.
        }
        if (name.trim().length() > Sector.MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Sector name cannot exceed %d characters.", Sector.MAX_NAME_LENGTH));
        }

        return name.trim();

    }

    /**
     * Public constructor.
     *
     * @param name  Unique name for this sector.
     * @param x     X coordinate for this sector.
     * @param y     Y coordinate for this sector.
     */
    public Sector(final String name, final int x, final int y) {
        this.name = validateName(name);
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the unique numerical id for this sector.
     *
     * @return  Unique id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique numerical id for this sector.
     *
     * @param id    Unique id.
     */
    private void setId(final int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Unique sector id must be strictly positive.");
        }
        this.id = id;
    }

    /**
     * Gets the name of this sector. Sector names are unique and cannot be a pure numeric.
     *
     * @return  Sector name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this sector. Sector names must be non-empty, unique and cannot be a pure numeric.
     *
     * @param name  Sector name.
     */
    private void setName(final String name) {
        this.name = validateName(name);
    }

    public int getX() {
        return x;
    }

    private void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    private void setY(final int y) {
        this.y = y;
    }

    public String getSubSectorName(int x, int y) {
        String names = "ABCDEFGHIJKLMNOP";

        int sx = (x - 1) / (WIDTH / 4);
        int sy = (y - 1) / (HEIGHT / 4);

        int pos = (sy) * 4 + sx;

        return names.substring(pos, pos + 1);
    }
}
