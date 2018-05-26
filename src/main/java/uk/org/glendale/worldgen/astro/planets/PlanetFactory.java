/**
 * PlanetFactory.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.ImageBlob;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetClass;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetGroup;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;
import uk.org.glendale.worldgen.exceptions.WorldGenException;
import uk.org.glendale.worldgen.text.TextGenerator;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factory class to create, store and find planets.
 */
public class PlanetFactory {
    private static final Logger logger = LoggerFactory.getLogger(PlanetFactory.class);
    private final EntityManager session;
    private final WorldGen      worldgen;

    private static String SYSTEM_QUERY = "FROM Planet WHERE systemId = :systemId ORDER BY parentId, distance";
    private static String STAR_QUERY = "FROM Planet WHERE parentId = :starId ORDER BY distance";
    private static String FACILITY_QUERY = "FROM Facility WHERE planetId = :planetId ORDER BY id";

    /**
     * Constructor using a session object.
     *
     * @param session   Persistence session to use.
     */
    public PlanetFactory(final WorldGen worldgen, final EntityManager session) {
        if (session == null || !session.isOpen()) {
            throw new IllegalArgumentException("Session object must be open and non-null.");
        }
        this.worldgen = worldgen;
        this.session = session;
    }

    /**
     * Gets the planet defined by its unique id.
     *
     * @param id        Unique id of planet.
     * @return          Planet if it exists.
     * @throws NoSuchPlanetException    Thrown if planet does not exist.
     */
    public Planet getPlanet(int id) throws NoSuchPlanetException {
        if (id < 1) {
            throw new IllegalArgumentException("Planet Id must be strictly positive.");
        }
        Planet planet = (Planet) session.find(Planet.class, id);
        if (planet == null) {
            throw new NoSuchPlanetException(id);
        }

        return planet;
    }

    /**
     * Gets a list of all the planets in the given star system. Returned ordered by the star
     * they are orbiting and their distance from it.
     *
     * @param system    Star system to look in.
     * @return          List of planets, may be an empty list. Ordered by parent and distance.
     */
    public List<Planet> getPlanets(StarSystem system) {
        ArrayList<Planet> planets;

        Query query = session.createQuery(SYSTEM_QUERY);
        query.setParameter("systemId", system.getId());

        planets = (ArrayList<Planet>) query.getResultList();

        return planets;
    }

    /**
     * Gets a list of all the planets around a given star.
     *
     * @param star      Star to get planets for.
     * @return          List of planets, may be an empty list. Ordered by distance.
     */
    public List<Planet> getPlanets(Star star) {
        ArrayList<Planet> planets;
        Query query = session.createQuery(STAR_QUERY);
        query.setParameter("starId", star.getId());

        planets = (ArrayList<Planet>) query.getResultList();

        return planets;
    }

    /**
     * Gets a count of the total number of planets.
     *
     * @return      Number of planets.
     */
    public int getPlanetCount() {
        Query query = session.createNativeQuery("SELECT COUNT(*) FROM planets");
        List<BigInteger> count = query.getResultList();

        return (int) count.get(0).intValue();
    }

    public List<Facility> getFacilities(Planet planet) {
        Query query = session.createQuery(FACILITY_QUERY);
        query.setParameter("planetId", planet.getId());

        return (ArrayList<Facility>) query.getResultList();
    }

    public void setFacility(Facility facility) {
        session.persist(facility);
    }

    /**
     * Stores the map for a given planet. Each map is stored as its own entry in the database,
     * it isn't part of the Planet object itself. Each planet can have several named maps
     * associated with it.
     *
     * @param planetId      Id of planet to store map for.
     * @param name          Name of this map.
     * @param image         Image of the map itself.
     * @throws IOException
     */
    public void setPlanetMap(int planetId, String name, SimpleImage image) throws IOException {
        ByteArrayOutputStream stream = image.save();

        Query query = session.createQuery("FROM PlanetMap G WHERE planetId = :planetId AND name=:name");
        query.setParameter("planetId", planetId);
        query.setParameter("name", name);

        PlanetMap map;
        try {
            map = (PlanetMap) query.getSingleResult();
            map.setData(stream.toByteArray());
        } catch (NoResultException e) {
            map = new PlanetMap(planetId, name, stream.toByteArray());
        }

        session.persist(map);
    }

