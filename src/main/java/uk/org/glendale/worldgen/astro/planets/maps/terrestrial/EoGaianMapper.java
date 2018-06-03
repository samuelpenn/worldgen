/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.terrestrial;

import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.generators.Terrestrial;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.maps.TerrestrialMapper;
import uk.org.glendale.worldgen.astro.planets.maps.dwarf.HermianMapper;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.org.glendale.worldgen.astro.planets.generators.Terrestrial.TerrestrialFeature.*;

public class EoGaianMapper extends TerrestrialMapper {
    public EoGaianMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }


    private void generateFeatures(List<PlanetFeature> features) {
        if (features.contains(Terrestrial.TerrestrialFeature.BacterialMats)) {
            Tile BACTERIAL_MAT = new Tile("Bacterial Mat", "#203020", true);

            for (int y=0; y < getNumRows(); y++) {
                for (int x=0; x < getWidthAtY(y); x++) {
                    if (getTile(x, y).isWater() && getHeight(x, y) + getLatitude(y) < 60) {
                        setTile(x, y, BACTERIAL_MAT);
                    }
                }
            }
        }
    }

    public void generate() {
        super.generate();
        setWater();

        int continents = 4 + Die.d6();
        createContinents(continents);

        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).isWater()) {
                    setTile(x, y, getTile(x, y).getShaded((getHeight(x, y) + 200) / 3));
                } else {
                    setTile(x, y, getTile(x, y).getShaded((getHeight(x, y) + 100) / 2));
                }
            }
        }
        setIceCaps();

        // After finishing with the height map, set it to more consistent values
        // so that the bump mapper can use it cleanly.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).isWater()) {
                    setHeight(x, y, 0);
                } else {
                    int h = getHeight(x, y);
                    if (h < 90) {
                        h = 50;
                    } else {
                        h = 100;
                    }
                    setHeight(x, y, h);
                }
            }
        }
        createCraters(0, 50);

        generateFeatures(planet.getFeatures());

        // Mark world as having clouds.
        hasCloudMap = true;
    }

    public List<SimpleImage> drawClouds(int width) {
        List<SimpleImage>  clouds = new ArrayList<>();

        Icosahedron cloud = getCloudLayer();

        int cloudLimit = 50;
        if (planet.hasFeature(Dry)) {
            cloudLimit = 75;
        } else if (planet.hasFeature(Wet)) {
            cloudLimit = 30;
        }
        for (int y=0; y < cloud.getNumRows(); y++) {
            for (int x=0; x < cloud.getWidthAtY(y); x++) {
                int h = cloud.getHeight(x, y);
                if (h < cloudLimit) {
                    cloud.setHeight(x, y, 0);
                }
            }
        }

        try {
            String cloudColour = "#F0F0F0";
            if (planet.hasFeature(Volcanoes) || planet.hasFeature(VolcanicFlats)) {
                cloudColour = "#808080";
            }
            clouds.add(Icosahedron.stretchImage(cloud.drawTransparency("#F0F0F0", width), width));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clouds;
    }

    public static void main(String[] args) throws IOException {
        Planet planet = new Planet();
        planet.setName("Foo I");
        planet.setType(PlanetType.EoGaian);
        planet.setTemperature(288);
        planet.addFeature(Terrestrial.TerrestrialFeature.BacterialMats);
        PlanetMapper p = new EoGaianMapper(planet);

        System.out.println("EoGaian:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/eogaian.jpg"));

    }
}
