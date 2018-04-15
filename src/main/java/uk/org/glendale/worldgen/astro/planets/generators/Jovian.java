/**
 * Jovian.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.generators;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.Helium;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.Hydrogen;

/**
 * Planet Generator for the Jovian group of planet types.
 */
public class Jovian extends PlanetGenerator {

    // Some notes taken from here: https://en.wikipedia.org/wiki/Sudarsky%27s_gas_giant_classification
    public enum JovianFeature implements PlanetFeature {
        MethaneClouds, // Whitish - Blue
        AmmoniaClouds, // Pale Brown
        WaterClouds,   // White
        Cloudless,     // Blue
        AlkaliMetals,  // Dark
        SilicateClouds // Greenish
    }

    public Jovian(WorldGen worldgen, StarSystem system, Star primary, Planet previous, long distance) {
        super(worldgen, system, primary, previous, distance);
    }

    /**
     * Get a generated planet. Can't be called directly on the Belt class, because
     * we don't know exactly what type of planet to create.
     * Call getPlanet(String, PlanetType) instead.
     *
     * @param name  Name of planet to be generated.
     * @return      Always throws UnsupportedException().
     */
    public Planet getPlanet(String name) {
        throw new UnsupportedException("Must define planet type");
    }


    @Override
    public Planet getPlanet(String name, PlanetType type) {
        Planet planet =  definePlanet(name, type);
        planet.setRadius(Die.d6(3) * 5000);

        // Set default day length to be around 10 hours.
        planet.setDayLength(9 * 86400 + Die.die(3600, 2));

        addPrimaryResource(planet, Hydrogen);
        addTertiaryResource(planet, Helium);

        return planet;
    }
}
