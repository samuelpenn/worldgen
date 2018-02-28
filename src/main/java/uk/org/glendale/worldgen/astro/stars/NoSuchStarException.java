/**
 * SectorFactory.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;

import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;

/**
 * Exception thrown if no Star object can be found in the database.
 */
public class NoSuchStarException extends NoSuchObjectException {

    public NoSuchStarException(int id) {
        super(String.format("Cannot find star with id [%d]", id));
    }
}
