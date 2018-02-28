/**
 * NoSuchPlanetException.java
 *
 * Copyright (c) 2011, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.commodities;

/**
 * Enum for defining all the common commodity names. Code should refer to commodities by their enum
 * rather than by their name, in order to guarantee a level of type safety.
 */
public enum CommodityName {
    Hydrogen("Hydrogen"),
    Helium("Helium"),
    OrganicGases("Organic Gases"),
    CorrosiveGases("Corrosive Gases"),
    ExoticGases("Exotic Gases"),
    SilicateOre("Silicate Ore"),
    CarbonicOre("Carbonic Ore"),
    FerricOre("Ferric Ore"),
    HeavyMetals("Heavy Metals"),
    Radioactives("Radioactives"),
    RareMetals("Rare Metals"),
    PreciousMetals("Precious Metals"),
    SilicateCrystals("Silicate Crystals"),
    ExoticCrystals("Exotic Crystals"),
    CarbonicCrystals("Carbonic Crystals"),
    Water("Water"),
    Oxygen("Oxygen"),
    OrganicChemicals("Organic Chemicals"),
    Protobionts("Protobionts"),
    Prokaryotes("Prokaryotes"),
    Cyanobacteria("Cyanobacteria"),
    Algae("Algae"),
    Metazoa("Metazoa"),
    Plankton("Plankton"),
    Echinoderms("Echinoderms"),

    Unobtanium("Unobtanium");

    private String name;

    CommodityName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
