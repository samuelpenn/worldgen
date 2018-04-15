/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.jovian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.*;
import uk.org.glendale.worldgen.astro.planets.generators.Jovian;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.planets.generators.Jovian.JovianFeature.*;
import static uk.org.glendale.worldgen.astro.planets.generators.Jovian.JovianFeature.SilicateClouds;

/**
 * Sokarian worlds of the SubJovian class of the Jovian group. They are very hot worlds, close
 * to their star, often with silicate clouds (Sudarsky class V).
 */
public class Sokarian extends Jovian {
    private static final Logger logger = LoggerFactory.getLogger(Sokarian.class);

    public Sokarian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    private void generateFeatures(Planet planet) {
        planet.addFeature(SilicateClouds);
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet =  definePlanet(name, type);
        planet.setRadius(45000 + Die.die(5000, 4));

        if (planet.getTemperature() < Temperature.SilicatesMelt.getKelvin()) {
            planet.setTemperature(Temperature.SilicatesMelt.getKelvin());
        }
        planet.setAtmosphere(Atmosphere.Hydrogen);
        planet.setPressure(Pressure.SuperDense);

        switch (Die.d6(2)) {
            case 2: case 3:
                planet.setMagneticField(MagneticField.Standard);
                break;
            case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11:
                planet.setMagneticField(MagneticField.Strong);
                break;
            case 12:
                planet.setMagneticField(MagneticField.VeryStrong);
                break;
        }

        // Set default day length to be around 10 hours.
        planet.setDayLength(10 * 86400 + Die.die(3600, 2));

        generateFeatures(planet);

        addPrimaryResource(planet, Hydrogen);
        addTertiaryResource(planet, Helium);
        addTertiaryResource(planet, OrganicGases);
        addTertiaryResource(planet, Water);

        return planet;
    }
}
