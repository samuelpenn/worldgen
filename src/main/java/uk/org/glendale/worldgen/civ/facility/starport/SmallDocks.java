/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.civ.facility.starport;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.StarPort;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

public class SmallDocks extends AbstractFacility {
    public SmallDocks(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setRating(90 + Die.d10(2));
        facility.setTechLevel(planet.getTechLevel());
        if (facility.getTechLevel() < 8) {
            facility.setTechLevel(8);
        }
        planet.setStarPort(StarPort.Do);

        return facility;
    }
}
