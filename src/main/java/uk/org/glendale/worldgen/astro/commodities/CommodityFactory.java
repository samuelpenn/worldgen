/**
 * Commodity.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.commodities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.worldgen.astro.planets.NoSuchPlanetException;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

/**
 * Factory class for retrieving commodity information from the database.
 */
public class CommodityFactory {
    private static final Logger logger = LoggerFactory.getLogger(CommodityFactory.class);
    private final EntityManager session;

    private static String BY_NAME = "FROM Commodity WHERE name = :name";

    /**
     * Constructor using a session object.
     *
     * @param session   Persistence session to use.
     */
    public CommodityFactory(final EntityManager session) {
        if (session == null || !session.isOpen()) {
            throw new IllegalArgumentException("Session object must be open and non-null.");
        }
        this.session = session;
    }

    /**
     * Gets the commodity defined by its unique id.
     *
     * @param id        Unique id of commodity.
     * @return          Commodity if it exists.
     * @throws NoSuchCommodityException    Thrown if commodity does not exist.
     */
    public Commodity getCommodity(int id) throws NoSuchCommodityException {
        if (id < 1) {
            throw new IllegalArgumentException("Commodity Id must be strictly positive.");
        }
        Commodity commodity = (Commodity) session.find(Commodity.class, id);
        if (commodity == null) {
            throw new NoSuchCommodityException(id);
        }

        return commodity;
    }

    /**
     * Gets the commodity defined by its unique id.
     *
     * @param name      Unique name of commodity.
     * @return          Commodity if it exists.
     * @throws NoSuchCommodityException    Thrown if commodity does not exist.
     */
    public Commodity getCommodity(String name) throws NoSuchCommodityException {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Commodity name must be non-empty.");
        }
        Query query = session.createQuery(BY_NAME);
        query.setParameter("name", name);

        Commodity commodity = (Commodity) query.getSingleResult();
        if (commodity == null) {
            throw new NoSuchCommodityException(name);
        }

        return commodity;
    }
}
