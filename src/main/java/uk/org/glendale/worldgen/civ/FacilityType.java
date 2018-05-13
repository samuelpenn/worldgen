/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ;

public enum FacilityType {
    RESIDENTIAL("Residential"),
    INDUSTRY("Industry"),
    AGRICULTURE("Agriculture"),
    MINING("Mining"),
    SCIENCE("Science"),
    MILITARY("Military"),
    STARPORT("StarPort"),
    GENERIC("");

    private final String title;

    FacilityType(final String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
