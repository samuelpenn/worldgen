/**
 * FerrinianMapper.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;

import java.io.File;
import java.io.IOException;

import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;

/**
 * Defines a surface map for a Ferrinian class world. These are generally grey, barren
 * and airless worlds close to their star. They are extremely hot, and heavily cratered.
 */
public class FerrinianMapper extends PlanetMapper {

    protected static final Tile CRATER = new Tile("Polar Crater", "#505050", false, 4);
    protected static final Tile DARK_GREY = new Tile("Dark Grey", "#707270", false, 3);
    protected static final Tile MID_GREY = new Tile("Mid Grey", "#747674", false, 3);
    protected static final Tile LIGHT_GREY = new Tile("Light Grey", "#7B7B78", false, 3);

    protected static final Tile RIFT = new Tile("Rift", "#404040", false, 1);


    public FerrinianMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public FerrinianMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private Tile getRandomColour() {
        switch (Die.d6(2)) {
            case 2: case 3:
                return FerrinianMapper.LIGHT_GREY;
            case 4: case 5: case 6: case 7: case 8:
                return FerrinianMapper.MID_GREY;
            case 9: case 10: case 11: case 12:
                return FerrinianMapper.DARK_GREY;
        }
        throw new IllegalStateException("getRandomColour: Invalid switch value.");
    }

    public void generate() {
        generateHeightMap(DEFAULT_FACE_SIZE, DEFAULT_FACE_SIZE);

        // Basic barren landscape.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                Tile tile = getRandomColour();
                setTile(x, y, tile);
            }
        }

        // Expand light and dark areas.
        flood(DARK_GREY, 4);
        flood(LIGHT_GREY, 6);

        // Apply craters, frequency depending on tile type.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).equals(LIGHT_GREY)) {
                    if (Die.d4() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (Die.d3() == 1) {
                    setTile(x, y, new Cratered(getTile(x, y)));
                }
            }
        }

        if (planet.hasFeature(NorthCrater)) {
            int size = getNumRows() - Die.d4();
            for (int y=0; y < size; y++) {
                for (int x = 0; x < getWidthAtY(y); x++) {
                    if (y < size - Die.d2()) {
                        setTile(x, y, CRATER);
                    }
                }
            }
        } else if (planet.hasFeature(SouthCrater)) {
            int size = (getNumRows() * 2)/3 + Die.d4();
            for (int y=getNumRows() - 1; y > size; y--) {
                for (int x = 0; x < getWidthAtY(y); x++) {
                    if (y > size + Die.d2()) {
                        setTile(x, y, CRATER);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        FerrinianMapper p = new FerrinianMapper(null,12);

        System.out.println("Ferrinian:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/ferrinian.jpg"));

    }
}
