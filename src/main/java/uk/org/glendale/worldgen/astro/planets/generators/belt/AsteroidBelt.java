/**
 * AsteroidBelt.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.generators.belt;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Belt;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.util.ArrayList;
import java.util.List;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.Physics.MKM;

/**
 * Generates an asteroid belt. Mostly rocky, with some volatiles.
 */
public class AsteroidBelt extends Belt {

    public AsteroidBelt(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.AsteroidBelt);
        int radius = (int) (Die.d6(4 * 5) * MKM);

        radius = checkDistance(radius);
        planet.setRadius(radius);

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        addPrimaryResource(planet, SilicateOre);
        addPrimaryResource(planet, CarbonicOre);
        addSecondaryResource(planet, FerricOre);
        addTertiaryResource(planet, Water);
        addTertiaryResource(planet, HeavyMetals);
        addTertiaryResource(planet, PreciousMetals);

        return planet;
    }

    private Planet getMoon() {
        return null;
    }

    /**
     * Gets a list of moons for this belt. In a belt, a 'moon' is a significantly larger
     * asteroid that is part of the main belt, rather than something that orbits the
     * primary.
     *
     * @param primary   Belt the moons belong to.
     * @return          Array of planets, may be empty.
     */
    public List<Planet> getMoons(Planet primary) {
        List<Planet> moons = new ArrayList<Planet>();

        int numberOfMoons = Die.d4() - 1;
        if (numberOfMoons > 0) {
            long variance = primary.getRadius() / numberOfMoons;
            long distance = primary.getRadius();
            switch (numberOfMoons) {
                case 1:
                    distance = Die.dieV(primary.getRadius());
                    variance = 0;
                    break;
                case 2:
                    distance = 0 - Die.die(primary.getRadius());
                    break;
            }
        }

        return moons;
    }
}
