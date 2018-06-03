/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps;

import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;

public class TerrestrialMapper extends PlanetMapper {
    protected static final int    DEFAULT_FACE_SIZE = 24;

    protected static final Tile WATER = new Tile("Water", "#7070C0", true, 2);
    protected static final Tile LAND = new Tile("Land", "#807050", false, 3);
    protected static final Tile LAND_ICE = new Tile("Glacier", "#C0C0C0", false, 2);
    protected static final Tile SEA_ICE = new Tile("Sea Ice", "#D0D0D0", true, 2);

    public TerrestrialMapper(final Planet planet, int size) {
        super(planet, size);
    }

    public void generate() {
        generateHeightMap(24, DEFAULT_FACE_SIZE);
    }

    /**
     * Covers the surface with water.
     */
    protected void setWater() {
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                setTile(x, y, WATER);
            }
        }
    }

    protected void createContinents(int num) {
        for (int c=0; c < num; c++) {
            int y = Die.rollZero(getNumRows() - 4) + 2;
            int x = Die.rollZero(getWidthAtY(y));
            setTile(x, y, LAND);
        }

        floodToPercentage(LAND, 30, true);
    }

    protected void setIceCaps() {
        int k = planet.getTemperature();
        int iceLine = 0;

        if (k > 250) {
            iceLine += (k - 250) * 1.5;
        }

        for (int y=0; y < getNumRows(); y++) {
            int lat = getLatitude(y);

            for (int x=0; x < getWidthAtY(y); x++) {
                int h = getHeight(x, y);
                if (getTile(x, y).isWater()) {
                    if (lat + h / 10 > iceLine) {
                        setTile(x, y, SEA_ICE);
                    }
                } else {
                    if (lat + h/5 > iceLine) {
                        setTile(x, y, LAND_ICE);
                    }
                }

            }
        }
    }

    /**
     * Draws fractal clouds which can be applied to a cloud layer.
     *
     * @return  New high resolution icosahedron map.
     */
    protected Icosahedron getCloudLayer() {
        Icosahedron cloud = new Icosahedron(12);
        cloud.fractal();
        int size = cloud.getFaceSize();

        int variation = 48;
        while (size < 48) {
            size *= 2;
            Icosahedron  map = new Icosahedron(size);
            map.fractal(cloud, variation);
            variation /= 2;
            cloud = map;
        }

        return cloud;
    }
}
