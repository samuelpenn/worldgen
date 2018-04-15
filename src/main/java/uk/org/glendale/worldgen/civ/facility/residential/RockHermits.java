/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ.facility.residential;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.FacilityType;
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

public class RockHermits extends AbstractFacility {

    public RockHermits(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setRating(60 + Die.d20(2));

        return facility;
    }

    public static void main(String[] args) {
        Planet planet = new Planet();
        RockHermits rh = new RockHermits(planet);

        Facility f = rh.getFacility();

        System.out.println(f.getName());
        System.out.println(f.getType());
    }
}
