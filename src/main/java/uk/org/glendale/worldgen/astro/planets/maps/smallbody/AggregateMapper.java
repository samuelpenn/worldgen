/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.smallbody;

import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.maps.SmallBodyMapper;

public class AggregateMapper extends SmallBodyMapper {
    public AggregateMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public AggregateMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }
}
