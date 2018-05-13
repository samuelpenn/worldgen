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
import uk.org.glendale.worldgen.civ.facility.AbstractFacility;

/**
 * Class to generate and manage Dust Farmers. Residential facility sometimes
 * found in dust rings around young stars. Tend to be poor and low tech.
 */
public class DustFarmers extends AbstractFacility {
    public DustFarmers(Planet planet) {
        super(planet);
    }

    public Facility getFacility() {
        Facility facility = super.getFacility();

        facility.setRating(50 + Die.d20(2));
        planet.setTechLevel(4 + Die.d2());
        planet.setStarPort(StarPort.Eo);
        switch (Die.d6()) {
            case 1: case 2:
                planet.setGovernment(Government.Communist);
                planet.setLawLevel(Die.d2());
                break;
            case 3: case 4: case 5: case 6:
                planet.setGovernment(Government.Anarchy);
                planet.setLawLevel(Die.d2() - 1);
                break;
        }

        return facility;
    }
}
