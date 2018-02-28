/**
 * PlanetTest.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets;

import org.junit.Test;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;

import static junit.framework.TestCase.assertEquals;

public class PlanetTest {

    @Test
    public void basicPlanetValues() {
        Planet planet = new Planet();
        planet.setId(5);
        planet.setName("Test");
        planet.setType(PlanetType.Hermian);
        planet.setRadius(1000);
        planet.setDistance(100);

        assertEquals(planet.getId(), 5);
        assertEquals(planet.getName(), "Test");
        assertEquals(planet.getType(), PlanetType.Hermian);
        assertEquals(planet.getRadius(), 1000);
        assertEquals(planet.getDiameter(), 2000);
        assertEquals(planet.getDistance(), 100);
    }

    @Test
    public void boundedTests() {
        Planet planet = new Planet();

        planet.setHydrographics(50);
        assertEquals(planet.getHydrographics(), 50);

        planet.setHydrographics(-5);
        assertEquals(planet.getHydrographics(), 0);

        planet.setHydrographics(150);
        assertEquals(planet.getHydrographics(), 100);
    }
}
