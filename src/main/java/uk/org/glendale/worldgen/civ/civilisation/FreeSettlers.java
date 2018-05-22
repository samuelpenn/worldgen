/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.civ.civilisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.civ.CivilisationFeature;
import uk.org.glendale.worldgen.civ.CivilisationGenerator;
import uk.org.glendale.worldgen.civ.Facility;

import java.util.ArrayList;
import java.util.List;

/**
 * Free Settlers are a range of anarchist or similar groups that have formed disperse communities
 * within a system. They tend to inhabit barren systems, which nobody else wants.
 */
public class FreeSettlers extends CivilisationGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FreeSettlers.class);

    public FreeSettlers(WorldGen worldGen, StarSystem system) {
        super(worldGen, system);
    }


    public void generate(CivilisationFeature... features) {
        setFeatures(features);

        for (Planet planet : system.getPlanets()) {
            List<Facility> facilities = new ArrayList<Facility>();


        }


/*
        switch (planet.getType()) {
            case DustDisc:
            case PlanetesimalDisc:
                createDustFarmers(facilities);
                break;
            case AsteroidBelt:
            case VulcanianBelt:
                createRockHermits(facilities);
                break;
            case IceBelt:
            case IceRing:
                createIceHermits(facilities);
                break;
            default:
                createRockHermits(facilities);
        }
        generateDescription(facilities);
        */
    }
}