    public SimpleImage getPlanetMap(int planetId, String name) throws IOException {
        Query query = session.createQuery("FROM PlanetMap G WHERE planetId = :planetId AND name=:name");
        query.setParameter("planetId", planetId);
        query.setParameter("name", name);
        PlanetMap map = (PlanetMap) query.getSingleResult();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(map.getData());
            SimpleImage image = new SimpleImage(ImageIO.read(bais));
            return image;
        } catch (IOException e) {
            logger.warn(String.format("Unable to create image from data (%s)", e.getMessage()));
        }
        return null;
    }

    /**
     * Get list of maps available for this planet.
     *
     * @param planetId  Plant to get list of maps for.
     * @return          List of map names.
     */
    public List<String> getPlanetMaps(int planetId) {
        Query query = session.createNativeQuery("SELECT name FROM planet_maps WHERE planet_id=:id");
        query.setParameter("id", planetId);
        return query.getResultList();
    }

    private static final String GENERATOR_PREFIX = "uk.org.glendale.worldgen.astro.planets.generators.";

    private static final Class getGeneratorClass(String name, PlanetType type) throws UnsupportedException {

        try {
            return Class.forName(GENERATOR_PREFIX + type.getGroup().name().toLowerCase() + "." + type.name());
        } catch (ClassNotFoundException e) {
            logger.warn(String.format("Unable to find generator class for planet type [%s]", type.name()));
        }

        // Failed to get a generator for the given type, so look to see if there is a parent
        // class defined for this type.
        PlanetClass classification = type.getClassification();
        try {
            return Class.forName(GENERATOR_PREFIX + type.getGroup().name().toLowerCase() + "." + classification.name());
        } catch (ClassNotFoundException e) {
            logger.warn(String.format("Unable to find generator class for planet type [%s.%s]",
                    classification.name(), type.name()));
        }

        // Failed to get a generator for either the specific type or classification, so look
        // for a top level group generator for this type.
        PlanetGroup group = classification.getGroup();
        try {
            return Class.forName(GENERATOR_PREFIX + group.name());
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Unable to find generator class for planet type [%s.%s.%s]",
                    group.name(), classification.name(), type.name()));
        }
        throw new UnsupportedException(String.format("Planet [%s] has unsupported type [%s]", name, type.name()));
    }

    public List<Planet> createPlanet(StarSystem system, Star star, String name, PlanetType type, long distance) throws UnsupportedException {
        return createPlanet(system, star, name, type, distance, null);
    }

    /**
     * Creates a new planet in the given star system. Calls one of the specific generators for the given
     * type of planet. May return more than one planet if the planet has moons. The primary planet will
     * be the first in the list, followed by the moons from inner to outer.
     *
     * @param system    System to create planet in.
     * @return          List containing the planet and any moons.
     */
    public List<Planet> createPlanet(StarSystem system, Star star, String name, PlanetType type, long distance, Planet previous) throws UnsupportedException {
        Class genClass = getGeneratorClass(name, type);
        PlanetGenerator generator;
        List<Planet>    planets = new ArrayList<Planet>();

        logger.info(String.format("Creating planet [%s] of type [%s]", name, type.name()));

        try {
            Constructor c = genClass.getConstructor(WorldGen.class, StarSystem.class, Star.class, Planet.class, Long.TYPE);
            generator = (PlanetGenerator) c.newInstance(worldgen, system, star, previous, distance);

            Planet planet;
            try {
                planet = generator.getPlanet(name);
            } catch (UnsupportedException e) {
                planet = generator.getPlanet(name, type);
            }
            generator.generateDescription(planet);

            session.persist(planet);
            session.flush();
            planets.add(planet);

            if (type.getGroup() != PlanetGroup.Belt) {
                Map<String,SimpleImage> maps = generator.getPlanetMaps(planet);

                for (String mapType : maps.keySet()) {
                    setPlanetMap(planet.getId(), mapType, maps.get(mapType));
                }
            }
            List<Planet> moons = generator.getMoons(planet, this);
            if (moons.size() > 0) {
                logger.info(String.format("Planet [%s] has %d moons", name, moons.size()));
                for (Planet moon : moons) {
                    session.persist(moon);
                }
                planets.addAll(moons);
            }

            return planets;
        } catch (NoSuchMethodException e) {
            logger.error(String.format("No such method for [%s]", type.name()), e);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Illegal access for [%s]", type.name()), e);
        } catch (InstantiationException e) {
            logger.error(String.format("Cannot instantiate for [%s]", type.name()), e);
        } catch (InvocationTargetException e) {
            logger.error(String.format("Cannot invoke target for [%s]", type.name()), e);
        } catch (IOException e) {
            logger.error(String.format("Error generating/storing image map [%s]", type.name()), e);
        }

        return null;
    }

    public Planet createMoon(StarSystem system, Star star, String name, PlanetType type,
                             long distance, Planet parent, PlanetFeature... features) {
        Class genClass = getGeneratorClass(name, type);
        PlanetGenerator generator;

        logger.info(String.format("Creating moon [%s] of type [%s] at [%d]km", name, type.name(), distance));

        try {
            Constructor c = genClass.getConstructor(WorldGen.class, StarSystem.class, Star.class, Planet.class, Long.TYPE);
            generator = (PlanetGenerator) c.newInstance(worldgen, system, star, null, distance);

            if (features != null && features.length > 0) {
                for (PlanetFeature f : features) {
                    generator.addFeature(f);
                }
            }
            Planet moon;
            try {
                moon = generator.getPlanet(name);
            } catch (UnsupportedException e) {
                moon = generator.getPlanet(name, type);
            }
            moon.setMoonOf(parent.getId());
            generator.generateDescription(moon);

            session.persist(moon);
            session.flush();

            if (type.getGroup() != PlanetGroup.Belt) {
                Map<String,SimpleImage> maps = generator.getPlanetMaps(moon);

                for (String mapType : maps.keySet()) {
                    setPlanetMap(moon.getId(), mapType, maps.get(mapType));
                }
            }

            return moon;
        } catch (NoSuchMethodException e) {
            logger.error(String.format("No such method for [%s]", type.name()), e);
        } catch (IllegalAccessException e) {
            logger.error(String.format("Illegal access for [%s]", type.name()), e);
        } catch (InstantiationException e) {
            logger.error(String.format("Cannot instantiate for [%s]", type.name()), e);
        } catch (InvocationTargetException e) {
            logger.error(String.format("Cannot invoke target for [%s]", type.name()), e);
        } catch (IOException e) {
            logger.error(String.format("Error generating/storing image map [%s]", type.name()), e);
        }

        return null;
    }
}
