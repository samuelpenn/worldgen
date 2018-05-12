/**
 * PlanetGenerator.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.Main;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.commodities.Commodity;
import uk.org.glendale.worldgen.astro.commodities.CommodityFactory;
import uk.org.glendale.worldgen.astro.commodities.CommodityName;
import uk.org.glendale.worldgen.astro.commodities.NoSuchCommodityException;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.*;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemGenerator;
import uk.org.glendale.worldgen.civ.CivilisationFeature;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;
import uk.org.glendale.worldgen.exceptions.WorldGenException;
import uk.org.glendale.worldgen.web.Server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Abstract class which defines planet generators. There is a planet generator class
 * for each type of planet, as defined by the PlanetType enum.
 */
public abstract class PlanetGenerator {
    protected static final Logger logger = LoggerFactory.getLogger(PlanetGenerator.class);
    protected final StarSystem system;
    protected final Star star;
    protected final Planet previousPlanet;
    protected long  distance;
    protected final CommodityFactory commodityFactory;
    protected final WorldGen worldGen;

    public PlanetGenerator(WorldGen worldGen, StarSystem system, Star primary, Planet previous, long distance) {
        this.worldGen = worldGen;
        this.system = system;
        this.star = primary;
        this.distance = distance;
        this.previousPlanet = previous;

        this.commodityFactory = worldGen.getCommodityFactory();
    }

    public abstract Planet getPlanet(String name, PlanetType type);

    public abstract Planet getPlanet(String name);

    protected void generateDescription(Planet planet) {
        String text = "Harmless.";
        planet.setDescription(text);
    }

    protected Planet definePlanet(Planet planet) {
        planet.setSystemId(system.getId());
        if (star != null) {
            planet.setParentId(star.getId());
        }
        if (planet.getDistance() == 0) {
            planet.setDistance(distance);
        }
        if (planet.getRadius() == 0) {
            planet.setRadius(500 + Die.d100() * 10);
        }
        planet.setTemperature(star.getOrbitTemperature(distance));
        planet.setAtmosphere(Atmosphere.Vacuum);
        planet.setPressure(0);
        planet.setMagneticField(MagneticField.None);
        planet.setStarPort(StarPort.X);
        planet.setPopulation(0);
        planet.setGovernment(Government.None);
        planet.setLife(Life.None);
        planet.setDayLength(86400 * (36 + Die.d12(4)));

        generateDescription(planet);

        return planet;
    }

    protected Planet definePlanet(String name, PlanetType type) {
        Planet planet = new Planet();
        planet.setName(name);
        planet.setType(type);

        return definePlanet(planet);
    }

    /**
     * Gets the distance of the previous planet in the system. This is used for working
     * out whether we need to migrate this planet outwards in case of collision. If there
     * is no previous planet, the distance returned is zero.
     *
     * @return  Distance from star of previous planet, in millions of km.
     */
    protected long getPreviousDistance() {
        if (previousPlanet == null) {
            return 0;
        } else {
            return previousPlanet.getDistance();
        }
    }

    private static Class getMapClass(PlanetType type) throws UnsupportedException {
        String mapRoot = "uk.org.glendale.worldgen.astro.planets.maps";
        String typeName = String.format("%s.%s.%sMapper", mapRoot, type.getGroup().name().toLowerCase(), type.name());

        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Unable to find map class for planet type [%s]", type.name()));
        }
        throw new UnsupportedException(String.format("Planet type [%s] does not support maps", type.name()));
    }

    protected SimpleImage getPlanetMap(Planet planet) throws UnsupportedException {
        try {
            Constructor c = getMapClass(planet.getType()).getConstructor(Planet.class);
            PlanetMapper map = (PlanetMapper) c.newInstance(planet);

            map.generate();
            return map.draw(Server.getConfiguration().getPlanetMapResolution());
        } catch (UnsupportedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedException("Unable to create world map (" + e.getMessage() + ")", e);
        }
    }

    protected Map<String,SimpleImage> getPlanetMaps(Planet planet) throws UnsupportedException {
        try {
            Map<String,SimpleImage> maps = new HashMap<>();

            Constructor c = getMapClass(planet.getType()).getConstructor(Planet.class);
            PlanetMapper mapper = (PlanetMapper) c.newInstance(planet);

            if (mapper.hasMainMap()) {
                mapper.generate();
                maps.put(PlanetMap.MAIN, mapper.draw(Server.getConfiguration().getPlanetMapResolution()));
            }
            if (mapper.hasHeightMap()) {
                maps.put(PlanetMap.HEIGHT, mapper.drawHeightMap(Server.getConfiguration().getPlanetMapResolution()));
            }
            if (mapper.hasOrbitMap()) {

            }

            return maps;
        } catch (UnsupportedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedException("Unable to create world map (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Adds a resource to the planet. A resource is a density of a commodity. The frequency
     * of the commodity affects the final density of the resource.
     *
     * @param planet    Planet to add resource to.
     * @param name      Name of the commodity this resource is for.
     * @param density   Base density for this resource.
     */
    private final void addResource(Planet planet, CommodityName name, int density) {
        try {
            Commodity commodity = commodityFactory.getCommodity(name.getName());
            planet.addResource(commodity, density);
        } catch (NoSuchCommodityException e) {
            // This shouldn't happen, since use of the enum should be well defined.
            logger.error(String.format("Well defined commodity [%s] cannot be found", name.getName()));
        }
    }

    public final void addPrimaryResource(Planet planet, CommodityName commodity) {
        addResource(planet, commodity, 800 + Die.d100(4));
    }

    public final void addSecondaryResource(Planet planet, CommodityName commodity) {
        addResource(planet, commodity, 400 + Die.d100(2));
    }

    public final void addTertiaryResource(Planet planet, CommodityName commodity) {
        addResource(planet, commodity, 200 + Die.d100());
    }

    public final void addTraceResource(Planet planet, CommodityName commodity) {
        addResource(planet, commodity, 100 + Die.d20(2));
    }

    public List<Planet> getMoons(Planet primary) {
        return new ArrayList<Planet>();
    }

    /**
     * Try to colonise a world. Determines whether the world should be colonised, and
     * creates a new civilisation on it if so.
     *
     * @param planet    Planet to try to colonise.
     * @param features  List of features of any civilisation.
     * @return          Population (if any) of the new world.
     */
    public long colonise(Planet planet, CivilisationFeature... features) {
        // By default, a planet is unlikely to be colonised.

        return 0;
    }

}
