/**
 * StarFactory.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;

import org.hibernate.exception.ConstraintViolationException;
import uk.org.glendale.worldgen.astro.sectors.DuplicateSectorException;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import javax.persistence.EntityManager;

public class StarFactory {
    private final EntityManager session;

    /**
     * Creates a new star factory.
     *
     * @param session   Persistence session.
     */
    public StarFactory(EntityManager session) {
        this.session = session;
    }

    /**
     * Gets a star according to its unique id.
     *
     * @param id    Unique id of star.
     * @return      Found star.
     */
    public Star getStar(int id) throws NoSuchStarException {
        Star star = session.find(Star.class, id);

        if (star == null) {
            throw new NoSuchStarException(id);
        }

        return star;
    }

    /**
     * Creates a new star with the given name and details.
     *
     * @param system
     * @param name
     * @param luminosity
     * @param type
     * @return
     */
    public Star createStar(StarSystem system, String name, Luminosity luminosity, SpectralType type) throws DuplicateStarException {
       Star star = new Star();

       star.setSystem(system);
       star.setDistance(0);
       star.setParentId(0);
       star.setName(name);
       star.setLuminosity(luminosity);
       star.setSpectralType(type);

       persist(star);

       return star;
    }

    public void persist(Star star) throws DuplicateStarException {
        try {
            session.persist(star);
        } catch (ConstraintViolationException e) {
            throw new DuplicateStarException(star);
        }
    }
}
