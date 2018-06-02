/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.systems;

/**
 * Star system codes are trade codes which apply to the entire star system.
 */
public enum StarSystemCode {
    Ba("Barren", 30),               // Has no life bearing worlds.
    Cp("Subsector Capital", 60),    // System is capital of sub-sector.
    Cs("Sector Capital", 100),      // System is capital of sector.
    Ga("Garden Worlds", 60),        // There are habitable worlds here.
    Hi("High Population", 30),      // Total population is greater than 1 billion.
    Lo("Low Population", 10),       // Total population is less than 1 million.
    Sf("Solar Flares", 100),        // Star is prone to dangerous flares.
    Un("Uncivilised", 30);          // No intelligent life.

    private final String title;
    private final int notability;

    StarSystemCode(final String title, final int notability) {
        this.title = title;
        this.notability = notability;
    }

    /**
     * Gets the full descriptive name of this system code.
     *
     * @return      Descriptive name.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the notability of this code. This is the chance that the code should be mentioned in
     * the text description of the star system. A high notability means the code is quite interesting
     * and should be mentioned, whilst a low notability means its quite boring and can often be
     * left out.
     *
     * @return  Notability percentage.
     */
    public int getNotability() {
        return notability;
    }
}
