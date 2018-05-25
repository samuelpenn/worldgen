/**
 * SpectralType.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;

public class NoSuchStarSystemException extends NoSuchObjectException {
    public NoSuchStarSystemException(int id) {
        super(String.format("Cannot find star system [%d]", id));
    }

    public NoSuchStarSystemException(Sector sector, int x, int y) {
        super(String.format("Cannot find star system at [%s]/[%02d%02d]",
                sector.getName(), x, y));
    }

    public NoSuchStarSystemException(Sector sector, String name) {
        super(String.format("Cannot find star system [%s]/[%s]",
                sector.getName(), name));
    }

}
