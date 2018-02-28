/**
 * DuplicateSectorException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.sectors;

import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

/**
 * Thrown if there is an attempt to create a duplicate Sector. Sectors must have
 * unique ids, names and coordinates. If any of these constraints are thrown then
 * this exception is thrown.
 */
public class DuplicateSectorException extends DuplicateObjectException {
    /**
     * Constructor for duplicated sector exception. Provides information on the
     * sector that could not be created.
     * 
     * @param sector    Sector object that failed to be persisted.
     */
    public DuplicateSectorException(Sector sector) {
        super(String.format("Cannot store duplicate Sector object [%d, '%s', %d, %d]",
                sector.getId(), sector.getName(), sector.getX(), sector.getY()));
    }
}
