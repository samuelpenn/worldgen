/**
 * Planet.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets;

import org.hibernate.annotations.Formula;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.commodities.Commodity;
import uk.org.glendale.worldgen.astro.commodities.Resource;
import uk.org.glendale.worldgen.astro.planets.codes.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A planet represents any notable celestial object that is not a star. This includes actual planets, moons,
 * asteroid belts, planetary rings and asteroids. They have a PlanetType, which determines the type of planet
 * that the object represents.
 *
 * Where a planet refers to other objects (stars, star systems and planets), it does so by id, not directly
 * to an object of that type. This is done to simplify JSON data structures.
 */
@Entity
@Table(name="planets")
public class Planet {
    @Id @GeneratedValue @Column
    private int id;

    @Column
    private String name;

    @Column (name = "system_id")
    private int systemId;

    @Column (name = "parent_id")
    private int parentId;

    @Column (name = "moon_of")
    private int moonOf;

    @Column
    private long distance;

    @Column
    private int radius;

    @Column (name = "day")
    private long dayLength;

    @Column (name = "period")
    private long period;

    @Column @Enumerated (EnumType.STRING)
    private PlanetType type;

    @Column
    private int temperature;

    @Column @Enumerated (EnumType.STRING)
    private Atmosphere atmosphere;

    @Column
    private int pressure;

    @Column
    private int density;

    @Column @Enumerated (EnumType.STRING)
    private MagneticField field;

    @Column (name = "hydro")
    private int hydrographics;

    @Column @Enumerated (EnumType.STRING)
    private Life life;

    @Column @Enumerated (EnumType.STRING)
    private StarPort port;

    @Column (name = "population")
    private long population;

    @Column @Enumerated (EnumType.STRING)
    private Government government;

    @Column (name = "tech")
    private int techLevel;

    @Column (name = "law")
    private int law;

    @Column
    private String description;

