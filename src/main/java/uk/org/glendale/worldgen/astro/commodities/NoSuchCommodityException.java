/**
 * NoSuchPlanetException.java
 *
 * Copyright (c) 2011, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.commodities;

import uk.org.glendale.worldgen.exceptions.NoSuchObjectException;

/**
 * Exception type thrown if a commodity cannot be found in the database.
 */
public class NoSuchCommodityException extends NoSuchObjectException {
    /**
     * Commodity referenced by unique id could not be found.
     *
     * @param id        Id of commodity that was searched for.
     */
    public NoSuchCommodityException(int id) {
        super(String.format("Cannot find commodity with id [%d]", id));
    }

    /**
     * Commodity referenced by unique name could not be found.
     *
     * @param name      Name of commodity that was searched for.
     */
    public NoSuchCommodityException(String name) {
        super(String.format("Cannot find commodity with name [%s]", name));
    }
}
