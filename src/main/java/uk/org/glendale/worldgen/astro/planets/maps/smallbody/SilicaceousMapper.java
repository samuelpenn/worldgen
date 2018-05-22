/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.smallbody;

import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.maps.SmallBodyMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;
import uk.org.glendale.worldgen.astro.planets.tiles.Rough;

public class SilicaceousMapper extends SmallBodyMapper {
    public SilicaceousMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public SilicaceousMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private static final Tile SILICATES = new Tile("Carbon", "#A09070", true);

    public void generate() {
        super.generate();

        int baseGrey = 50;
        for (int y=0; y < getNumRows(); y++) {
            for (int x = 0; x < getWidthAtY(y); x++) {
                int grey = baseGrey + getHeight(x, y) / 2;
                setTile(x, y, new Rough(SILICATES.getShaded(grey)));
            }
        }

        // Larger asteroids tend to be more spherical.
        int heightDividor = 3;
        if (planet.getRadius() > 240) {
            heightDividor = 1 + planet.getRadius() / 80;
        }

        // After finishing with the height map, set it to more consistent values
        // so that the bump mapper can use it cleanly.
        smoothHeights(planet, heightDividor);
    }
}
