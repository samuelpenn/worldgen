/**
 * Dwarf.java
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
import uk.org.glendale.worldgen.astro.planets.codes.Pressure;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

/**
 * Generator for the Dwarf Terrestrial group of planets.
 */
public class Dwarf extends PlanetGenerator {

    // Features common to Dwarf planets.
    public enum DwarfFeature implements PlanetFeature {
        GreatRift,
        BrokenRifts,
        MetallicSea,
        MetallicLakes,
        NaturalHoneyComb,
        ArtificialHoneyComb,
        NorthCrater,
        SouthCrater,
        EquatorialRidge,
        NightsideIce,
        ReMelted
    }

    public Dwarf(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
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
        Planet planet = definePlanet(name, type);
        planet.setRadius(Die.d6(3) * 100);

        return planet;
    }

    /**
     * Randomly determine atmospheric pressure of a Dwarf world, from Trace to Thin, with VeryThin
     * being the most likely. If the modifier is negative, then Trace atmospheres become more likely,
     * and positive modifiers make Thin atmospheres more likely.
     *
     * A modifier of 5 either way forces a Trace or Thin atmosphere.
     *
     * @param modifier      Modifier to the thickness (normally -3 to +3).
     */
    protected Pressure determinePressure(int modifier) {
        int roll = Die.d6() + modifier;

        if (roll < 2) {
            return Pressure.Trace;
        } else if (roll < 6) {
            return Pressure.VeryThin;
        } else {
            return Pressure.Thin;
        }
    }

}
