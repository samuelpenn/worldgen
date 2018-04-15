/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Physics helper methods.
 */
public class PhysicsTest {
    @Test
    public void simpleRounding() {
        assertEquals(100, Physics.round(123, 1));
        assertEquals(120, Physics.round(123, 2));
        assertEquals(123, Physics.round(123, 3));
        assertEquals(123, Physics.round(123, 4));
    }

    @Test
    public void complexRounding() {
        assertEquals(-123, Physics.round(-123, 3));
        assertEquals(4570, Physics.round(4567, 3));
        assertEquals(456800000, Physics.round(456765432, 4));
    }

    /**
     * Test that we get expected orbital periods for some common situations.
     * We are not looking for perfect results, but within 1% of expected is good enough.
     */
    @Test
    public void orbitalPeriods() {
        long earthPeriod = Physics.getOrbitalPeriod(Physics.SOL_MASS, Physics.AU * 1000);
        assertEquals(Physics.STANDARD_YEAR, earthPeriod, earthPeriod * 0.01);

        long marsPeriod = Physics.getOrbitalPeriod(Physics.SOL_MASS, 228 * Physics.MKM * 1000);
        assertEquals(Physics.STANDARD_DAY * 687, marsPeriod, marsPeriod * 0.01);

        long jupiterPeriod = Physics.getOrbitalPeriod(Physics.SOL_MASS, 778 * Physics.MKM * 1000);
        assertEquals(Physics.STANDARD_DAY * 4332, jupiterPeriod, jupiterPeriod * 0.01);
    }
}
