/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems;

import uk.org.glendale.worldgen.astro.stars.SpectralType;
import uk.org.glendale.worldgen.astro.stars.Star;

/**
 * Static helper class which provides common physics methods and constant values.
 */
public class Physics {
    // Radius of Sol, in kilometres.
    public static final long SOL_RADIUS = 695700;

    // Surface temperature of Sol, in Kelvin.
    public static final int  SOL_TEMPERATURE = 5830;

    // Mass of Sol, in KG.
    public static final double SOL_MASS = 2e30;

    // Length of a standard year, in seconds.
    public static final long STANDARD_YEAR = 31557600L;

    // Distance of one Astronomical Unit, in millions of kilometres.
    public static final double  AU = 150.0;

    public static final double G = 6.67408e-11;

    // Can't be instantiated.
    private Physics() {

    }

    /**
     * Gets the energy output of a star, relative to Sol. One solar luminosity is equal
     * to 3.828 x 10^26 W.
     *
     * See https://en.wikipedia.org/wiki/Solar_luminosity
     *
     * @param star
     * @return
     */
    public static double getSolarLuminosity(Star star) {
        return 1.0;
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
    public static double getSolarConstant(Star star) {
        // Start with the basic surface temperature.
        double t = (1.0 * star.getSpectralType().getSurfaceTemperature()) / SpectralType.G2.getSurfaceTemperature();
        // Radius of the star.
        double r = 1.0 * star.getLuminosity().getRadius();

        // Get power output relative to Sol.
        double l = Math.pow(r, 2.0) * Math.pow(t, 4);

        return l;
    }

    public static int getOrbitTemperature(Star star, int distance) {
        // Start with the basic surface temperature.
        double t = (1.0 * star.getSpectralType().getSurfaceTemperature()) / SpectralType.G2.getSurfaceTemperature();
        // Radius of the star.
        double r = 1.0 * star.getLuminosity().getRadius();

        // Distance in AU.
        double d = (1.0 * distance) / 150;

        // Get power output relative to Sol.
        double l = Math.pow(r, 2.0) * Math.pow(t, 4);
        l = l / Math.pow(d, 2.0);

        return (int) (280 * Math.pow(l, 0.25));
    }

    /**
     * Gets the orbital period of a pair of bodies in seconds, given their total mass and distance
     * from each other. For planets orbiting a star, the total mass can generally be assumed equal
     * to the solar mass, and the distance can be assumed to be the distance of the planet from
     * the centre of the star.
     *
     * @param kg    Total mass of the two bodies in kg.
     * @param m     Distance between the two bodies in metres.
     * @return      Orbital period in seconds.
     */
    public static long getOrbitalPeriod(double kg, long m) {
        double rhs = Math.pow(m, 3);
        System.out.println(rhs);
        rhs = rhs / ( G * kg);
        System.out.println(rhs);

        return (long) (2 * Math.PI * Math.pow(rhs, 0.5));
    }

}
