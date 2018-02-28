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
    EMPTY,
    // A system with a single star.
    SINGLE,
    BINARY,
    TRIPLE
}
