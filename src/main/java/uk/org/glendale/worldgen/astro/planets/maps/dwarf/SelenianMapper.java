/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Selenian worlds are barren, grey, rocky worlds. They have darker 'seas' of recent lava plains.
 */
public class SelenianMapper extends PlanetMapper {
    protected static final Tile MARIA = new Tile("Seas", "#404040", false, 2);
    protected static final Tile HIGHLANDS = new Tile("Highlands", "#8B8B88", false, 2);

    public SelenianMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public SelenianMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    public void generate() {

        generateHeightMap(24, DEFAULT_FACE_SIZE);
        int seaLevel = getSeaLevel(5);

        // Basic barren landscape.
        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
                int h = getHeight(tileX, tileY);
                if (h <= seaLevel) {
                    setTile(tileX, tileY, MARIA);
                } else {
                    setTile(tileX, tileY, HIGHLANDS.getShaded(50 + (h / 2)));
                }
            }
        }

        // Flood the maria out to their final coverage. Then set the height shading on them.
        // This ensures that the height shading is consistent between original maria seeds
        // and the flooded areas.
        floodToPercentage(MARIA, 20, false);
        growBorder(MARIA, 2, 2);
        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
                int h = getHeight(tileX, tileY);
                if (getTile(tileX, tileY).equals(MARIA)) {
                    setTile(tileX, tileY, MARIA.getShaded(75 + h/2));
                }
            }
        }


    }

    public static void main(String[] args) throws IOException {
        Planet planet = new Planet();
        planet.setName("Foo I");
        planet.setType(PlanetType.Selenian);
        SelenianMapper p = new SelenianMapper(planet);

        System.out.println("Selenian:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/selenian.png"));

        TextGenerator tg = new TextGenerator(planet);
        System.out.println(tg.getFullDescription());

    }
}
