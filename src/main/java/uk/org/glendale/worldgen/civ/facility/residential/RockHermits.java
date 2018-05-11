/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ.facility.residential;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.Government;
import uk.org.glendale.worldgen.astro.planets.codes.StarPort;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.FacilityType;
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

/**
 * Rock Hermits live inside hollowed out asteroids. They are generally communist or anarchy.
 */
public class RockHermits extends AbstractFacility {

    public RockHermits(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setTitle("Rock Hermits");

        facility.setRating(60 + Die.d20(2));

        planet.setTechLevel(4 + Die.d2());
        planet.setStarPort(StarPort.Eo);
        switch (Die.d6()) {
            case 1: case 2: case 3:
                planet.setGovernment(Government.Communist);
                planet.setLawLevel(Die.d2() + 2);
                if (Die.d3() == 1) {
                    planet.setStarPort(StarPort.Do);
                }
                break;
            case 4: case 5:
                planet.setGovernment(Government.Anarchy);
                planet.setLawLevel(Die.d2() - 1);
                break;
            case 6:
                planet.setGovernment(Government.TheocraticDictatorship);
                planet.setLawLevel(Die.d3() + 3);
                break;
        }

        return facility;
    }
}
