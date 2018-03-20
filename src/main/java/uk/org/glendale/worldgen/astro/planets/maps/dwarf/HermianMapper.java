/**
 * HermianMapper.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.codes.PlanetType;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.maps.DwarfMapper;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.maps.SmallBodyMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;
import uk.org.glendale.worldgen.text.TextGenerator;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;
import static uk.org.glendale.worldgen.astro.planets.maps.dwarf.AreanMapper.DEFAULT_FACE_SIZE;

/**
 * Defines a surface map for a Hermian class world. These are generally grey, barren
 * and airless worlds close to their star. They are extremely hot, and heavily cratered.
 */
public class HermianMapper extends DwarfMapper {

    protected static final Tile DARK_GREY = new Tile("Dark Grey", "#808280", false, 2);
    protected static final Tile MID_GREY = new Tile("Mid Grey", "#848684", false, 2);
    protected static final Tile LIGHT_GREY = new Tile("Light Grey", "#8B8B88", false, 2);

    protected static final Tile RIFT = new Tile("Rift", "#404040", false, 1);
    protected static final Tile SILVER = new Tile("Silver", "#D0D0D0", true, 1);

    protected static final Tile CRATER_RIDGE = new Tile("Ridge", "#A0A0A0", false, 2);
    protected static final Tile CRATER_FLOOR = new Tile("Floor", "#606060", false, 5);


    public HermianMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public HermianMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private Tile getRandomColour(int height) {
        if (planet.hasFeature(ReMelted)) {
            if (Die.d6(2) == 2) {
                return LIGHT_GREY;
            } else {
                return DARK_GREY;
            }
        } else {
            switch (Die.d4(2) + height / 24) {
                case 2:
                    return HermianMapper.LIGHT_GREY;
                case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                    return HermianMapper.MID_GREY;
                case 10: case 11: case 12:
                    return HermianMapper.DARK_GREY;
            }
            throw new IllegalStateException("getRandomColour: Invalid switch value.");
        }
    }

    /**
     * Generate a Hermian surface landscape. This will be grey, barren and cratered.
     */
    public void generate() {
        super.generate();

        // Basic barren landscape.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                Tile tile = getRandomColour(getHeight(x, y));
                setTile(x, y, tile);
            }
        }

        // Expand light and dark areas.
        if (planet.hasFeature(ReMelted)) {
            flood(DARK_GREY, 1);
            flood(LIGHT_GREY, 3);
        } else {
            flood(DARK_GREY, 3);
            flood(LIGHT_GREY, 7);
        }

        // Apply craters, frequency depending on tile type.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).equals(LIGHT_GREY)) {
                    if (Die.d3() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (Die.d2() == 1 && !planet.hasFeature(ReMelted)) {
                    setTile(x, y, new Cratered(getTile(x, y)));
                } else if (Die.d6() == 1) {
                    setTile(x, y, new Cratered(getTile(x, y)));
                }
            }
        }

        // After finishing with the height map, set it to more consistent values
        // so that the bump mapper can use it cleanly.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                int h = getHeight(x, y);
                if (h < 21) {
                    h = 10;
                } else if (h < 90) {
                    h = 50;
                } else {
                    h = 100;
                }
                setHeight(x, y, h);
            }
        }
        createCraters(0, 250);

        if (planet != null) {
            List<PlanetFeature> features = planet.getFeatures();
            if (features != null && features.size() != 0) {
                generateFeatures(features);
            }
        }
    }

    private void generateMajorCrater(PlanetFeature crater) {
        int startY = 0;
        int direction = 1;

        if (crater == SouthCrater) {
            startY = getNumRows() - 1;
            direction = -1;
        }

        int tileY = startY;
        for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
            setTile(tileX, tileY, CRATER_RIDGE);
        }
        // Fill in the floor of the crater down to the edge.
        int craterEdge = startY + (getNumRows()/3 + Die.dieV(3)) * direction;
        while (tileY != craterEdge) {
            tileY += direction;
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                setTile(tileX, tileY, CRATER_FLOOR);
                if (Die.d6() == 1) {
                    setTile(tileX, tileY, new Cratered(getTile(tileX, tileY)));
                }
            }
        }
        // Main ridge around edge of crater.
        for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
            if (Die.d6() == 1) {
                setTile(tileX, tileY, LIGHT_GREY);
            } else {
                setTile(tileX, tileY, CRATER_RIDGE);
            }
        }
        if (Die.d2() == 1) {
            // Place a mini-ridge around the mid-point.
            tileY = (startY + tileY) / 2 + Die.dieV(2);
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                if (Die.d3() == 1) {
                    setTile(tileX, tileY, DARK_GREY);
                } else {
                    setTile(tileX, tileY, MID_GREY);
                }
            }
        }

    }

    private void generateFeatures(List<PlanetFeature> features) {
        for (PlanetFeature f : features) {
            if (f == GreatRift) {
                // A single rift split across the world.
                addRift(RIFT,8 + Die.d6(2));
            } else if (f == BrokenRifts) {
                // A number of small rifts in the surface of the world.
                int numRifts = 6 + Die.d4(2);
                for (int r = 0; r < numRifts; r++) {
                    addRift(RIFT,6 + Die.d4(2));
                }
            } else if (f == MetallicSea) {
                // A sea of molten metal, generally equatorial.
                int y = getNumRows() / 2 + Die.dieV(3);
                int x = getWidthAtY(y) / 2 + Die.dieV(2);
                setTile(x, y, SILVER);
                flood(SILVER, 7);
            } else if (f == NorthCrater || f == SouthCrater) {
                generateMajorCrater(f);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Planet planet = new Planet();
        planet.setName("Foo I");
        planet.setType(PlanetType.Hermian);
        planet.addFeature(Dwarf.DwarfFeature.ReMelted);
        HermianMapper p = new HermianMapper(planet);

        System.out.println("Hermian:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/hermian.png"));

        TextGenerator tg = new TextGenerator(planet);
        System.out.println(tg.getFullDescription());

    }
}
