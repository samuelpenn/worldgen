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
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.codes.MagneticField;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.codes.Pressure;
import uk.org.glendale.worldgen.astro.planets.generators.Jovian;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * Jovic worlds are large gas giant worlds similar to Jupiter.
 */
public class Jovic extends Jovian {
    private static final Logger logger = LoggerFactory.getLogger(Jovic.class);

    public Jovic(WorldGen worldgen, StarSystem system, Star star, Planet previous, int distance) {
        super(worldgen, system, star, previous, distance);
    }

    private void generateFeatures(Planet planet) {
        if (Die.d4() == 1) {
            planet.addFeature(Jovian.JovianFeature.WaterClouds);
        } else {
            planet.addFeature(JovianFeature.AmmoniaClouds);
        }
    }

    public Planet getPlanet(String name) {
        Planet planet =  definePlanet(name, PlanetType.Sokarian);
        planet.setRadius(45000 + Die.die(5000, 4));

        planet.setAtmosphere(Atmosphere.Hydrogen);
        planet.setPressure(Pressure.SuperDense);

        switch (Die.d6(2)) {
            case 2: case 3: case 4:
                planet.setMagneticField(MagneticField.Strong);
                break;
            case 5: case 6: case 7: case 8: case 9: case 10: case 11:
                planet.setMagneticField(MagneticField.VeryStrong);
                break;
            case 12:
                planet.setMagneticField(MagneticField.Intense);
                break;
        }

        // Set default day length to be around 10 hours.
        planet.setDayLength(9 * 86400 + Die.die(3600, 2));

        generateFeatures(planet);

        addPrimaryResource(planet, Hydrogen);
        addSecondaryResource(planet, Helium);
        addTertiaryResource(planet, OrganicGases);
        addTertiaryResource(planet, Water);

        return planet;
    }
}
