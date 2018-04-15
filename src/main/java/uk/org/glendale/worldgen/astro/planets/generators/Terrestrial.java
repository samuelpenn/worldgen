/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.generators;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.WorldGen;
import uk.org.glendale.worldgen.astro.planets.MoonFeature;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.codes.MagneticField;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.codes.Pressure;
import uk.org.glendale.worldgen.astro.stars.Star;
import uk.org.glendale.worldgen.astro.systems.StarSystem;
import uk.org.glendale.worldgen.exceptions.UnsupportedException;

import java.util.ArrayList;
import java.util.List;

/**
 * The Terrestrial group contains Earth-sized terrestrial worlds, and include the various Gaian
 * world types which are most likely to harbour life.
 */
public class Terrestrial extends PlanetGenerator {

    public enum TerrestrialFeature implements PlanetFeature {
        VolcanicFlats,
        Pangaea,
        EquatorialOcean,
        ManyIslands,
        RedIce,
        BacterialMats,
        BorderedInBlack,
        BorderedInGreen
    }

    public Terrestrial(WorldGen worldgen, StarSystem system, Star star, Planet previous, long distance) {
        super(worldgen, system, star, previous, distance);
    }

    /**
     * Get a generated planet. Can't be called directly on the Terrestrial class, because
     * we don't know exactly what type of planet to create.
     *
     * Call getPlanet(String, PlanetType) instead.
     *
     * @param name  Name of planet to be generated.
     * @return      Always throws UnsupportedException().
     */
    public Planet getPlanet(String name) {
        throw new UnsupportedException("Must define planet type");
    }

    @Override
    public Planet getPlanet(String name, PlanetType type) {
        Planet planet = definePlanet(name, type);
        planet.setRadius(Die.die(1000, 2) + 5500);

        return planet;
    }

    protected void setTerrestrialProperties(Planet planet) {
        planet.setRadius(5500 + Die.die(1000, 2));
        planet.setAtmosphere(Atmosphere.NitrogenCompounds);
        planet.setDayLength(20 * 3600 + Die.die(7200, 4));

        // Atmospheric pressure is most likely to be Standard. Modified slightly by planet size.
        int temperature = planet.getTemperature();
        switch (Die.d6(2) + (planet.getRadius() / 1000) - 6) {
            case 1:
                planet.setPressure(Pressure.VeryThin);
                temperature *= 1.01;
                break;
            case 2: case 3: case 4:
                planet.setPressure(Pressure.Thin);
                temperature *= 1.03;
                break;
            case 5: case 6: case 7: case 8: case 9:
                planet.setPressure(Pressure.Standard);
                temperature *= 1.05;
                break;
            case 10: case 11: case 12: case 13:
                planet.setPressure(Pressure.Dense);
                temperature *= 1.10;
                break;
        }
        planet.setTemperature(temperature);

        // Magnetic field is likely to be weak or standard.
        switch (Die.d6(2)) {
            case 2:
                planet.setMagneticField(MagneticField.Minimal);
                break;
            case 3: case 4:
                planet.setMagneticField(MagneticField.VeryWeak);
                break;
            case 5: case 6: case 7:
                planet.setMagneticField(MagneticField.Weak);
                break;
            case 8: case 9: case 10: case 11: case 12:
                planet.setMagneticField(MagneticField.Standard);
                break;
        }
    }

    public List<Planet> getMoons(Planet primary) {
        List<Planet> moons = new ArrayList<>();

        Planet  moon = new Planet();
        moon.setMoonOf(primary.getId());
        moon.setName(primary.getName() + "a");
        moon.setType(PlanetType.Selenian);

        switch (Die.d6()) {
            case 1: case 2: case 3:
                // No moons.
                break;
            case 4: case 5:
                moon.setDistance((150 + Die.die(50, 2)) * 1000 + Die.die(1000));
                moon.addFeature(MoonFeature.SmallMoon);
                moon.addFeature(MoonFeature.TidallyLocked);
                moons.add(moon);
                break;
            case 6:
                moon.setDistance((300 + Die.die(100, 2)) * 1000 + Die.die(1000));
                if (Die.d3() == 1) {
                    moon.addFeature(MoonFeature.AlmostLocked);
                } else {
                    moon.addFeature(MoonFeature.TidallyLocked);
                }
                moons.add(moon);
                break;
        }

        return moons;
    }
}
