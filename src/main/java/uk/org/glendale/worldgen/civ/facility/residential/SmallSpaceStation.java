/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.civ.facility.residential;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.Government;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

/**
 * A small space station, normally up to a 100 population, but some may be up to ten times this.
 */
public class SmallSpaceStation extends AbstractFacility {
    public SmallSpaceStation(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setRating(90 + Die.d10(2));
        planet.setTechLevel(7 + Die.d2());
        planet.setGovernment(Government.Corporation);
        planet.setLawLevel(5);

        facility.setTechLevel(planet.getTechLevel());

        return facility;
    }
}
