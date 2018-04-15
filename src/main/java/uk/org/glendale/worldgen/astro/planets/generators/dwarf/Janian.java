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
import uk.org.glendale.worldgen.astro.planets.codes.Pressure;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.PreciousMetals;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.Radioactives;

/**
 * Janian worlds are tidally locked to their star. They are rich in silicates, and may have
 * night side ice caps. They are otherwise barren, though may have a trace atmosphere of
 * water vapour.
 */
public class Janian extends Dwarf {
    private static final Logger logger = LoggerFactory.getLogger(Janian.class);

    public Janian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    private void addFeatures(Planet planet) {
        if (Die.d2() == 1) {
            planet.addFeature(DwarfFeature.NightsideIce);
            if (Die.d2() == 1) {
                planet.setAtmosphere(Atmosphere.WaterVapour);
                planet.setPressure(Pressure.Trace);
            }
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.Janian);
        int radius = 1500 + Die.die(800, 3);

        planet.setRadius(radius);
        planet.setAtmosphere(Atmosphere.Vacuum);
        planet.setPressure(Pressure.None);

        switch (Die.d6(3)) {
        case 3:
            planet.setMagneticField(MagneticField.Weak);
            if (Die.d2() == 1) {
                planet.setAtmosphere(Atmosphere.InertGases);
                planet.setPressure(Pressure.Trace);
            }
            break;
        case 4: case 5:
            planet.setMagneticField(MagneticField.VeryWeak);
                if (Die.d3() == 1) {
                    planet.setAtmosphere(Atmosphere.InertGases);
                    planet.setPressure(Pressure.Trace);
                }
            break;
        case 6: case 7:
            planet.setMagneticField(MagneticField.Minimal);
            break;
        default:
            planet.setMagneticField(MagneticField.None);
        }

        // Janian worlds are tidally locked.
        planet.setDayLength(star.getPeriod(planet.getDistance()));

        addFeatures(planet);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addPrimaryResource(planet, SilicateOre);
        addSecondaryResource(planet, SilicateCrystals);
        addSecondaryResource(planet, FerricOre);
        addSecondaryResource(planet, HeavyMetals);
        addTertiaryResource(planet, Radioactives);
        addTertiaryResource(planet, PreciousMetals);
        addTertiaryResource(planet, Water);

        return planet;
    }
}
