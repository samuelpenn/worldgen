/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.sectors;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the Sector object.
 */
public class SectorTest {

    @Test
    public void basicSectorValues() {
        Sector sector = new Sector("Test", 1, 2);

        assertEquals("Test", sector.getName());
        assertEquals(1, sector.getX());
        assertEquals(2, sector.getY());
    }

}
