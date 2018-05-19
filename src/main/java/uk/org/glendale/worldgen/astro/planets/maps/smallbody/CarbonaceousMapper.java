/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.smallbody;

import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.maps.SmallBodyMapper;

/**
 * A Carbonaceous asteroid has a dark, rocky surface. They may have a few volatiles.
 */
public class CarbonaceousMapper extends SmallBodyMapper {
    public CarbonaceousMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public CarbonaceousMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private static final Tile CARBON = new Tile("Carbon", "#606060", true);

    public void generate() {
        super.generate();

        int baseGrey = 25;
        for (int y=0; y < getNumRows(); y++) {
            for (int x = 0; x < getWidthAtY(y); x++) {
                int grey = baseGrey + getHeight(x, y) / 2;
                setTile(x, y, CARBON.getShaded(grey));
            }
        }

        // Larger asteroids tend to be more spherical.
        int heightDividor = 3;
        if (planet.getRadius() > 300) {
            heightDividor = 1 + planet.getRadius() / 100;
        }

        // After finishing with the height map, set it to more consistent values
        // so that the bump mapper can use it cleanly.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                int h = getHeight(x - 1, y) + getHeight(x, y) + getHeight(x + 1, y);
                setHeight(x, y, h / heightDividor);
            }
        }
    }
}
