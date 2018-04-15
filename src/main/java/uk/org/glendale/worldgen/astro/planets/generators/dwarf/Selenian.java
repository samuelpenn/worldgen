/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators.dwarf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.MoonFeature;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.codes.MagneticField;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.codes.Pressure;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.text.TextGenerator;

import static uk.org.glendale.worldgen.astro.commodities.CommodityName.*;
import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.MetallicLakes;
import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.MetallicSea;

/**
 * Selenian worlds are a subtype of Dwarf Terrestrial Lithic worlds. They are similar to our Moon.
 * Airless, rocky and barren they may harbour water ice in the shadows of craters.
 */
public class Selenian extends Dwarf {
    private static final Logger logger = LoggerFactory.getLogger(Selenian.class);

    public Selenian(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    private void addFeatures(Planet planet) {
        // No features to add at the moment.
    }

    public Planet getPlanet(String name) {
        Planet planet = definePlanet(name, PlanetType.Selenian);
        int radius = 1200 + Die.die(600);

        planet.setRadius(radius);
        planet.setAtmosphere(Atmosphere.Vacuum);
        planet.setPressure(Pressure.None);
        planet.setMagneticField(MagneticField.None);

        addFeatures(planet);

        if (planet.hasFeature(MoonFeature.SmallMoon)) {
            planet.setRadius(planet.getRadius() / 2);
        } else if (planet.hasFeature(MoonFeature.LargeMoon)) {
            planet.setRadius((int)(planet.getRadius() * 1.5));
        }

        TextGenerator text = new TextGenerator(planet);
        planet.setDescription(text.getFullDescription());

        // Define resources for this world.
        addSecondaryResource(planet, SilicateOre);
        addTertiaryResource(planet, SilicateCrystals);

        return planet;
    }}
