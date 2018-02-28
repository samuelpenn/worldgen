/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.sectors;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the SectorFactory class.
 */
public class SectorFactoryTest {

    /**
     * Does the sector number generator produce the expected numbers?
     */
    @Test
    public void sectorNameTest() {
        assertEquals(1, SectorFactory.getSectorNumber(0, 0));
        assertEquals(2, SectorFactory.getSectorNumber(0, -1));
        assertEquals(3, SectorFactory.getSectorNumber(1, -1));
        assertEquals(4, SectorFactory.getSectorNumber(1, 0));
        assertEquals(5, SectorFactory.getSectorNumber(1, 1));
        assertEquals(6, SectorFactory.getSectorNumber(0, 1));
        assertEquals(7, SectorFactory.getSectorNumber(-1, 1));
        assertEquals(8, SectorFactory.getSectorNumber(-1, 0));
        assertEquals(9, SectorFactory.getSectorNumber(-1, -1));
        assertEquals(10, SectorFactory.getSectorNumber(-1, -2));
    }

}

