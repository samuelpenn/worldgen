/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ.facility;

import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.FacilityType;

public abstract class AbstractFacility {
    protected Planet planet;
    protected FacilityType type;

    public AbstractFacility(Planet planet) {
        this.planet = planet;
    }

    private FacilityType getType() {
        String[] parts = this.getClass().getName().split("\\.");

        String typeName = parts[parts.length - 2].toUpperCase();

        return FacilityType.valueOf(typeName);
    }

    /**
     * Gets a new facility object, filled in with some basic values.
     *
     * @return  New facility object.
     */
    public Facility getFacility() {
        Facility facility = new Facility();
        facility.setPlanetId(planet.getId());
        facility.setName(this.getClass().getSimpleName());
        facility.setType(getType());

        facility.setRating(100);
        facility.setTechLevel(planet.getTechLevel());

        return facility;
    }
}
