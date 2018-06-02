/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.generators.terrestrial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.codes.MagneticField;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Terrestrial;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * A hot terrestrial world with a super dense atmosphere, no water or appreciable plate tectonics.
 * Such worlds are very similar to Venus.
 */
public class Cytherean extends Terrestrial {
    private static final Logger logger = LoggerFactory.getLogger(Cytherean.class);

    public Cytherean(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.Cytherean);

        // Volcanic activity.
        planet.setTemperature((int) (planet.getTemperature() * 2));
        planet.setHydrographics(0);
        planet.setPressure(Physics.STANDARD_PRESSURE * ( 70 + Die.d20(2)));
        planet.setAtmosphere(Atmosphere.CarbonDioxide);

        addFeatures(planet);

        switch (Die.d6()) {
            case 1: case 2: case 3:
                planet.setDayLength(3600 * 20 + Die.die(3600 * 4, 2));
                planet.setMagneticField(MagneticField.Standard);
                break;
            case 4: case 5:
                planet.setDayLength(3600 * 10 + Die.die(3600 * 8));
                planet.setMagneticField(MagneticField.Strong);
                break;
            case 6:
                planet.setDayLength(Physics.STANDARD_DAY + Die.die((int) Physics.STANDARD_DAY * 10));
                planet.setMagneticField(MagneticField.Weak);
                break;
        }

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addSecondaryResource(planet, SilicateOre);
        addSecondaryResource(planet, SilicateCrystals);
        addTertiaryResource(planet, CarbonicOre);
        addTertiaryResource(planet, FerricOre);

        return planet;
    }

    private void addFeatures(Planet planet) {
    }
}
