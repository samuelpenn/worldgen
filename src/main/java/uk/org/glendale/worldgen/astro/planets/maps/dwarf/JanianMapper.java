/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.codes.Atmosphere;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;

/**
 * Tidally locked world.
 */
public class JanianMapper extends PlanetMapper {
    protected static final Tile DARK_GREY = new Tile("Dark Grey", "#808280", false, 2);
    protected static final Tile MID_GREY = new Tile("Mid Grey", "#848684", false, 2);
    protected static final Tile LIGHT_GREY = new Tile("Light Grey", "#908B88", false, 2);

    protected static final Tile RIFT = new Tile("Rift", "#404040", false, 1);
    protected static final Tile SILVER = new Tile("Silver", "#1E1F12", true, 1);
    protected static final Tile ICE = new Tile("Ice", "#E0E0E0", false, 1);


    public JanianMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public JanianMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    /**
     * Such worlds are slightly darker on the star side. No particular reason, it just gives them
     * a different look.
     *
     * @param x     X coordinate of this tile.
     * @param y     Y coordinate of this tile.
     * @return      Type of tile to place.
     */
    private Tile getRandomColour(int x, int y) {
        int midPoint = getWidthAtY(y) / 2;
        int modifier = 0;

        if (x < midPoint * 0.20) {
            modifier = -2;
        } else if (x < midPoint * 0.40) {
            modifier = -1;
        } else if (x < midPoint * 0.60) {
            modifier = +0;
        } else if (x < midPoint * 0.80) {
            modifier = +1;
        } else if (x < midPoint * 1.20) {
            modifier = +2;
        } else if (x < midPoint * 1.40) {
            modifier = +1;
        } else if (x < midPoint * 1.60) {
            modifier = +0;
        } else if (x < midPoint * 1.80) {
            modifier = -1;
        } else {
            modifier = -2;
        }

        switch (Die.d6(2) + modifier) {
            case 0: case 1: case 2: case 3: case 4:
                return DARK_GREY;
            case 5: case 6: case 7: case 8: case 9:
                return MID_GREY;
            case 10: case 11: case 12: case 13: case 14:
                return LIGHT_GREY;
        }
        throw new IllegalStateException("getRandomColour: Invalid switch value.");
    }

    /**
     * Generate a Hermian surface landscape. This will be grey, barren and cratered.
     */
    public void generate() {
        // Basic barren landscape.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                Tile tile = getRandomColour(x, y);
                setTile(x, y, tile);
            }
        }

        // Expand light and dark areas.
        flood(DARK_GREY, 2);
        flood(LIGHT_GREY, 2);

        if (planet.hasFeature(NightsideIce)) {
            int y = getNumRows() / 2;
            int x = getWidthAtY(y) / 2;
            setTile(x, y, ICE);
            flood(ICE, 8);
            if (planet.getAtmosphere() == Atmosphere.Oxygen) {
                // Make the ice cap slightly larger if there is an atmosphere.
                flood(ICE, 4);
            }
        }

        // Apply craters, frequency depending on tile type.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).equals(LIGHT_GREY)) {
                    if (Die.d3() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (Die.d6() == 1) {
                    setTile(x, y, new Cratered(getTile(x, y)));
                }
            }
        }

        if (planet != null) {
            List<PlanetFeature> features = planet.getFeatures();
            if (features != null && features.size() != 0) {
                generateFeatures(features);
            }
        }
    }

    private void generateFeatures(List<PlanetFeature> features) {
        for (PlanetFeature f : features) {
            System.out.println(f);
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
                int x = Die.dieV(4);
                setTile(x, y, SILVER);
                flood(SILVER, 7);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Planet planet = new Planet();
        planet.addFeature(NightsideIce);
        planet.addFeature(MetallicSea);
        PlanetMapper p = new JanianMapper(planet,12);

        System.out.println("Janian:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/janian.jpg"));

    }
}
