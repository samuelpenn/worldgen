/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

import org.junit.Test;
import uk.org.glendale.worldgen.astro.sectors.Sector;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class StarSystemTest {
    @Test
    public void basicSystemValues() {
        Sector sector = new Sector("Test", 0, 0);
        StarSystem system = new StarSystem(sector, "Test", 1, 12, StarSystemType.EMPTY, Zone.GREEN);

        assertEquals(system.getName(), "Test");
        assertEquals(system.getType(), StarSystemType.EMPTY);
        assertEquals(system.getZone(), Zone.GREEN);
        assertEquals(system.getX(), 1);
        assertEquals(system.getY(), 12);
    }

    @Test
    public void systemCodes() {
        Sector sector = new Sector("Test", 0, 0);
        StarSystem system = new StarSystem(sector, "Test", 1, 12, StarSystemType.EMPTY, Zone.GREEN);

        assertFalse(system.getTradeCodes().contains(StarSystemCode.Ba));
        assertFalse(system.getTradeCodes().contains(StarSystemCode.Un));

        system.addTradeCode(StarSystemCode.Ba);
        system.addTradeCode(StarSystemCode.Un);

        assertTrue(system.getTradeCodes().contains(StarSystemCode.Ba));
        assertTrue(system.getTradeCodes().contains(StarSystemCode.Un));
    }

}
