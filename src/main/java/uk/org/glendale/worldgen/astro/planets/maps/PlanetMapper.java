package uk.org.glendale.worldgen.astro.planets.maps;

import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;

import java.awt.*;
import java.io.IOException;

public class PlanetMapper extends Icosahedron {
    protected final Planet planet;
    protected static final int    DEFAULT_FACE_SIZE = 24;

    protected boolean hasMainMap = true;
    protected boolean hasHeightMap = false;
    protected boolean hasCloudMap = false;
    protected boolean hasOrbitMap = false;

    public PlanetMapper(final Planet planet, final int faceSize) {
        super(faceSize);
        this.planet = planet;
    }

    public PlanetMapper(final Planet planet) {
        super(DEFAULT_FACE_SIZE);
        this.planet = planet;
    }

    public boolean hasMainMap() { return hasMainMap; }

    public boolean hasHeightMap() {
        return hasHeightMap;
    }

    public boolean hasCloudMap() {
        return hasCloudMap;
    }

    public boolean hasOrbitMap() { return hasOrbitMap; }

    /**
     * Gets the latitude of the current tileY in degrees, between 0 and 90.
     * This does not differentiate between Northern and Southern hemispheres, it is mostly
     * for use determining how close to the poles the position is.
     *
     * @param tileY     Y coordinate of the tile.
     * @return          Latitude in degrees, between 0 and 90.
     */
    public int getLatitude(int tileY) {
        int lat = 0;
        if (tileY < getNumRows() / 2) {
            lat = (int) (90 * (1 - (2.0 * tileY) / getNumRows()));
        } else {
            lat = (int) (90 * (1 - (2.0 * (getNumRows() - tileY)) / getNumRows()));
        }
        return lat;
    }

    protected void generateHeightMap(int variation, int finalSize) {
        Icosahedron parent = new Icosahedron(3);
        parent.fractal();

        generateHeightMap(parent, variation, finalSize);
        hasHeightMap = true;
    }

    protected void generateHeightMap(Icosahedron parent, int variation, int finalSize) {
        int size = parent.getFaceSize();

        while (size < finalSize) {
            size *= 2;
            Icosahedron  map = new Icosahedron(size);
            map.fractal(parent, variation);
            variation /= 2;
            parent = map;
        }

        copyHeightMap(parent);
    }

    public SimpleImage drawHeightMap(int width) throws IOException {
        return drawHeight(width);
    }

    private static final int CRATER_HEIGHT = 10;

    /**
     * Set an individual tile to be a crater. Darkens the tile colour, and sets the height
     * to be lower than normal land (but higher than water).
     *
     * @param tileX     X coordinate of tile to be set.
     * @param tileY     Y coordinate of tile to be set.
     */
    private void setCraterTile(int tileX, int tileY) {
        if (!getTile(tileX, tileY).isWater()) {
            if (getHeight(tileX, tileY) != CRATER_HEIGHT) {
                setHeight(tileX, tileY, CRATER_HEIGHT);
                setTile(tileX, tileY, getTile(tileX, tileY).getShaded(85));
            }
        }
    }

    /**
     * Creates a small crater, a single tile in size.
     *
     * @param tileX     X coordinate of centre tile of crater.
     * @param tileY     Y coordinate of centre tile of crater.
     */
    private void createSmallCrater(int tileX, int tileY) {
        setCraterTile(tileX, tileY);
    }


    private void setCraterLine(int tileX, int tileY, int width) {
        for (int x = tileX - width; x <= tileX + width; x++) {
            setCraterTile(x, tileY);
        }
    }

    /**
     * Creates a medium crater, a hexagon covering six tiles. The
     * centre of the crater will be either the top or bottom centre
     * tile of the hexagon.
     *
     * @param tileX     X coordinate of centre tile of crater.
     * @param tileY     Y coordinate of centre tile of crater.
     */
    private void createMediumCrater(int tileX, int tileY) {
        try {
            setCraterLine(tileX, tileY, 1);
            Point p = getOpposite(tileX, tileY);
            tileX = (int) p.getX();
            tileY = (int) p.getY();
            setCraterLine(tileX, tileY, 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array index out of bounds for " + tileX + "," + tileY);
        }
    }

    private void createLargeCrater(int tileX, int tileY) {
        try {
            setCraterLine(tileX, tileY, 3);
            Point p = getOpposite(tileX, tileY);
            int x = (int) p.getX();
            int y = (int) p.getY();
            setCraterLine(x, y, 3);
            p = getUpDown(x, y);
            x = (int) p.getX();
            y = (int) p.getY();
            setCraterLine(x, y, 2);
            p = getUpDown(tileX, tileY);
            x = (int) p.getX();
            y = (int) p.getY();
            setCraterLine(x, y, 2);

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array index out of bounds for " + tileX + "," + tileY);
        }
    }

    protected void createCraters(int size, int number) {
        for (int c = 0; c < number; c++) {
            int tileY = getNumRows() / 5 + Die.rollZero((getNumRows() * 3) / 5);
            int tileX = 2 + Die.rollZero(getWidthAtY(tileY) - 4);

            switch (Die.d6() + size) {
                case -2: case -1: case 0: case 1:
                    createSmallCrater(tileX, tileY);
                    break;
                case 2: case 3: case 4: case 5:
                    createMediumCrater(tileX, tileY);
                    break;
                default:
                    createLargeCrater(tileX, tileY);
            }
        }
    }

    /**
     * Add a rift to the world map. This will be of the specified length and tile type.
     * Rifts will tend to run East/West, and tend towards equatorial regions, though
     * they can drift towards the poles.
     *
     * @param tile      Type of tile to set rift to.
     * @param length    Length of the rift.
     */
    protected void addRift(Tile tile, int length) {
        int y = getNumRows() / 4 + Die.die(getNumRows() / 2);
        int x = Die.rollZero(getWidthAtY(y));

        for (int l = 0; l < length; l++) {
            if (Die.d3() == 1) {
                x = getWest(x, y);
                Point p = getUpDown(x, y);
                x = (int) p.getX();
                y = (int) p.getY();
            }
            setTile(x, y, tile);
            setHeight(x, y, 10);
            x = getEast(x, y);
        }
    }
}
