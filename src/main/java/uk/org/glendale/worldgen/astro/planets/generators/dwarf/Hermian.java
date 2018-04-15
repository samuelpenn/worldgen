/**
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
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
 * Generates a Hermian world. Dwarf Terrestrial Group, GeoPassive Lithic.
 * These are hot, silicate worlds with large metalic cores and relatively thin crusts.
 * They may have lost much of their mass through an earlier collision.
 */
public class Hermian extends Dwarf {
    private static final Logger logger = LoggerFactory.getLogger(Hermian.class);

    public Hermian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    /**
     * Add one or more random features to the planet.
     */
    private void addFeatures(Planet planet) {
        if (planet.getTemperature() > Temperature.LeadMelts.getKelvin()) {
            logger.info("Surface temperature is very hot.");
            switch (Die.d6()) {
                case 1:
                    planet.addFeature(MetallicSea);
                    logger.info("Adding metallic sea.");
                    return;
                case 2: case 3:
                    planet.addFeature(MetallicLakes);
                    logger.info("Adding metallic lakes.");
                    return;
                default:
                    // Add nothing.
            }
        }
        switch (Die.d6(2)) {
            case 5:
                planet.addFeature(SouthCrater);
                break;
            case 6:
                planet.addFeature(EquatorialRidge);
                break;
            case 8:
                planet.addFeature(GreatRift);
                break;
            case 9:
                planet.addFeature(NorthCrater);
                break;
            case 10:
                planet.addFeature(BrokenRifts);
                break;
            case 11:
                planet.addFeature(ReMelted);
                break;
            default:
                // No special features.
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.Hermian);
        int radius = 2000 + Die.die(500, 2);

        planet.setRadius(radius);
        planet.setAtmosphere(Atmosphere.Vacuum);
        planet.setPressure(0);

        switch (Die.d6(3)) {
        case 3: case 4:
            planet.setMagneticField(MagneticField.VeryWeak);
            if (Die.d3() == 1) {
                planet.setAtmosphere(Atmosphere.InertGases);
                planet.setPressure(50 + Die.die(100, 1));
            }
            break;
        case 5: case 6: case 7:
            planet.setMagneticField(MagneticField.Minimal);
            break;
        default:
            planet.setMagneticField(MagneticField.None);
        }

        addFeatures(planet);

        if (planet.hasFeature(MetallicSea) || planet.hasFeature(MetallicLakes)) {
            if (Die.d2() == 1) {
                // Outgassing from the liquid metal on the surface.
                planet.setAtmosphere(Atmosphere.Exotic);
                planet.setPressure(50 + Die.die(100, 2));
            }
        }

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addPrimaryResource(planet, SilicateOre);
        addSecondaryResource(planet, SilicateCrystals);
        addSecondaryResource(planet, FerricOre);
        if (planet.hasFeature(MetallicSea) || planet.hasFeature(MetallicLakes)) {
            addPrimaryResource(planet, HeavyMetals);
        } else {
            addSecondaryResource(planet, HeavyMetals);
        }
        addTertiaryResource(planet, Radioactives);
        addTertiaryResource(planet, PreciousMetals);

        return planet;
    }
}
