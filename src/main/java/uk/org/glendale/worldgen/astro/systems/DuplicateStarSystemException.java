/**
 * DuplicateStarSystemException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

public class DuplicateStarSystemException extends DuplicateObjectException {
    public DuplicateStarSystemException(StarSystem system) {
        super(String.format("Cannot store duplicate StarSystem object [%d, '%s', %d, %d]",
                system.getSectorId(), system.getName(), system.getX(), system.getY()));

    }
}
