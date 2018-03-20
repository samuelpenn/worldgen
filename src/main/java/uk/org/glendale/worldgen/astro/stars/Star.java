/**
 * Star.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.codes.Temperature;
import uk.org.glendale.worldgen.astro.systems.Physics;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import javax.persistence.*;

/**
 * Represents a Star in a solar system. A star system will consist of zero or more stars, plus any
 * planets which orbit them. In practice, the limit of Stars in a star system is probably about three,
 * due to the complexity of working out orbital mechanics.
 *
 * A Star can be a typical star, a stellar remnant (such as black hole, neutron star or white dwarf)
 * or a brown dwarf. Brown dwarfs are considered to be stars since they tend to be the primary object
 * in the star system.
 *
 * Nomenclature: If there is a single star in the system then it is named after the system. If there
 * are multiple stars, they have a suffix of Alpha (for the largest/brightest), Beta, Gamma etc.
 * The closest companion of the primary is always Beta.
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

    @Column(name = "mass")
    private double mass;

    @Column(name = "radius")
    private int radius;

    @Column(name = "period")
    private long period;

    public static final long SOL_RADIUS = 696000000; // Metres.
    public static final int  SOL_TEMPERATURE = 5830; // Kelvin.

    /**
     * Empty constructor.
     */
    public Star() {
    }

    public Star(Star copy) {
        this.name = copy.name;
        this.system = copy.system;
        this.parentId = copy.parentId;
        this.distance = copy.distance;
        this.luminosity = copy.luminosity;
        this.type = copy.type;
        this.mass = copy.mass;
        this.radius = copy.radius;
        this.period = copy.period;
    }

    /**
     * Define a new Star with a full set of details.
     */
    public Star(String name, StarSystem system, int parentId, int distance, Luminosity luminosity, SpectralType type) {
        this.id = 0;
        this.name = name;
        this.system = system;
        this.parentId = parentId;
        this.distance = distance;
        this.luminosity = luminosity;
        this.type = type;
        setStandardMass();
    }

    /**
     * Gets the unique id of the star. All star ids are unique across the entire universe.
     *
     * @return Unique id of this star.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the star system to which this star belongs. Once persisted, a Star will always have a
     * StarSystem defined.
     *
     * @return  Star system object, not null.
     */
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
     * no parent, this is zero. If it is non-zero, then this star is in orbit
     * around another star. It is possible that two stars orbit each other around
     * a common centre of gravity, in which case they will each have a parent of
     * the other.
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
     * Gets the orbital period of this star in seconds. If the star is at the centre
     * of the system, then it's orbital period will be zero.
     *
     * @return  Period in seconds.
     */
    public long getPeriod() {
        return period;
    }

    public void setPeriod(long seconds) {
        this.period = seconds;
    }

    /**
     * Gets the classification of this star, from class VI dwarfs up to class Ia
     * super giants. Most stars are class V.
     *
     * @return Star class, from VII up to Ia.
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
     * Gets the multiplier for different temperature bands around this star
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
    private double getSolarConstant() {
        double solSurface = SpectralType.G2.getSurfaceTemperature();
        double constant = (1.0 * type.getSurfaceTemperature() / solSurface);

        constant *= Math.pow(luminosity.getRadius(), 2);

        return Math.pow(constant, 0.5);
    }

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
        double d = (1.0 * distance) / 150;

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

        if (luminosity == Luminosity.B || luminosity == Luminosity.N || luminosity == Luminosity.VII) {
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
    private static final long STANDARD_YEAR = 31557600L;
    private static final double  AU = 150.0;

    /**
     * Given a distance from the star, get the orbital period of that orbit in seconds.
     *
     * @param distance  Distance in millions of kilometres.
     * @return          Orbital period in seconds.
     */
    public long getPeriod(int distance) {
        long period = (long) (STANDARD_YEAR * Math.pow((1.0 * distance) / AU, 3.0/2.0));

        return (long) (period / Math.sqrt(getMass()));
    }

    /**
     * Sets the mass of the star in solar masses, based on the Luminosity Class and Spectral Type
     * of this star.
     */
    public void setStandardMass() {
        if (type != null && luminosity != null) {
            this.mass = type.getMass() * luminosity.getMass();
        } else if (type != null) {
            this.mass = type.getMass();
        } else if (luminosity != null) {
            this.mass = luminosity.getMass();
        } else {
            this.mass = 0;
        }
    }

    /**
     * Sets the mass of the star in solar masses, to a specific value.
     *
     * @param solarMasses  Mass in solar masses.
     */
    public void setMass(final double solarMasses) {
        if (solarMasses < 0) {
            throw new IllegalArgumentException("Stellar mass must be positive.");
        }
        this.mass = solarMasses;
    }

    /**
     * Gets the mass of this star in solar masses.
     *
     * @return      Star mass in solar masses.
     */
    public double getMass() {
        return type.getMass() * luminosity.getMass();
    }

    public void setStandardRadius() {
        this.radius = (int) (Physics.SOL_RADIUS * luminosity.getRadius() * type.getRadius());
    }

    /**
     * Set the radius of this star in kilometres.
     *
     * @param km    Radius in kilometres.
     */
    public void setRadius(int km) {
        this.radius = km;
    }

    /**
     * Gets the radius of this star in kilometres.
     *
     * @return  Radius in kilometres.
     */
    public int getRadius() {
        return radius;
    }



    public static void main(String[] args) {

        Star s = new Star();

        System.out.println(String.format("%-7s %7s %7s %7s %7s %7s %7s %7s", "Class", "M5", "K5", "G5", "F5", "A5", "B5", "O5"));

        for (Luminosity l : new Luminosity[] { Luminosity.VII, Luminosity.VI, Luminosity.V, Luminosity.IV, Luminosity.III, Luminosity.II, Luminosity.Ib, Luminosity.Ia, Luminosity.O }) {
            s.setLuminosity(l);

            System.out.print(String.format("%-8s ", l.name()));

            for (SpectralType hr : new SpectralType[] { SpectralType.M5, SpectralType.K5, SpectralType.G5, SpectralType. F5, SpectralType.A5, SpectralType.B5, SpectralType.O5 }) {
                s.setSpectralType(hr);

                s.setStandardMass();
                s.setStandardRadius();

                System.out.print(String.format("% 7.2f ", s.getMass() ));
            }
            System.out.println("");
        }


    }
}
