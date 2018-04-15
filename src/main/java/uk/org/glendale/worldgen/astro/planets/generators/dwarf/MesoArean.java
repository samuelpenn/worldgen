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

/**
 * A GeoCyclic MesoArean world of the Dwarf Terrestrial Group. GeoCylic worlds go through
 * a cycle of active/passive periods. A MesoArean world is in the active part of its cycle.
 */
public class MesoArean extends Dwarf {
    private static final Logger logger = LoggerFactory.getLogger(MesoArean.class);

    public MesoArean(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    /**
     * Add one or more random features to the planet.
     */
    private void addFeatures(Planet planet) {
        switch (Die.d6(2)) {
            case 5:
                planet.addFeature(DwarfFeature.SouthCrater);
                break;
            case 6:
                planet.addFeature(DwarfFeature.EquatorialRidge);
                break;
            case 7:
                planet.addFeature(DwarfFeature.GreatRift);
                break;
            case 8:
                planet.addFeature(DwarfFeature.NorthCrater);
                break;
            default:
                // No special features.
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.MesoArean);
        int radius = 2800 + Die.die(500, 2);

        planet.setRadius(radius);
        planet.setAtmosphere(Atmosphere.CarbonDioxide);
        planet.setPressure(determinePressure(0));
        planet.setMagneticField(MagneticField.Minimal);

        int pascals = planet.getPressure();
        if (pascals < 1000) {
            // Shouldn't happen.
            planet.setHydrographics(0);
        } else if (pascals < 10000) {
            planet.setHydrographics(Die.d4());
        } else if (pascals < 30000) {
            planet.setMagneticField(MagneticField.VeryWeak);
            planet.setHydrographics(Die.d6(3));
        } else {
            // Shouldn't happen.
            planet.setHydrographics(Die.d8(3));
        }

        addFeatures(planet);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addPrimaryResource(planet, SilicateOre);
        addSecondaryResource(planet, SilicateCrystals);
        addTertiaryResource(planet, FerricOre);

        addTertiaryResource(planet, Water);

        return planet;
    }
}
