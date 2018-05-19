/**
 * Temperature.java
 *
 * Copyright (c) 2007, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Describes the average surface temperature of the planet. The temperature across a planet can vary widely.
 * Earth's average temperature is 'Standard', but can vary between 'ExtremelyCold' and 'VeryHot'.
 *
 * Worlds with temperatures between 'Cold' and 'Hot' are considered suitable for human habitation, though
 * 'Cold' and 'Hot' worlds aren't ideal. Both generally allow liquid water on the surface, and won't result
 * in immediate death without a survival suit.
 *
 * ExtremelyCold: Temperature rarely above 100K
 * VeryCold: Too cold for humans. Temperature rarely above 200K
 * Cold: Winter world, similar to Arctic conditions all over. Special life support
 *       is needed for a colony to survive. Rarely above 250K
 * Cool: Uncomfortable, long winters and short summers. Humans can survive
 *       without special life support.
 * Standard: Earth
 * Warm: Tropical climate, hot all over. Really nice to uncomfortable.
 * Hot: Too hot, difficult to survive. Rarely below 320K.
 * VeryHot: Rarely below 400K. Water boils. Humans cannot live.
 * ExtremelyHot: Rock melts.
 *
 */
public enum Temperature {
    DeepSpace(0.0, 3, 20),          // Cosmic Background radiation temperature.
    UltraCold(0.0, 5, 25),
    ExtremelyCold_5(0.0, 10, 20),
    ExtremelyCold_4(0.0, 20, 20),
    ExtremelyCold_3(0.0, 50, 20),
    OxygenMelts(0.0, 55, 20),      // Melting point of oxygen.
    OxygenBoils(0.0, 90, 20),      // Boiling point of oxygen.
    ExtremelyCold_2(0.0, 100, 20),
    ExtremelyCold_1(0.0, 150, 20),
    ExtremelyCold(0.0, 200, 20),
    VeryCold(0.0, 230, 15),
    Cold(0.1, 260, 7),
    TriplePoint(0.0, 273, 5),       // Freezing point of water.
    Cool(0.5, 280, 3),
    Standard(1.0, 290, 0),
    Warm(0.75, 300, 2),
    Hot(0.1, 320, 7),
    VeryHot(0.0, 350, 50),
    WaterBoils(0.0, 373, 100),      // Boiling point of water.
    PaperBurns(0.0, 505, 150),      // Temperature at which paper burns.
    LeadMelts(0.0, 750, 200),       // Temperature at which lead melts.
    IronMelts(0.0, 1000, 250),      // Average melting point of iron.
    SilicatesMelt(0.0, 1500, 250),  // Many silicate rocks melt.
    IronBoils(0.0, 2500, 250),      // Iron boils.
    SilicatesBoil(0.0, 5000, 250),  // Rock boils.
    StellarSurface(0.0, 6500, 250); // Temperature at the surface of a G2 star.

    final double	suitability;
    final int		kelvin;
    final int		badness;

    Temperature(double suitability, int kelvin, int badness) {
        this.suitability = suitability;
        this.kelvin = kelvin;
        this.badness = badness;
    }

    public int getBadness() {
        return badness;
    }

    public double getSuitability() {
        return suitability;
    }

    /**
     * Gets the actual mid-point temperature for this Temperature value.
     *
     * @return  Temperature in Kelvin.
     */
    public int getKelvin() {
        return kelvin;
    }

    public boolean isColderThan(Temperature otherTemperature) {
        return ordinal() < otherTemperature.ordinal();
    }

    public boolean isHotterThan(Temperature otherTemperature) {
        return ordinal() > otherTemperature.ordinal();
    }

    /**
     * Get a temperature that is one level hotter than the current one.
     * If the temperature is already ExtremelyHot, then ExtremelyHot is
     * returned.
     */
    public Temperature getHotter() {
        if (this == StellarSurface) return StellarSurface;

        return Temperature.values()[ordinal()+1];
    }

    /**
     * Get a temperature that is one level colder than the current one.
     * If the temperature is already ExtremelyCold, then ExtremelyCold
     * is returned.
     */
    public Temperature getColder() {
        if (this == UltraCold) return UltraCold;

        return Temperature.values()[ordinal()-1];
    }

    /**
     * Given a temperature in Kelvin, return the suitable enum for that
     * temperature.
     *
     * @param kelvin    Temperature in Kelvin.
     * @return          Temperature enum.
     */
    public static Temperature getTemperature(int kelvin) {
        if (kelvin < UltraCold.getKelvin()) {
            return UltraCold;
        } else if (kelvin > StellarSurface.getKelvin()) {
            return StellarSurface;
        }

        Temperature t = UltraCold;
        while (t != StellarSurface && kelvin > (t.getKelvin() + t.getHotter().getKelvin())/2) {
            t = t.getHotter();
        }

        return t;
    }
}