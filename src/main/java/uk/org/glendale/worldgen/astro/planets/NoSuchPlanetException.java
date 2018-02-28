/**
 * NoSuchPlanetException.java
 *
 * Copyright (c) 2011, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets;

import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;

public class NoSuchPlanetException extends NoSuchObjectException {
    public NoSuchPlanetException(int id) {
        super(String.format("Cannot find planet with id [%d]", id));
    }
}
