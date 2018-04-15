/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.smallbody;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.*;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;

/**
 * A Vulcanian is the only member of the Vulcanoidal Class. It is a hot asteroid close to a star.
 * They generally have very high metallic content.
 */
public class Vulcanian extends SmallBody {
    public Vulcanian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        return getPlanet(name, PlanetType.Vulcanian);
    }

    public Planet getPlanet(String name, PlanetType type) {
        Planet planet =  definePlanet(name, type);
        planet.setRadius(Die.d10(3));

        // Set default day length to be 2-3 hours.
        planet.setDayLength(3600 + Die.die(3600, 2));

        addSecondaryResource(planet, FerricOre);
        addSecondaryResource(planet, HeavyMetals);
        addSecondaryResource(planet, RareMetals);
        addSecondaryResource(planet, PreciousMetals);

        return planet;
    }
}
