/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.dwarf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.*;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;

/**
 * Ferrinian worlds are comprised primarily of metals. They are found close to their stars.
 * They tend to be small, and are often the left over core of a world that collided with
 * another object soon after formation.
 */
public class Ferrinian extends Dwarf {
    private static final Logger logger = LoggerFactory.getLogger(Ferrinian.class);

    public Ferrinian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    /**
     * Add one or more random features to the planet.
     */
    private void addFeatures(Planet planet) {
        switch (Die.d6(3)) {
            case 8:
                planet.addFeature(BrokenRifts);
                break;
            case 9:
                planet.addFeature(GreatRift);
                break;
            case 10:
                planet.addFeature(NorthCrater);
                break;
            case 11:
                planet.addFeature(SouthCrater);
                break;
            case 12:
                planet.addFeature(NaturalHoneyComb);
                break;
            case 13:
                planet.addFeature(ArtificialHoneyComb);
                break;
            default:
                // No special features.
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.Ferrinian);
        int radius = 1500 + Die.die(400, 2);

        planet.setRadius(radius);
        planet.setAtmosphere(Atmosphere.Vacuum);
        planet.setPressure(Pressure.None);

        switch (Die.d6(3)) {
            case 3:
                planet.setMagneticField(MagneticField.Weak);
                break;
            case 4: case 5:
                planet.setMagneticField(MagneticField.VeryWeak);
                break;
            case 6: case 7: case 8:
                planet.setMagneticField(MagneticField.Minimal);
                break;
            default:
                planet.setMagneticField(MagneticField.None);
        }

        addFeatures(planet);

        // Define resources for this world.
        addPrimaryResource(planet, SilicateOre);
        addTertiaryResource(planet, SilicateCrystals);
        addPrimaryResource(planet, FerricOre);
        addPrimaryResource(planet, HeavyMetals);
        addSecondaryResource(planet, Radioactives);
        addSecondaryResource(planet, PreciousMetals);

        return planet;
    }
}
