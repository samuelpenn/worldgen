/**
 * PlanetGroup.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

public enum PlanetClass {
    // Belt Group
    Circumstellar("Circumstellar", PlanetGroup.Belt),
    PlanetaryRing("Planetary Ring", PlanetGroup.Belt),

    // Small Body Group
    Vulcanoidal("Vulcanoidal", PlanetGroup.SmallBody),
    Asteroidal("Asteroidal", PlanetGroup.SmallBody),
    Cometary("Cometary", PlanetGroup.SmallBody),

    // Dwarf Terrestrial Group
    Protothermic("Protothermic", PlanetGroup.Dwarf),
    GeoPassive("Geo Passive", PlanetGroup.Dwarf),
    GeoThermic("Geo Thermic", PlanetGroup.Dwarf),
    GeoTidal("Geo Tidal", PlanetGroup.Dwarf),
    GeoCyclic("Geo Cyclic", PlanetGroup.Dwarf),

    // Jovian Group
    SubJovian("Sub Jovian", PlanetGroup.Jovian),
    DwarfJovian("Dwarf Jovian", PlanetGroup.Jovian),
    MesoJovian("Meso Jovian", PlanetGroup.Jovian),
    SuperJovian("Super Jovian", PlanetGroup.Jovian),
    Chthonian("Chthonian", PlanetGroup.Jovian),

    // Terrestrial Group
    ProtoActive("Proto Active", PlanetGroup.Terrestrial),
    Epistellar("Epistellar", PlanetGroup.Terrestrial),
    Telluric("Telluric", PlanetGroup.Terrestrial),
    Arid("Arid", PlanetGroup.Terrestrial),
    Tectonic("Tectonic", PlanetGroup.Terrestrial),

    BDO("Big Damn Object", PlanetGroup.Construct);

    private final String title;
    private final PlanetGroup group;

    private PlanetClass(String title, PlanetGroup group) {
        this.title = title;
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public PlanetGroup getGroup() {
        return group;
    }

}
