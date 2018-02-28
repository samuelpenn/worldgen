/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.smallbody;

import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.maps.SmallBodyMapper;

public class CarbonaceousMapper extends SmallBodyMapper {
    public CarbonaceousMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public CarbonaceousMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }
}
