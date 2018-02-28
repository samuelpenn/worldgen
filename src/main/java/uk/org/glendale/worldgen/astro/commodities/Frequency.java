/**
 * Frequency.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.commodities;

/**
 * Enum that defines the frequency of a commodity when found in nature.
 * COMMON commodities tend to be found in large amounts in most systems.
 * ARTIFICIAL commodities aren't found in nature.
 */
public enum Frequency {
    COMMON(100),
    UNCOMMON(60),
    RARE(30),
    VERYRARE(10),
    TRACE(3),
    ARTIFICIAL(0);

    private final int base;

    Frequency(int base) {
        this.base = base;
    }

    /**
     * Gets the base frequency, as a percentage from 0 to 100%.
     *
     * @return  Frequency between 0 and 100%.
     */
    public int getBaseFrequency() {
        return base;
    }
}
