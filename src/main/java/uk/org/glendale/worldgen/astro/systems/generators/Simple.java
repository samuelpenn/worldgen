/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.systems.generators;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.sectors.Sector;
import uk.org.glendale.worldgen.astro.stars.Luminosity;
import uk.org.glendale.worldgen.astro.stars.SpectralType;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.stars.StarGenerator;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemGenerator;
import uk.org.glendale.worldgen.astro.systems.StarSystemType;
import uk.org.glendale.worldgen.exceptions.DuplicateObjectException;

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

        updateStarSystem(createSimple(system));

        return system;
    }

    /**
     * Generates a simple predictable star system that is very similar to that of Sol.
     */
    public StarSystem createSimple(StarSystem system) throws DuplicateObjectException {
        system.setType(StarSystemType.SINGLE);

        PlanetFactory planetFactory = worldgen.getPlanetFactory();
        StarGenerator starGenerator = new StarGenerator(worldgen, system, false);

        // Generate a Sol-like star.
        Luminosity l = Luminosity.V;
        SpectralType hr = SpectralType.G2;
        switch (Die.d6(2)) {
            case 2: case 3:
                hr = SpectralType.G0;
                break;
            case 4: case 5:
                hr = SpectralType.G1;
                break;
            case 6: case 7: case 8:
                hr = SpectralType.G2;
                break;
            case 9: case 10:
                hr = SpectralType.G3;
                break;
            case 11: case 12:
                hr = SpectralType.G4;
                break;
        }

        Star primary = starGenerator.generatePrimary(l, hr);

        int orbit = 1;
        String planetName;

        planetName = factory.getPlanetName(primary, orbit++);
        switch (Die.d6(2)) {
            case 2: case 3:
                planetFactory.createPlanet(system, primary, planetName, PlanetType.VulcanianBelt,
                        30 + Die.d12());
                break;
            case 4: case 5:
                planetFactory.createPlanet(system, primary, planetName, PlanetType.Janian,
                        40 + Die.d12(2));
                break;
            case 6: case 7: case 8: case 9:
                planetFactory.createPlanet(system, primary, planetName, PlanetType.Hermian,
                        40 + Die.d12(2));
                break;
            case 10: case 11: case 12:
                planetFactory.createPlanet(system, primary, planetName, PlanetType.Ferrinian,
                        40 + Die.d8(2));
                break;
        }

        planetName = factory.getPlanetName(primary, orbit++);
        planetFactory.createPlanet(system, primary, planetName, PlanetType.Cytherean, 110);

        planetName = factory.getPlanetName(primary, orbit++);
        planetFactory.createPlanet(system, primary, planetName, PlanetType.EoGaian, 150);

        planetName = factory.getPlanetName(primary, orbit++);
        switch (Die.d6()) {
            case 1: case 2:
                planetFactory.createPlanet(system, primary, planetName, PlanetType.MesoArean, 250);
                break;
            case 3: case 4: case 5: case 6:
                planetFactory.createPlanet(system, primary, planetName, PlanetType.EuArean, 250);
                break;
        }

        planetName = factory.getPlanetName(primary, orbit++);
        planetFactory.createPlanet(system, primary, planetName, PlanetType.AsteroidBelt, 400 + Die.d20(5));

        planetName = factory.getPlanetName(primary, orbit++);
        planetFactory.createPlanet(system, primary, planetName, PlanetType.Junic, 700 + Die.d20(5));

        planetName = factory.getPlanetName(primary, orbit++);
        planetFactory.createPlanet(system, primary, planetName, PlanetType.Saturnian, 1400 + Die.d100(2));

        return system;

    }

    public void colonise(StarSystem system) {

    }
}
