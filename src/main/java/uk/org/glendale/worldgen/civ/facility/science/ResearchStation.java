/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.civ.facility.science;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.Government;
import uk.org.glendale.worldgen.astro.planets.codes.StarPort;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

/**
 * A research station is a science facility designed for scientific research.
 */
public class ResearchStation extends AbstractFacility {
    public ResearchStation(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setRating(90 + Die.d10(2));
        facility.setTechLevel(planet.getTechLevel() + 1);

        return facility;
    }
}
