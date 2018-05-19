/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro;

import uk.org.glendale.worldgen.astro.stars.Luminosity;
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

    public static final long STANDARD_DAY = 86400L;

    // Distance of one Astronomical Unit, in kilometres.
    public static final long  AU = 150_000_000;

    // Distance unit of millions of kilometres.
    public static final long MKM = 1_000_000;

    public static final double G = 6.67408e-11;

    public static final long SNOW_DISTANCE = 400 * MKM;
    public static final long OUTER_DISTANCE = 250 * MKM;
    public static final long INNER_DISTANCE = 100 * MKM;
    public static final long MINIMUM_DISTANCE = 25 * MKM;

    // Can't be instantiated.
    private Physics() {

    }

    /**
     * Rounds a number to the given number of significant digits.
     *
     * @param number      Number to be rounded.
     * @param digits      Number of digits to round it to. Must be at least one.
     *
     * @return            The number after it has been rounded.
     */
    public static final long round(long number, int digits) {
        int sign = (number < 0)?-1:1;
        number = Math.abs(number);

        if (digits < 1) {
            throw new IllegalArgumentException("Number of significant digits must be at least one.");
        }
        if (number <= Math.pow(10, digits)) {
            // Nothing to do.
        } else {
            int  log = (int) Math.log10(number);
            digits = 1 + log - digits;

            number += Math.pow(10, digits) / 2;
            number -= number % (long)Math.pow(10, digits);
        }

        return sign * number;
    }

    public static final long round(long number) {
        return round(number, 4);
    }

    public static final long round(double number) {
        return round((long) number, 4);
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
        double r = 1.0 * star.getRadius() / SOL_RADIUS;

        // Get power output relative to Sol.
        return Math.pow(r, 2.0) * Math.pow(t, 4);
    }

    public static int getOrbitTemperature(double solarConstant, long distance) {
        double l = solarConstant / Math.pow(1.0 * distance / AU, 2.0);

        return (int) (280 * Math.pow(l, 0.25));
    }

    public static int getOrbitTemperature(Star star, long distance) {
        return getOrbitTemperature(getSolarConstant(star), distance);
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
        rhs = rhs / ( G * kg);

        return (long) (2 * Math.PI * Math.pow(rhs, 0.5));
    }

    public static void main(String[] args) {
        for (SpectralType type : new SpectralType[] { SpectralType.A2, SpectralType.F2, SpectralType.G2, SpectralType.K2, SpectralType.M2, SpectralType.L2, SpectralType.T2, SpectralType.Y2 }) {
            Star star = new Star("foo", null, 0, 0, Luminosity.V, type);
            star.setStandardRadius();
            System.out.println(type + ": " + getSolarConstant(star));
        }
    }
}
