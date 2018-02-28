/**
 * Sector.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.sectors;

import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;

/**
 * Thrown when a requested Sector does not exist.
 */
public class NoSuchSectorException extends NoSuchObjectException {
    /**
     * Sector identified by unique id does not exist.
     * @param id    Id used to find Sector.
     */
    public NoSuchSectorException(int id) {
        super(String.format("Sector [%d] does not exist", id));
    }

    /**
     * Sector identified by unique name does not exist.
     * @param name  Name used to find Sector.
     */
    public NoSuchSectorException(String name) {
        super(String.format("Sector [%s] does not exist", name));
    }

    /**
     * Sector identified by coordinates does not exist.
     * @param x     X coordinate used to find Sector.
     * @param y     Y coordinate used to find Sector.
     */
    public NoSuchSectorException(int x, int y) {
        super(String.format("Sector [%d,%d] does not exist", x, y));
    }
}
