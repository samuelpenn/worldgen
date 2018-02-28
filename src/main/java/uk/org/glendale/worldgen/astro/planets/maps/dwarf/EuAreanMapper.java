/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;

/**
 * EuArean worlds are similar to Mars.
 */
public class EuAreanMapper extends AreanMapper {


    public EuAreanMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public EuAreanMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }


    /**
     * Generate a Hermian surface landscape. This will be grey, barren and cratered.
     */
    public void generate() {
        super.generate();

        if (planet != null) {
            List<PlanetFeature> features = planet.getFeatures();
            if (features != null && features.size() != 0) {
                generateFeatures(features);
            }
        }
    }

    private void generateFeatures(List<PlanetFeature> features) {
        for (PlanetFeature f : features) {
            if (f == GreatRift) {
                // A single rift split across the world.
                addRift(RIFT,24 + Die.d12(3));
                flood(RIFT, 1);
            } else if (f == BrokenRifts) {
                // A number of small rifts in the surface of the world.
                int numRifts = 6 + Die.d4(2);
                for (int r = 0; r < numRifts; r++) {
                    addRift(RIFT,6 + Die.d4(2));
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Planet planet = new Planet();
        planet.addFeature(Dwarf.DwarfFeature.GreatRift);
        PlanetMapper p = new EuAreanMapper(planet);
        System.out.println("EuArean:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/euArean.png"));

    }
}
