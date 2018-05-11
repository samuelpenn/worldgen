/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.civ.civilisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.generators.BrownDwarf;
import uk.org.glendale.worldgen.civ.CivilisationFeature;
import uk.org.glendale.worldgen.civ.CivilisationGenerator;
import uk.org.glendale.worldgen.civ.Facility;
import uk.org.glendale.worldgen.civ.facility.residential.RockHermits;
import uk.org.glendale.worldgen.civ.facility.starport.RamshackleDocks;

/**
 * Rock Hermits live on asteroids or similar space based debris. They are generally disorganised and low tech,
 * barely scraping by, and deliberately avoiding building up their civilisation.
 */
public class Hermits extends CivilisationGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Hermits.class);

    public Hermits(WorldGen worldGen, StarSystem system, Planet planet) {
        super(worldGen, system, planet);
    }

    public void generate(CivilisationFeature... features) {
        setFeatures(features);

        switch (planet.getType()) {
            case DustDisc:
            case PlanetesimalDisc:
                createDustHermits();
                break;
            case AsteroidBelt:
            case VulcanianBelt:
                createRockHermits();
                break;
            case IceBelt:
            case IceRing:
                createIceHermits();
                break;
            default:
                createRockHermits();
        }
    }

    public void createIceHermits(CivilisationFeature... features) {
        setFeatures(features);
        planet.setTechLevel(4 + Die.d2());

        if (hasFeature(CivilisationFeature.LARGE_POPULATION)) {
            planet.setPopulation(Die.d12(10));
        } else if (hasFeature(CivilisationFeature.HUGE_POPULATION)) {
            planet.setPopulation(Die.d20(20));
            planet.setTechLevel(6);
        } else {
            planet.setPopulation(Die.d6(5));
        }

        Facility residential = new RockHermits(planet).getFacility();
        if (hasFeature(CivilisationFeature.POOR)) {
            residential.setRating(residential.getRating() - 25);
        } else if (hasFeature(CivilisationFeature.RICH)) {
            residential.setRating(residential.getRating() + 25);
        }
    }

    public void createDustHermits(CivilisationFeature... features) {
        logger.info("Creating dust hermits");
        setFeatures(features);
        planet.setTechLevel(4 + Die.d2());

        if (hasFeature(CivilisationFeature.SMALL_POPULATION)) {
            planet.setPopulation(Die.d6(5));
        } else if (hasFeature(CivilisationFeature.LARGE_POPULATION)) {
            planet.setPopulation(Die.d12(10) * 5);
        } else if (hasFeature(CivilisationFeature.HUGE_POPULATION)) {
            planet.setPopulation(Die.d20(20) * 10);
            planet.setTechLevel(6);
        } else {
            planet.setPopulation(Die.d6(10));
        }

        Facility residential = new RockHermits(planet).getFacility();
        if (hasFeature(CivilisationFeature.POOR)) {
            residential.setRating(residential.getRating() - 25);
        } else if (hasFeature(CivilisationFeature.RICH)) {
            residential.setRating(residential.getRating() + 25);
        }

        logger.debug("Setting residential");
        worldGen.getPlanetFactory().setFacility(residential);

        Facility port = new RamshackleDocks(planet).getFacility();
        worldGen.getPlanetFactory().setFacility(port);
    }

    public void createRockHermits(CivilisationFeature... features) {
        setFeatures(features);

        planet.setTechLevel(4 + Die.d2());

        if (hasFeature(CivilisationFeature.LARGE_POPULATION)) {
            planet.setPopulation(Die.d12(12));
        } else if (hasFeature(CivilisationFeature.HUGE_POPULATION)) {
            planet.setPopulation(Die.d20(24));
            planet.setTechLevel(6);
        } else {
            planet.setPopulation(Die.d6(6));
        }

        Facility residential = new RockHermits(planet).getFacility();
        if (hasFeature(CivilisationFeature.POOR)) {
            residential.setRating(residential.getRating() - 25);
        } else if (hasFeature(CivilisationFeature.RICH)) {
            residential.setRating(residential.getRating() + 25);
        }

    }
}
