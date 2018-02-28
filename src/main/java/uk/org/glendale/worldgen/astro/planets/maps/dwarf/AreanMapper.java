/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;

/**
 * An Arean world type encompasses several sub-types, include EoArean, MesoArean, EuArean and AreanLacustric.
 * They belong to the Dwarf Terrestrial Group, and generally lack the size to hold onto their atmospheres
 * for more than a few billion years.
 */
public class AreanMapper extends PlanetMapper {
    protected final static int DEFAULT_FACE_SIZE = 24;
    protected static final Tile DARK_RED = new Tile("Dark Red", "#906045", false, 2);
    protected static final Tile MID_RED = new Tile("Mid Red", "#D08055", false, 3);
    protected static final Tile LIGHT_RED = new Tile("Light Red", "#F09050", false, 2);
    protected static final Tile ICE = new Tile("Ice", "#E0E0E0", false, 2);

    protected static final Tile RIFT = new Tile("Rift", "#D08040", false, 1);


    public AreanMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public AreanMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private Tile getRandomColour(int tileX, int tileY) {
        int h = getHeight(tileX, tileY);

        if (h < 15) {
            return DARK_RED.getShaded(75 + h);
        } else if (h < 75) {
            return LIGHT_RED.getShaded(100 - (h / 5));
        } else {
            return MID_RED.getShaded(85 + (h - 75) / 2);
        }
    }

    public void generate() {
        Icosahedron parent = new Icosahedron(3);
        parent.fractal();

        double modifier = 1.0;
        for (int y= parent.getFaceSize() * 2 - 1; y < parent.getFaceSize() * 3; y++) {
            modifier *= 0.5;
            for (int x=0; x < parent.getWidthAtY(y); x++) {
                parent.setHeight(x, y, (int)(parent.getHeight(x, y) * modifier));
            }
        }

        generateHeightMap(parent,24, DEFAULT_FACE_SIZE);

        // Basic barren landscape.
        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
                Tile tile = getRandomColour(tileX, tileY);
                setTile(tileX, tileY, tile);
            }
        }

        // Expand light and dark areas.
        flood(DARK_RED, 6);
        for (int y=0; y < getNumRows(); y++) {
            for (int x = 0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).equals(DARK_RED)) {
                    setTile(x,y, getTile(x,y).getShaded(75 + getHeight(x, y)));
                }
            }
        }
        //flood(LIGHT_RED, 3);

        // Apply craters, frequency depending on tile type.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getLatitude(y) + getHeight(x, y) / 5 > 85) {
                    setTile(x, y, ICE);
                } else if (getLatitude(y) + getHeight(x, y) / 5 > 80) {
                        setTile(x, y, getTile(x, y).getMix(ICE).getMix(ICE));
                } else if (getTile(x, y).equals(LIGHT_RED)) {
                    if (Die.d20() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (getTile(x, y).equals(MID_RED)) {
                    if (Die.d6() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (Die.d12() == 1) {
                    setTile(x, y, new Cratered(getTile(x, y)));
                }
            }
        }



    }
}
