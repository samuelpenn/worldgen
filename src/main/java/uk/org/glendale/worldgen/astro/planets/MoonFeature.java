/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets;

/**
 * Defines features that are unique to moons.
 */
public enum MoonFeature implements PlanetFeature {
    SmallMoon,
    LargeMoon,
    TidallyLocked,
    AlmostLocked
}
