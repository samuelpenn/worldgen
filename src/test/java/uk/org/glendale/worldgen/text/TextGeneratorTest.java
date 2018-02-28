/**
 * TextGeneratorTest.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.text;

import org.junit.Test;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;

import static org.junit.Assert.assertEquals;

public class TextGeneratorTest {

    /**
     * Test that the property file is found, text used and the variable correctly expanded.
     */
    @Test
    public void basicPlanetDescription() {
        TextGenerator text;
        Planet          planet = new Planet();
        planet.setType(PlanetType.Hermian);
        planet.setRadius(1000);

        text = new TextGenerator(planet);
        String d = text.getFullDescription();

        assertEquals(d, "A small barren world 1,000km in radius.");
    }

    /**
     * Now test that the feature is correctly picked up and included in the output text.
     */
    @Test
    public void featureDescription() {
        TextGenerator text;
        Planet          planet = new Planet();
        planet.setType(PlanetType.Hermian);
        planet.setRadius(1000);
        planet.addFeature(Dwarf.DwarfFeature.MetallicSea);

        text = new TextGenerator(planet);
        String d = text.getFullDescription();

        assertEquals(d, "A small barren world 1,000km in radius. It has a sea of molten metal.");
    }
}
