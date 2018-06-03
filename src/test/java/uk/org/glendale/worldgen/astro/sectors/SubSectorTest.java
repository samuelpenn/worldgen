/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.sectors;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class SubSectorTest {
    @Test
    public void testPosition() {
        assertEquals(SubSector.A.getX(), 0);
        assertEquals(SubSector.A.getY(), 0);
        assertEquals(SubSector.D.getX(), 3);
        assertEquals(SubSector.D.getY(), 0);
        assertEquals(SubSector.F.getX(), 1);
        assertEquals(SubSector.F.getY(), 1);
    }

    @Test
    public void testCoordinates() {
        assertEquals(SubSector.A.getMinX(), 1);
        assertEquals(SubSector.A.getMinY(), 1);
        assertEquals(SubSector.A.getMaxX(), 8);
        assertEquals(SubSector.A.getMaxY(), 10);

        assertEquals(SubSector.F.getMinX(), 9);
        assertEquals(SubSector.F.getMinY(), 11);
        assertEquals(SubSector.F.getMaxX(), 16);
        assertEquals(SubSector.F.getMaxY(), 20);
    }
}
