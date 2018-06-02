/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.terrestrial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.*;
import uk.org.glendale.worldgen.astro.planets.generators.Terrestrial;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * EoGaian worlds are Tectonic Terrestrial worlds similar to an early Earth.
 */
public class EoGaian extends Terrestrial {
    private static final Logger logger = LoggerFactory.getLogger(EoGaian.class);

    public EoGaian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.EoGaian);

        setTerrestrialProperties(planet);

        // Volcanic activity.
        planet.setTemperature((int) (planet.getTemperature() * 1.05));
        planet.setHydrographics(Die.d20(2));

        switch (Die.d6(2)) {
            case 2: case 3:
                planet.setLife(Life.None);
                addPrimaryResource(planet, OrganicChemicals);
                break;
            case 4: case 5:
                planet.setLife(Life.Organic);
                addPrimaryResource(planet, OrganicChemicals);
                addSecondaryResource(planet, Protobionts);
                break;
            case 6: case 7:
                planet.setLife(Life.Organic);
                addSecondaryResource(planet, OrganicChemicals);
                addPrimaryResource(planet, Protobionts);
                break;
            case 8: case 9:
                planet.setLife(Life.Organic);
                addSecondaryResource(planet, OrganicChemicals);
                addPrimaryResource(planet, Protobionts);
                addTraceResource(planet, Prokaryotes);
                break;
            case 10:
                planet.setLife(Life.Archaean);
                addTertiaryResource(planet, OrganicChemicals);
                addSecondaryResource(planet, Protobionts);
                addPrimaryResource(planet, Prokaryotes);
                break;
            case 11:
                planet.setLife(Life.Archaean);
                addTertiaryResource(planet, OrganicChemicals);
                addTertiaryResource(planet, Protobionts);
                addPrimaryResource(planet, Prokaryotes);
                addTraceResource(planet, Cyanobacteria);
                break;
            case 12:
                planet.setLife(Life.Archaean);
                addTertiaryResource(planet, Protobionts);
                addPrimaryResource(planet, Prokaryotes);
                addTertiaryResource(planet, Cyanobacteria);
                break;
        }

        addFeatures(planet);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addSecondaryResource(planet, SilicateOre);
        addSecondaryResource(planet, SilicateCrystals);
        addSecondaryResource(planet, CarbonicOre);
        addTertiaryResource(planet, FerricOre);

        addPrimaryResource(planet, OrganicGases);
        addPrimaryResource(planet, Water);


        return planet;
    }

    private void addFeatures(Planet planet) {
        switch (Die.d6(3)) {
            case 3: case 4:
                planet.addFeature(TerrestrialFeature.BacterialMats);
                break;
            case 5:
                planet.addFeature(TerrestrialFeature.BorderedInBlack);
                break;
            case 6:
                planet.addFeature(TerrestrialFeature.BorderedInGreen);
                break;
            case 7:
                planet.addFeature(TerrestrialFeature.VolcanicFlats);
                break;
        }
    }
}
