/**
 * Pressure.java
 *
 * Copyright (c) 2007, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Defines the various levels of atmospheric pressure. 'Standard' is considered normal for humans.
 * Thin and Dense are survivable (though uncomfortable) without a pressure suit or breathing mask.
 * Thinner or Denser atmospheres require special survival gear for a human to survive.
 *
 * @author Samuel Penn
 */
public enum Pressure {
    /**
     * This is a vacuum world with no appreciable atmosphere. It requires a full pressure suit
     * for survival by humans, and produces no weather of any note. It is less than 0.001 atmospheres.
     */
    None(0, 1.0, 0.0, 10),
    /**
     * A Trace atmosphere is less than 0.1 atmospheres. It requires a full pressure suit for
     * survival by humans.
     */
    Trace(5000, 1.0, 0.0, 10),
    /**
     * Very Thin atmospheres are between 0.1 and 0.3 atmospheric pressure. They can be survived
     * with breathing gear, but don't require a full pressure suit.
     */
    VeryThin(20000, 0.9, 0.01, 6),
    /**
     * Thin atmospheres range from 0.3 to 0.6 atmospheric pressure. Humans can live and work in
     * such atmospheres without breathing equipment, but many tasks may be fatiguing.
     */
    Thin(45000, 0.8, 0.5, 2),
    /**
     * Standard atmospheric pressure ranges between 0.6 and 1.5. This is considered normal for
     * humans.
     */
    Standard(100000, 0.7, 1.0, 0),
    Dense(150000, 0.6, 0.9, 1),
    VeryDense(500000, 0.5, 0.75, 5),
    SuperDense(2500000, 0.4, 0.5, 50);

    private int     pascals = 0;
    private double	distance = 1.0;
    private double  suitability = 1.0;
    private int		badness = 0;

    Pressure(int pascals, double distance, double suitability, int badness) {
        this.pascals = pascals;
        this.distance = distance;
        this.suitability = suitability;
        this.badness = badness;
    }

    /**
     * Get the effective stellar distance modifier. A thick atmosphere
     * retains heat, meaning the world is warmer as if it were closer to
     * the star.
     */
    public double getEffectiveDistance() {
        return distance;
    }

    public double getSuitability() {
        return suitability;
    }

    public int getBadness() {
        return badness;
    }

    public int getPascals() {
        return pascals;
    }

    /**
     * Get an atmosphere one level denser than this one. If already
     * at SuperDense, simply returns SuperDense.
     */
    public Pressure getDenser() {
        if (this == SuperDense) {
            // Cannot get any denser.
            return SuperDense;
        } else {
            return values()[ordinal()+1];
        }
    }

    /**
     * Get an atmosphere one level thinner than this one. If
     * pressure is already None, then returns None.
     */
    public Pressure getThinner() {
        if (this == None) {
            return None;
        } else {
            return values()[ordinal()-1];
        }
    }

    public boolean isThinnerThan(Pressure pressure) {
        if (pressure != null && (ordinal() < pressure.ordinal())) {
            return true;
        }

        return false;
    }

    public boolean isDenserThan(Pressure pressure) {
        if (pressure != null && (ordinal() > pressure.ordinal())) {
            return true;
        }

        return false;
    }}
