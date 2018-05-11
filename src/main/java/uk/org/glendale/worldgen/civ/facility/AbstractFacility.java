/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ.facility;

import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.FacilityType;

/**
 * Base class for Facility handlers. These generate facilities, and also process them.
 */
public abstract class AbstractFacility {
    protected Planet planet;
    protected FacilityType type;

    public AbstractFacility(Planet planet) {
        this.planet = planet;
    }

    /**
     * Determine the type of the facility based on the package this class is part of.
     * When called from a sub-class, uses the sub-class package name.
     *
     * @return  Enum for the type of facility.
     */
    private FacilityType getType() {
        String[] parts = this.getClass().getName().split("\\.");

        String typeName = parts[parts.length - 2].toUpperCase();

        try {
            return FacilityType.valueOf(typeName);
        } catch (Throwable e) {
            return FacilityType.GENERIC;
        }
    }

    /**
     * Gets the title of the facility. The title is the human readable name, and this
     * generates a default based on the class name. Assumes camel case name, and adds
     * a space before each capital letter within the name.
     *
     * @return  Human readable name for this facility.
     */
    private String getTitle() {
        String title = this.getClass().getSimpleName();

        title = title.replaceAll("([A-Z])", " $1").trim();

        return title;
    }

    /**
     * Gets a new facility object, filled in with some basic values based on the
     * properties of the sub-class.
     *
     * @return  New facility object.
     */
    public Facility getFacility() {
        Facility facility = new Facility();
        facility.setPlanetId(planet.getId());
        facility.setName(this.getClass().getSimpleName());
        facility.setTitle(getTitle());
        facility.setType(getType());

        facility.setRating(100);
        facility.setTechLevel(planet.getTechLevel());

        return facility;
    }
}
