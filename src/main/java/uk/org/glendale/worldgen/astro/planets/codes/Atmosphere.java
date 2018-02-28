/**
 * Atmosphere.java
 *
 * Copyright (c) 2007, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Defines the different types of atmosphere that are common on planets.
 *
 * @author Samuel Penn
 */
public enum Atmosphere {
    Vacuum(0.0, 1.0, 4, 10),
    Standard(1.0, 1.0, 0, 0),
    Chlorine(0.0, 1.0, 5, 40),
    Flourine(0.0, 1.0, 5, 60),
    Oxygen(0.1, 0.25, 2, 10),
    SulphurCompounds(0.0, 0.9, 2, 15),
    NitrogenCompounds(0.0, 0.8, 2, 15),
    OrganicToxins(0.5, 1.0, 1, 3),
    LowOxygen(0.75, 1.0, 2, 2),
    Pollutants(0.5, 1.0, 1, 3),
    HighCarbonDioxide(0.25, 0.75, 1, 3),
    HighOxygen(1.0, 1.0, 0, 1),
    InertGases(0.0, 1.0, 2, 10),
    Hydrogen(0.0, 1.0, 2, 12),
    Primordial(0.0, 0.9, 2, 10),
    WaterVapour(0.0, 0.8, 2, 10),
    CarbonDioxide(0.0, 0.6, 2, 10),
    Tainted(0.75, 1.0, 1, 2),
    Exotic(0.0, 1.0, 4, 50);

    double 	suitability = 0.0;
    double  greenhouse = 1.0;
    int		environmentalRating = 0;
    int		badness = 0;

    Atmosphere(double suitability, double greenhouse, int environmentalRating, int badness) {
        this.suitability = suitability;
        this.greenhouse = greenhouse;
        this.environmentalRating = environmentalRating;
        this.badness = badness;
    }

    /**
     * Get the suitability of this atmosphere for humans. This is on a scale
     * of 0 to 1 or more, with 1.0 being Earth normal.
     *
     * @return		Suitability of the world for human life.
     */
    public double getSuitability() {
        return suitability;
    }

    public int getBadness() {
        return badness;
    }

    public boolean isNonWater() {
        switch (this) {
            case Chlorine:
            case Flourine:
            case Exotic:
            case SulphurCompounds:
            case NitrogenCompounds:
                return true;
        }
        return false;
    }

    public boolean isGaian() {
        return (suitability > 0.1);
    }

    /**
     * Get the greenhouse factor for this atmosphere. This modifies the
     * effective distance of the planet from the star, so values less
     * than 1.0 make the planet warmer.
     *
     * @return		Greenhouse modifier.
     */
    public double getGreenhouse() {
        return greenhouse;
    }

    /**
     * Get the Environmental Protection Rating. This ranges from 0
     * (no protection required) to 6 (highest level of protection
     * required).
     */
    public int getEPRating() {
        return environmentalRating;
    }
}
