/**
 * DuplicateStarException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;

import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

public class DuplicateStarException extends DuplicateObjectException {

    public DuplicateStarException(Star star) {
        super(String.format("Cannot store duplicate star object in system [%d] [%s]",
                star.getSystem().getId(), star.getName()));
    }
}
