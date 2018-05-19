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
    protected static final int    DEFAULT_FACE_SIZE = 12;

    public SmallBodyMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public SmallBodyMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    /**
     * A SmallBody has a deform map rather than a height map. The height map is normally used
     * just for bump mapping. A deform map actually deforms the object, so is far more radical.
     * This better suits asteroids which are often not entirely round.
     */
    public void generate() {
        generateHeightMap(12, DEFAULT_FACE_SIZE);
        hasHeightMap = false;
        hasDeformMap = true;
    }
}
