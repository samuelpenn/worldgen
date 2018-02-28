/**
 * PlanetGroup.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * A PlanetGroup is the highest level of classification in the PCL. A group contains classes, and within
 * classes are types. Groups are very broad categories for lumping planetary objects.
 */
public enum PlanetGroup {
    // Belt.
    Belt("Belt"),
    // Asteroids, Comets and Vulcanoids.
    SmallBody("Small Body"),
    // Dwarf Terrestrial Group.
    Dwarf("Dwarf Terrestrial"),
    // Terrestrial Worlds.
    Terrestrial("Terrestrial"),
    // Massive Terrestrial Worlds.
    Helian("Helian"),
    // Gas Giants.
    Jovian("Jovian"),
    // Rogue Planets.
    Planemo("Planemo"),
    // Artificial Worlds.
    Construct("Construct");

    private final String title;
    private PlanetGroup(final String title) {
        this.title = title;
    }

    /**
     * Gets the human readable title for this group. May contain spaces
     * and is properly capitalised.
     *
     * @return  Title of the group.
     */
    public String getTitle() {
        return title;
    }
}
