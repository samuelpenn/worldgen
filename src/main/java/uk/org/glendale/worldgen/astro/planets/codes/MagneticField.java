/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Enum which defines the strength of a planet's magnetic field. A standard magnetic field is
 * equivalent to that of Earth, and considered good enough to protect from most normal stellar
 * activity. Weak is effective (30%), but solar storms can be dangerous. VeryWeak fields are of
 * only minimal use, around 10% of that of Earth. Minimal fields are around 1% of that of Earth,
 * and only really of scientific interest.
 *
 * Strong is much stronger than that of Earth (x3), and useful around active stars. The fields
 * can be dangerous when in orbit. VeryStrong is equivalent to Jupiter (x10), and can cause
 * serious problems to even orbiting moons.
 *
 * Intense fields are way beyond even Jupiter (x30).
 */
public enum MagneticField {
    None,
    Minimal,
    VeryWeak,
    Weak,
    Standard,
    Strong,
    VeryStrong,
    Intense;
}
