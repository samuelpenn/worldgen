/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps;

import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;

/**
 * A Small Body is not really a planet, or even a dwarf planet. They are asteroids, comets or other
 * small objects at most a few hundred kilometres across.
 *
 * There maps tend to be incredibly simple.
 */
public class SmallBodyMapper extends PlanetMapper {
    protected static final int    DEFAULT_FACE_SIZE = 24;

    public SmallBodyMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public SmallBodyMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    public void generate() {
        generateHeightMap(24, DEFAULT_FACE_SIZE);
    }
}
