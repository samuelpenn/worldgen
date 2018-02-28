/**
 * Star.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.codes.Temperature;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import javax.persistence.*;

/**
 * Represents a Star in a solar system. A system will consist of one or more
 * stars.
 *
 * @author Samuel Penn
 */
@Entity
@Table(name = "stars")
public class Star {
    // Unique identifier used as primary key.
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    // Persisted fields.
    @Column(name = "name")
    private String name;

    // Astronomical data
    @ManyToOne
    @JoinColumn(name = "system_id", referencedColumnName = "id")
    private StarSystem system;

    @Column(name = "parent_id")
    private int parentId;
    @Column(name = "distance")
    private int distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "luminosity")
    private Luminosity luminosity;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SpectralType type;

    public static final long SOL_RADIUS = 696000000; // Metres.
    public static final int  SOL_TEMPERATURE = 5830; // Kelvin.

    public Star() {

    }

    /**
     * Gets the unique id of the star. All star ids are unique across the entire
     * universe.
     *
     * @return Unique id of this star.
     */
    public int getId() {
        return id;
    }

    public StarSystem getSystem() {
        return system;
    }

    public void setSystem(StarSystem system) {
        this.system = system;
    }

    /**
     * Gets the name of the star. Names should be unique within a star system.
     * If there are multiple stars, normally the first is named Alpha, the
     * second Beta etc.
     *
     * @return Name of the star.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("Name is not valid");
        }
        this.name = name.trim();
    }

    /**
     * Gets the id of the parent around which this star orbits. If the star has
     * no parent, this is zero.
     *
     * @return Gets the parent id of this star.
     */
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the orbit distance of this star, in millions of kilometres. If the
     * star has no parent, this will normally be zero. Support for multiple
     * stars orbiting a common centre of gravity is not yet supported.
     *
     * @return Distance from parent star, in Mkm.
     */
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.distance = distance;
    }

    /**
     * Gets the classification of this star, from class VI dwarfs up to class Ia
     * super giants. Most stars are class V.
     *
     * @return Star class, from VI up to Ia.
     */
    public Luminosity getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(Luminosity luminosity) {
        this.luminosity = luminosity;
    }

    /**
     * Gets the spectral type of the star, using the Hertzsprung Russell
     * diagram. This is a two character code, e.g. our sun is G2.
     *
     * @return Spectral type of star.
     */
    public SpectralType getSpectralType() {
        return type;
    }

    public void setSpectralType(SpectralType type) {
        this.type = type;
    }


    /**
     * Gets the multipler for different temperature bands around this star
     * based on that of Sol. A star four times as luminous as Sol would have
     * range bands which are twice as far.
     *
     * Luminosity is considered to increase by the square of the radius and
     * linearly with surface temperature. See:
     *
     * https://en.wikipedia.org/wiki/Circumstellar_habitable_zone
     *
     * @return  Constant to multiply temperature range bands by.
     */
    public double getSolarConstant() {
        double solSurface = SpectralType.G2.getSurfaceTemperature();
        double constant = (1.0 * type.getSurfaceTemperature() / solSurface);

        constant *= Math.pow(luminosity.getRadius(), 2);

        return Math.pow(constant, 0.5);
    }

    // Minimum distance at which planets can form, in Millions KM.
    private static double MINIMUM_MKM = 25;
    // Inner edge of Habitable Zone, in Millions KM.
    private static double INNER_HZ_MKM = 75;
    // Outer edge of Habitable Zone, in Millions KM.
    private static double OUTER_HZ_MKM = 450;


    /**
     * Gets the temperature at the given distance from the star. This will not be the
     * temperature of a planet at that distance, due to factors such as albedo, rotation
     * and atmospheric effects.
     *
     * @param distance  Distance from star in millions of km.
     * @return          Temperature, in kelvin.
     */
    public int getOrbitTemperature(int distance) {
        // Start with the basic surface temperature.
        double t = (1.0 * getSpectralType().getSurfaceTemperature()) / SpectralType.G2.getSurfaceTemperature();
        // Radius of the star.
        double r = 1.0 * getLuminosity().getRadius();

        // Distance in AU.
        double d = (1.0 * distance) /150;

        // Get power output relative to Sol.
        double l = Math.pow(r, 2.0) * Math.pow(t, 4);
        l = l / Math.pow(d, 2.0);

        return (int) (280 * Math.pow(l, 0.25));
    }


    /**
     * Gets the minimum distance at which a planet of some kind can exist around this
     * star. This is mostly based on the size and temperature of the star, but remnants
     * will have a larger distance on the assumption that anything closer would have
     * been destroyed in the star's death throes.
     *
     * @return  Distance, in millions of kilometres.
     */
    public int getMinimumDistance() {
        int distance;

        if (luminosity == Luminosity.B || luminosity == Luminosity.N || luminosity == Luminosity.D) {
            distance = 250;
        } else {
            distance = (int)(25 * getSolarConstant());
        }

        return distance;
    }

    public int getInnerWarmDistance() {
        return (int)(100 * getSolarConstant());
    }

    public int getOuterWarmDistance() {
        return (int)(250 * getSolarConstant());
    }

    public int getSnowLineDistance() {
        return (int)(400 * getSolarConstant());
    }

    public int getHotDistance() {
        int distance = 100;
        distance *= Math.sqrt(luminosity.getSize());
        distance *= (1.0 * type.getSurfaceTemperature()) / SpectralType.G2.getSurfaceTemperature();

        return distance;
    }

    // Length of a standard year, in seconds.
    private static long STANDARD_YEAR = 31557600L;
    private static double  AU = 150.0;

    /**
     * Given a distance from the star, get the orbital period of that orbit in seconds.
     *
     * @param distance  Distance in millions of kilometres.
     * @return          Orbital period in seconds.
     */
    public long getPeriod(int distance) {
        long period = (long) (STANDARD_YEAR * Math.pow((1.0 * distance) / AU, 3.0/2.0));


        period = (long) (period / Math.sqrt(type.getMass()));
        return period;
    }

    public static void main(String[] args) {

        Star s = new Star();
        s.setSpectralType(SpectralType.G2);
        s.setLuminosity(Luminosity.V);
        for (int d=25; d < 1000; d += 25) {
            System.out.println(String.format(" %04d Mkm  - %d", d, s.getOrbitTemperature(d)));
        }


    }
}
