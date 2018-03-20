/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps;

import uk.org.glendale.worldgen.astro.planets.Planet;

/**
 * A Dwarf Terrestrial world.
 */
public class DwarfMapper extends PlanetMapper {
    protected static final int    DEFAULT_FACE_SIZE = 24;

    public DwarfMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public DwarfMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    public void generate() {
        generateHeightMap(24, DEFAULT_FACE_SIZE);
    }}