    @Transient
    private Set<PlanetFeature> features = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "resources", joinColumns= @JoinColumn(name="planet_id"))
    private List<Resource> resources;

    public Planet() {

    }

    /**
     * Gets the unique id of this planet.
     *
     * @return  Planet Id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique id of this planet. Cannot be changed after it has been set.
     *
     * @param id    Planet id.
     */
    void setId(int id) {
        if (this.id != 0) {
            throw new IllegalStateException("Can't change the id of a planet after setting it.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Planet name must be non-empty.");
        }
        this.name = name.trim();
    }

    /**
     * Gets the id of the star system this planet belongs to.
     *
     * @return  Id of star system.
     */
    public int getSystemId() {
        return systemId;
    }

    /**
     * Sets the id of the star system this planet belongs to.
     *
     * @param systemId  Star System id.
     */
    void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    /**
     * Gets the id of the parent star for this planet. If this is a rogue planet
     * with no stellar parent, then the id will be zero. If this planet is a moon,
     * then the parent is still the star.
     *
     * @return  Id of planet's parent star.
     */
    public int getParentId() {
        return parentId;
    }

    void setParentId(int parentId) {
        this.parentId = parentId;
    }


    /**
     * Is this planet a belt or ring of some kind. If so, it won't have the usual planetary map.
     *
     * @return  True iff this is a stellar or planetary ring or belt.
     */
    public boolean isBelt() {
        return type.getGroup() == PlanetGroup.Belt;
    }

    /**
     * Returns true iff this planet is a moon of another planet. A planet is a moon if MoonOf is
     * non-zero.
     *
     * @return  True iff this is a moon.
     */
    public boolean isMoon() {
        return moonOf > 0;
    }

    public void setMoonOf(int planetId) {
        this.moonOf = planetId;
    }

    /**
     * If this is a moon, then gets the id of the planet around which this moon orbits.
     * If this is not a moon, then returns zero.
     *
     * @return      Id of the planet around which this planet orbits, if it is a moon.
     */
    public int getMoonOf() {
        return moonOf;
    }

    /**
     * Distance is always in kilometres. This is the distance from the planet's primary. If the
     * planet is a moon, this is the distance from the parent parent. If it's a planet, then it's
     * distance from the star.
     *
     * If a moon is part of a Belt, then the distance may be negative.
     *
     * @return  Distance of planet from primary.
     */
    public long getDistance() {
        return distance;
    }

    public void setDistance(long km) {
        if (moonOf > 0) {
            this.distance = km;
        } else {
            this.distance = Math.max(km, 0);
        }
    }

    public PlanetType getType() {
        return type;
    }

    public void setType(PlanetType type) {
        if (type == null) {
            throw new IllegalArgumentException("Planet type must be non-null");
        }
        this.type = type;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int km) {
        this.radius = Math.max(km, 0);
    }

    public int getBeltWidth() {
        return radius * 2;
    }

    /**
     * Gets the density of this planet. A density of 1000 is equal to that of water.
     * For belts, the density is handled differently.
     *
     * @return  Density in kg per cubic metre.
     */
    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = Math.max(0, density);
    }

    /**
     * Gets the length of the day of this planet in seconds.
     *
     * @return      Day length in seconds.
     */
    public long getDayLength() {
        return dayLength;
    }

    public void setDayLength(long seconds) {
        this.dayLength = Math.max(seconds, 0);
    }

    /**
     * Gets the length of the year of this planet in seconds.
     *
     * @return      Year length in seconds.
     */
    public long getPeriod() { return period; }

    public void setPeriod(long seconds) {
        this.period = Math.max(seconds, 0);
    }

    /**
     * Gets the surface temperature of this planet in Kelvin. This is the average surface
     * temperature - there will be wide fluctuations based on latitude and time of year.
     * A standard temperature is considered to be around 285K - 290K.
     *
     * @return  Surface temperature in Kelvin.
     */
    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int k) {
        this.temperature = Math.max(k, 3);
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    /**
     * Gets the atmospheric pressure of this planet, in Pascals. 100,000 Pa is considered
     * to be standard pressure.
     *
     * @return      Pressure in Pascals.
     */
    public int getPressure() {
        return pressure;
    }

    /**
     * Sets the atmsopheric pressure of the planet, in Pascals. 100,000 Pa is considered
     * to be standard pressure.
     *
     * @param pascals   Pressure in pascals.
     */
    public void setPressure(int pascals) {
        this.pressure = Math.max(pascals, 0);
    }

    public void setPressure(Pressure pressure) {
        if (pressure != null) {
            this.pressure = pressure.getPascals();
        } else {
            this.pressure = 0;
        }
    }

    /**
     * Gets the proportion of the planet's surface that is covered by liquid water, as a
     * percentage from 0 - 100.
     *
     * @return      Surface water percentage, 0-100.
     */
    public int getHydrographics() {
        return hydrographics;
    }

    /**
     * Sets the proportion of the planet's surface that is covered by liquid water, as a
     * percentage from 0 - 100. Values outside this range will be capped to 0 or 100.
     *
     * @param hydrographics Water cover as a percentage.
     */
    public void setHydrographics(int hydrographics) {
        this.hydrographics = Math.max(Math.min(hydrographics, 100), 0);
    }

    /**
     * Gets the magnetic field strength for this planet.
     *
     * @return  Strength of the magnetic field.
     */
    public MagneticField getMagneticField() {
        if (field != null) {
            return field;
        } else {
            return MagneticField.None;
        }
    }

    /**
     * Sets the magnetic field strength of this planet.
     *
     * @param field     Strength of the magnetic field.
     */
    public void setMagneticField(MagneticField field) {
        if (field != null) {
            this.field = field;
        } else {
            throw new IllegalArgumentException("MagneticField cannot be set to a null value");
        }
    }

    public Life getLife() {
        return life;
    }

    public void setLife(Life life) {
        this.life = life;
    }

    public StarPort getStarPort() {
        return port;
    }

    public void setStarPort(StarPort port) {
        this.port = port;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = Math.max(population, 0);
    }

    public Government getGovernment() {
        return government;
    }

    public void setGovernment(Government government) {
        this.government = government;
    }

    public int getTechLevel() {
        return techLevel;
    }

    public void setTechLevel(int techLevel) {
        this.techLevel = Math.max(techLevel, 0);
    }

    public int getLawLevel() {
        return law;
    }

    public void setLawLevel(int law) {
        this.law = Math.max(Math.min(law, 6), 0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Add the specified feature to the planet. Features are special aspects of the world
     * which may appear on the map, and can be described in the planet's text description.
     * Features themselves aren't stored against the planet object, they are only used to
     * modify maps and descriptions at generation time.
     *
     * @param feature   Feature to be set.
     */
    public void addFeature(PlanetFeature feature) {
        features.add(feature);
    }

    /**
     * Does the planet have the requested feature? A feature is an enum which implements
     * the PlanetFeature interface.
     *
     * @param feature   Feature to check for.
     * @return      Returns true iff the planet has this feature.
     */
    public boolean hasFeature(PlanetFeature feature) {
        return features.contains(feature);
    }

    public List<PlanetFeature> getFeatures() {
        return new ArrayList<PlanetFeature>(features);
    }


    /**
     * Gets the diameter of the planet. This is simply the radius * 2. This is sometimes used
     * by the TextGenerator which can't do the arithmetic itself.
     *
     * @return  Diameter of the planet in km, or width of a Belt.
     */
    public int getDiameter() {
        return radius * 2;
    }

    /**
     * Gets the list of resources available on this world.
     *
     * @return  List of resources. May be empty, never null.
     */
    public List<Resource> getResources() {
        if (resources == null) {
            resources = new ArrayList<Resource>();
        }
        return resources;
    }

    /**
     * Adds a resource with the specified density to the list of resources this planet has.
     * If the resource already exists, then its density is overwritten. If the density is below 1,
     * then no resource is added, and any existing resource of that type is removed.
     *
     * The provided density is modified by the frequency of the commodity type, so will probably
     * end up being lower than the given density (and may end up not being added if the density
     * drops below 1).
     *
     * @param commodity     Commodity this resource is for.
     * @param density       Density, generally 1 - 1000.
     */
    public void addResource(Commodity commodity, int density) {
        int f = commodity.getFrequency().getBaseFrequency();
        density = (density * f) / 100;

        if (resources == null) {
            resources = new ArrayList<Resource>();
        }
        for (Resource r : resources) {
            if (r.getCommodity().equals(commodity)) {
                if (density > 0) {
                    r.setDensity(density);
                } else {
                    resources.remove(r);
                }
                return;
            }
        }
        if (density > 0) {
            resources.add(new Resource(commodity, density));
        }
    }
}
