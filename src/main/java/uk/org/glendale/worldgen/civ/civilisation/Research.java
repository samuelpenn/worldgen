/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.civ.civilisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.civ.CivilisationFeature;
import uk.org.glendale.worldgen.civ.CivilisationGenerator;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.facility.science.ResearchStation;
import uk.org.glendale.worldgen.civ.facility.residential.SmallSpaceStation;
import uk.org.glendale.worldgen.civ.facility.starport.SmallDocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Research stations are orbital facilities that perform scientific monitoring and research.
 */
public class Research extends CivilisationGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Research.class);

    public Research(WorldGen worldGen, StarSystem system) {
        super(worldGen, system);
    }

    public void generate(CivilisationFeature... features) {
        setFeatures(features);

        for (Planet planet : system.getPlanets()) {
            List<Facility> facilities = new ArrayList<Facility>();

            switch (planet.getType()) {
                case DustDisc:
                case PlanetesimalDisc:
                case AsteroidBelt:
                case IceBelt:
                case IceRing:
                    createMonitorStation(planet, facilities);
                    break;
                case VulcanianBelt:
                    break;
            }
            generateDescription(planet, facilities);
        }
    }

    /**
     * A research and monitoring station, for scientific purposes.
     *
     * @param facilities    List of facilities to be updated.
     * @param features      Optional list of features.
     */
    private void createMonitorStation(final Planet planet, final List<Facility> facilities, CivilisationFeature... features) {
        logger.info("Creating Monitor Station");
        setFeatures(features);
        planet.setTechLevel(7 + Die.d2());

        long population = Die.d6(5);
        planet.setPopulation(Physics.round(population));

        Facility residential = new SmallSpaceStation(planet).getFacility();
        if (hasFeature(CivilisationFeature.POOR)) {
            residential.setRating(residential.getRating() - 15);
            planet.setTechLevel(planet.getTechLevel() - 1);
            planet.setLawLevel(planet.getLawLevel() - 1);
        } else if (hasFeature(CivilisationFeature.RICH)) {
            residential.setRating(residential.getRating() + 15);
            planet.setTechLevel(planet.getTechLevel() + 1);
        }

        logger.debug("Setting residential");
        worldGen.getPlanetFactory().setFacility(residential);
        facilities.add(residential);

        Facility port = new SmallDocks(planet).getFacility();
        worldGen.getPlanetFactory().setFacility(port);
        facilities.add(port);

        Facility research = new ResearchStation(planet).getFacility();
        worldGen.getPlanetFactory().setFacility(research);
        facilities.add(research);
    }
}
