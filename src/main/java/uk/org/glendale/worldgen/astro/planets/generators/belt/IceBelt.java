/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
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
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Belt;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.astro.systems.StarSystemFactory;

import java.util.ArrayList;
import java.util.List;

import static uk.org.glendale.worldgen.astro.Physics.MKM;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.HeavyMetals;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.PreciousMetals;
import static uk.org.glendale.worldgen.astro.planets.generators.Belt.BeltFeature.Planetoids;
import static uk.org.glendale.worldgen.astro.planets.generators.Belt.BeltFeature.ThinRing;
import static uk.org.glendale.worldgen.astro.planets.generators.Belt.BeltFeature.WideSparseRing;

public class IceBelt extends Belt {
    private static final Logger logger = LoggerFactory.getLogger(IceBelt.class);

    public IceBelt(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    private void addFeatures(Planet planet) {
        switch (Die.d6(3)) {
            case 3:
                planet.addFeature(ThinRing);
                break;
            case 4: case 5: case 6: case 7: case 8:
                planet.addFeature(WideSparseRing);
                break;
            case 17: case 18:
                planet.addFeature(Planetoids);
                break;
        }
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.IceBelt);
        int radius = (int) (Die.d6(4 * 5) * MKM);
        int density = 750 + Die.dieV(250);
        planet.setTemperature(Physics.getOrbitTemperature(star, distance));

        addFeatures(planet);

        radius = checkDistance(radius);

        if (planet.hasFeature(ThinRing)) {
            radius = 25_000 + Die.die(100_000, 3);
            density *= 10;
        } else if (planet.hasFeature(WideSparseRing)) {
            int extra = radius * 6;
            distance += extra;
            density /= 10;
            planet.setDistance(distance);
        } else if (planet.hasFeature(Planetoids)) {
            radius /= 2;
            density /= 2;
        }

        planet.setRadius(radius);
        planet.setDensity(density);

        addPrimaryResource(planet, Water);
        addTertiaryResource(planet, SilicateOre);
        addTraceResource(planet, CarbonicOre);

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
            numberOfMoons += Die.d3(2);
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
                String name = StarSystemFactory.getPlanetoidName(primary.getName(), m + 1);

                logger.info("Adding moon " + name);

                // We only want to call out an asteroid as a 'moon' if it is particularly large,
                // so ensure that the planetoid generated is of sufficient size to be interesting.
                List<PlanetFeature> features = new ArrayList<PlanetFeature>();
                switch (Die.d6(2) - (primary.hasFeature(Planetoids)?2:0)) {
                    case 0:
                        // Not really an asteroid at this size.
                        features.add(SmallBody.SmallBodyFeature.GIGANTIC);
                        break;
                    case 1: case 2: case 3: case 4:
                        // About Ceres sized.
                        features.add(SmallBody.SmallBodyFeature.HUGE);
                        break;
                    default:
                        // About Vesta sized.
                        features.add(SmallBody.SmallBodyFeature.LARGE);
                        break;
                }

                PlanetType type = PlanetType.Gelidaceous;
                switch (Die.d6()) {
                    case 1:
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
