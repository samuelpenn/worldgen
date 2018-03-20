/**
 * StarSystemType.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

/**
 * Defines the type of star system, which determines the number and orbital
 * dynamics of the stars in the system.
 */
public enum StarSystemType {
    // Completely empty system, no stars or planets.
    EMPTY(0),
    // A system with a single star.
    SINGLE(1),
    CONJOINED_BINARY(2),
    CLOSE_BINARY(2),
    MEDIUM_BINARY(2),
    FAR_BINARY(2),
    TRIPLE(3),
    ROGUE_PLANET(0);

    private final int numStars;

    private StarSystemType(final int numStars) {
        this.numStars = numStars;
    }

    public int getNumberOfStars() {
        return numStars;
    }
}
