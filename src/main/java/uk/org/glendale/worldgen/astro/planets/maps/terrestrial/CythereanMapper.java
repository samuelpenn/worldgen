/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
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
import uk.org.glendale.worldgen.astro.planets.generators.Terrestrial;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.maps.TerrestrialMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CythereanMapper extends TerrestrialMapper {
    public CythereanMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    protected static final Tile LAND = new Tile("Land", "#A07050", false, 2);

    private void generateFeatures(List<PlanetFeature> features) {
    }

    public void generate() {
        super.generate();
        setWater();

        int continents = 4 + Die.d6();
        createContinents(continents);

        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                setTile(x, y, LAND);
                setTile(x, y, getTile(x, y).getShaded((getHeight(x, y) + 100) / 2));
            }
        }

        // After finishing with the height map, set it to more consistent values
        // so that the bump mapper can use it cleanly.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                int h = getHeight(x, y);
                if (h < 50) {
                    h = 25;
                } else if (h < 90) {
                    h = 50;
                } else {
                    h = 100;
                }
                setHeight(x, y, h);
            }
        }

        generateFeatures(planet.getFeatures());
        hasCloudMap = true;
    }

    /**
     * Lower cloud layer is almost completely opaque.
     */
    private SimpleImage drawLowerCloudLayer(int width) throws IOException {
        Icosahedron cloud = getCloudLayer();

        int modifier = planet.getPressure() / Physics.STANDARD_PRESSURE;
        for (int y=0; y < cloud.getNumRows(); y++) {
            for (int x=0; x < cloud.getWidthAtY(y); x++) {
                int h = cloud.getHeight(x, y);
                cloud.setHeight(x, y, modifier + h / 2);
            }
        }

        return Icosahedron.stretchImage(cloud.drawTransparency("#CFC1A4", width), width);
    }

    /**
     * Upper cloud layer is more transparent, to allow the darker lower clouds to be seen.
     */
    private SimpleImage drawUpperCloudLayer(int width) throws IOException {
        Icosahedron cloud = getCloudLayer();

        int modifier = planet.getPressure() / Physics.STANDARD_PRESSURE;
        for (int y=0; y < cloud.getNumRows(); y++) {
            for (int x=0; x < cloud.getWidthAtY(y); x++) {
                int h = cloud.getHeight(x, y);
                cloud.setHeight(x, y, (modifier + h) / 3);
            }
        }

        return Icosahedron.stretchImage(cloud.drawTransparency("#B0B8BA", width), width);
    }

    /**
     * Generate and draw the cloud layers for this world. Cytherean worlds have thick clouds,
     * so two layers of clouds are drawn. The lower layer provides complete coverage, and the
     * upper layer provides less coverage. They have subtle colour differences to make them
     * less boring.
     *
     * Cloud layers are automatically stretched before being stored.
     *
     * @param width     Width of the texture to draw.
     * @return
     */
    public List<SimpleImage> drawClouds(int width) {
        List<SimpleImage>  clouds = new ArrayList<>();

        try {
            clouds.add(drawLowerCloudLayer(width));
            clouds.add(drawUpperCloudLayer(width));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clouds;
    }

    public static void main(String[] args) throws IOException {
        Planet planet = new Planet();
        planet.setName("Foo I");
        planet.setType(PlanetType.Cytherean);
        planet.setTemperature(288);
        PlanetMapper p = new CythereanMapper(planet);

        System.out.println("Cytherean:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/cytherean.png"));
        Icosahedron.stretchImage(p.drawHeightMap(2048), 2048).save(new File("/home/sam/tmp/cytherean_h.png"));
        p.drawClouds(2048).get(0).save(new File("/home/sam/tmp/cytherean_c.png"));

    }
}
