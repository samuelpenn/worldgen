/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems.generators;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.*;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemGenerator;
import uk.org.glendale.worldgen.astro.systems.StarSystemType;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

import static uk.org.glendale.worldgen.astro.systems.StarSystemFactory.getPlanetName;
import static uk.org.glendale.worldgen.astro.systems.StarSystemFactory.getBeltName;

import java.util.List;

/**
 * Creates a simple star system, similar to that of Sol. This is primarily for use in testing boring
 * and predictable systems that don't have much variety.
 */
public class Simple extends StarSystemGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Simple.class);

    public Simple(WorldGen worldgen) {
        super(worldgen);
    }

    public StarSystem generate(Sector sector, String name, int x, int y) throws DuplicateObjectException {
        StarSystem system = createEmptySystem(sector, name, x, y);

        switch (Die.d6(2)) {
            case 2:
                createSol(system);
                break;
            default:
                createSingleStar(system);
                break;
        }
        updateStarSystem(system);

        return system;
    }

    @SuppressWarnings("WeakerAccess")
    public void createSol(StarSystem system) throws DuplicateStarException {
        system.setType(StarSystemType.SINGLE);
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        List<Planet> planets;

        // Generate a Sol-like star.
        Star primary;
        switch (Die.d6(1)) {
            case 1:
                // Hotter.
                primary = starGenerator.generatePrimary(Luminosity.V, SpectralType.G1);
                break;
            case 6:
                // Cooler.
                primary = starGenerator.generatePrimary(Luminosity.V, SpectralType.G3);
                break;
            default:
                primary = starGenerator.generatePrimary(Luminosity.V, SpectralType.G2);
                break;
        }

        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        String  planetName;
        int     orbit = 1;

        // Mercury.
        long distance = 50 * Physics.MKM + Die.dieV(5_000_000);
        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Hermian, distance);
        system.addPlanets(planets);

        // Venus.
        distance = 110 * Physics.MKM + Die.dieV(5_000_000);
        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Cytherean, distance);
        system.addPlanets(planets);

        // Earth.
        distance = 150 * Physics.MKM + Die.dieV(5_000_000);
        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.EoGaian, distance);
        system.addPlanets(planets);

        // Mars.
        distance = 230 * Physics.MKM + Die.dieV(5_000_000);
        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.EuArean, distance);
        system.addPlanets(planets);

        // Asteroids.
        distance = 400 * Physics.MKM + Die.dieV(10_000_000);
        planetName = getBeltName(primary, 1);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt, distance);
        system.addPlanets(planets);

        // Jupiter.
        distance = 750 * Physics.MKM + Die.dieV(30_000_000);
        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Jovic, distance);
        system.addPlanets(planets);

        // Saturn.
        distance = 1500 * Physics.MKM + Die.dieV(100_000_000);
        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Saturnian, distance);
        system.addPlanets(planets);

        setDescription(system, null);
    }

    /**
     * Generates a simple predictable star system that is very similar to that of Sol.
     */
    @SuppressWarnings("WeakerAccess")
    public void createSingleStar(StarSystem system) throws DuplicateObjectException {
        system.setType(StarSystemType.SINGLE);

        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);
        List<Planet> planets;

        // Generate a Sol-like star.
        Luminosity l = Luminosity.V;
        SpectralType hr = SpectralType.G2.getSpectralType(Die.dieV(3));
        Star primary = starGenerator.generatePrimary(l, hr);

        int orbit = 1;
        int belts = 1;
        String planetName;

        switch (Die.d6(2)) {
            case 4:
                planetName = getBeltName(primary, belts++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.VulcanianBelt,
                        (30 + Die.d12()) * Physics.MKM);
                system.addPlanets(planets);
                break;
            case 5:
                planetName = getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Janian,
                        (40 + Die.d12(2)) * Physics.MKM);
                system.addPlanets(planets);
                break;
            case 6: case 7: case 8:
                planetName = getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Hermian,
                        (40 + Die.d12(2)) * Physics.MKM);
                system.addPlanets(planets);
                break;
            case 9: case 10:
                planetName = getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Ferrinian,
                        (40 + Die.d8(2)) * Physics.MKM);
                system.addPlanets(planets);
                break;
            default:
                // No planet.
        }

        if (Die.d3() > 1) {
            planetName = getPlanetName(primary, orbit++);
            planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Cytherean,
                    (70 + Die.d10(2) + orbit * 15) * Physics.MKM);
            system.addPlanets(planets);
        }

        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.EoGaian,
                (140 + Die.d10(2)) * Physics.MKM);
        system.addPlanets(planets);

        switch (Die.d6()) {
            case 1: case 2:
                planetName = getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.MesoArean,
                        (230 + Die.d20(2)) * Physics.MKM);
                system.addPlanets(planets);
                break;
            case 3: case 4: case 5:
                planetName = getPlanetName(primary, orbit++);
                planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.EuArean,
                        (230 + Die.d20(2)) * Physics.MKM);
                system.addPlanets(planets);
                break;
            default:
                // No planet.
        }

        planetName = getBeltName(primary, belts++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt,
                (400 + Die.d20(5)) * Physics.MKM);
        system.addPlanets(planets);

        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Junic,
                (700 + Die.d100(1)) * Physics.MKM);
        system.addPlanets(planets);

        planetName = getPlanetName(primary, orbit++);
        planets = planetFactory.createPlanet(system, primary, planetName, PlanetType.Saturnian,
                (1400 + Die.d100(2)) * Physics.MKM);
        system.addPlanets(planets);

        setDescription(system, null);

    }

    public void colonise(StarSystem system) {

    }
}
