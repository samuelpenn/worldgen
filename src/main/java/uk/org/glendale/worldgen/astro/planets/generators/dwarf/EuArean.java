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
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.codes.MagneticField;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;

/**
 * A GeoCyclic MesoArean world of the Dwarf Terrestrial Group. GeoCylic worlds go through
 * a cycle of active/passive periods. An EuArean world has become cold and dry.
 */
public class EuArean extends Dwarf {
    private static final Logger logger = LoggerFactory.getLogger(EuArean.class);

    public EuArean(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    /**
     * Add one or more random features to the planet.
     */
    private void addFeatures(Planet planet) {
        switch (Die.d6(2)) {
            case 5:
                planet.addFeature(SouthCrater);
                break;
            case 6:
                planet.addFeature(EquatorialRidge);
                break;
            case 7:
                planet.addFeature(GreatRift);
                break;
            case 8:
                planet.addFeature(NorthCrater);
                break;
            case 9:
                planet.addFeature(BrokenRifts);
                break;
            default:
                // No special features.
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.EuArean);
        int radius = 2800 + Die.die(500, 2);

        planet.setRadius(radius);
        planet.setAtmosphere(Atmosphere.CarbonDioxide);
        planet.setPressure(determinePressure(-2));
        planet.setMagneticField(MagneticField.None);

        addFeatures(planet);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addPrimaryResource(planet, SilicateOre);
        addSecondaryResource(planet, SilicateCrystals);
        addTertiaryResource(planet, FerricOre);

        addTraceResource(planet, Water);

        return planet;
    }
}
