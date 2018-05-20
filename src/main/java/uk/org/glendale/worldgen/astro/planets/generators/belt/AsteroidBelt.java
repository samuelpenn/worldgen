/**
 * AsteroidBelt.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.generators.belt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFactory;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.codes.Pressure;
import uk.org.glendale.worldgen.astro.planets.generators.Belt;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemFactory;
import uk.org.glendale.worldgen.astro.systems.generators.Simple;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.util.ArrayList;
import java.util.List;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.Physics.MKM;
import static uk.org.glendale.worldgen.astro.planets.generators.Belt.BeltFeature.*;

/**
 * Generates an asteroid belt. Mostly rocky, with some volatiles.
 */
public class AsteroidBelt extends Belt {
    private static final Logger logger = LoggerFactory.getLogger(AsteroidBelt.class);

    public AsteroidBelt(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    private void addFeatures(Planet planet) {
        switch (Die.d6(3)) {
            case 3: case 4:
                planet.addFeature(ThinRing);
                break;
            case 5: case 6:
                planet.addFeature(WideSparseRing);
                break;
            case 7: case 8:
                planet.addFeature(Planetoids);
                break;
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.AsteroidBelt);
        int radius = (int) (Die.d6(4 * 5) * MKM);
        int density = 1000 + Die.dieV(250);
        planet.setTemperature(Physics.getOrbitTemperature(star, distance));

        addFeatures(planet);

        radius = checkDistance(radius);

        if (planet.hasFeature(ThinRing)) {
            radius = 20_000 + Die.die(100_000, 3);
            density *= 10;
        } else if (planet.hasFeature(WideSparseRing)) {
            int extra = radius * 5;
            distance += extra;
            density /= 10;
            planet.setDistance(distance);
        } else if (planet.hasFeature(Planetoids)) {
            radius /= 2;
            density /= 3;
        }

        planet.setRadius(radius);
        planet.setDensity(density);

        addPrimaryResource(planet, SilicateOre);
        addPrimaryResource(planet, CarbonicOre);
        addSecondaryResource(planet, FerricOre);
        addTertiaryResource(planet, Water);
        addTertiaryResource(planet, HeavyMetals);
        addTertiaryResource(planet, PreciousMetals);

        return planet;
    }

    /**
     * Gets a list of moons for this belt. In a belt, a 'moon' is a significantly larger
     * asteroid that is part of the main belt, rather than something that orbits the
     * primary.
     *
     * @param primary   Belt the moons belong to.
     * @return          Array of planets (moons), may be empty.
     */
    public List<Planet> getMoons(Planet primary, PlanetFactory factory) {
        List<Planet> moons = new ArrayList<Planet>();

        int numberOfMoons = Die.d4() - 1;
        if (primary.hasFeature(Planetoids)) {
            numberOfMoons += Die.d4(3);
        }
        logger.info(String.format("Creating %d moons for [%s]", numberOfMoons, primary.getName()));
        if (numberOfMoons > 0) {
            long variance = primary.getRadius() / numberOfMoons;
            long distance = 0 - primary.getRadius();
            switch (numberOfMoons) {
                case 1:
                    distance = Die.dieV(primary.getRadius());
                    variance = primary.getRadius() / 100;
                    break;
                case 2:
                    distance = 0 - Die.die(primary.getRadius());
                    variance = primary.getRadius() / 2;
                    break;
                default:
                    // As above.
            }

            for (int m=0; m < numberOfMoons; m++) {
                String name = StarSystemFactory.getMoonName(primary.getName(), m + 1);

                logger.info("Adding moon " + name);

                // We only want to call out an asteroid as a 'moon' if it is particularly large,
                // so ensure that the planetoid generated is of sufficient size to be interesting.
                List<PlanetFeature> features = new ArrayList<PlanetFeature>();
                switch (Die.d6(2) - (primary.hasFeature(Planetoids)?2:0)) {
                    case 0: case 1:
                        // Not really an asteroid at this size.
                        features.add(SmallBody.SmallBodyFeature.GIGANTIC);
                        break;
                    case 2: case 3: case 4: case 5:
                        // About Ceres sized.
                        features.add(SmallBody.SmallBodyFeature.HUGE);
                        break;
                    default:
                        // About Vesta sized.
                        features.add(SmallBody.SmallBodyFeature.LARGE);
                        break;
                }

                PlanetType type = PlanetType.Carbonaceous;
                switch (Die.d6()) {
                    case 1: case 2: case 3:
                        type = PlanetType.Carbonaceous;
                        break;
                    case 4: case 5: case 6:
                        type = PlanetType.Silicaceous;
                        break;
                }

                Planet moon = factory.createMoon(system, star, name, type,
                        distance + Die.dieV((int) (variance / 5)), primary,
                         features.toArray(new PlanetFeature[0]));

                moons.add(moon);
                distance += variance;
            }
        }

        return moons;
    }
}
