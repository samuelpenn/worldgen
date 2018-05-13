/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ.facility.starport;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.StarPort;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.FacilityType;
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

/**
 * Ramshackle Docks are orbital starports no better than class D. They are
 * generally designed to be used by locals, with no attempts to provide facilities
 * for travellers or traders from other systems.
 */
public class RamshackleDocks extends AbstractFacility {
    public RamshackleDocks(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setRating(50 + Die.d12(2));
        facility.setTechLevel(planet.getTechLevel());
        if (facility.getTechLevel() < 7) {
            facility.setTechLevel(7);
        }
        planet.setStarPort(StarPort.Eo);
        if (Die.d4() == 1) {
            planet.setStarPort(StarPort.Do);
            facility.setRating(facility.getRating() + 20);
        }

        return facility;
    }
}
