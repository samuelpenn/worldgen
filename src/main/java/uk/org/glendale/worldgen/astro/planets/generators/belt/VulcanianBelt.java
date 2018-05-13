/**
 * VulcanianBelt.java
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

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.HeavyMetals;
import static uk.org.glendale.worldgen.astro.commodities.CommodityName.PreciousMetals;
import static uk.org.glendale.worldgen.astro.Physics.MKM;

/**
 * Rocky asteroid belt close to its star. Tends to have very few volatiles, but high in heavy metals.
 */
public class VulcanianBelt extends Belt {

    public VulcanianBelt(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.VulcanianBelt);

        // Radius of a Belt is in millions of km, and represents its width.
        int radius = (int) (Die.d6(3) * MKM);

        if (distance < 10) {
            radius = Die.d4(2);
            if (radius > distance) {
                distance = radius + Die.d2();
            }
        } else if (distance < 25) {
            radius = Die.d6(3);
        } else if (distance < 100) {
            radius = Die.d6(4);
        }

        radius = checkDistance(radius);
        planet.setRadius(radius);

        addPrimaryResource(planet, SilicateOre);
        addPrimaryResource(planet, CarbonicOre);
        addSecondaryResource(planet, FerricOre);
        addSecondaryResource(planet, HeavyMetals);
        addTertiaryResource(planet, PreciousMetals);
        addTertiaryResource(planet, Radioactives);

        return planet;
    }
}
