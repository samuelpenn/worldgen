/**
 * Icosahedron.java
 *
 * Copyright (C) 2011, 2012, 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.utils.graphics;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;

/**
 * Model for mapping world surfaces as an icosahedron. This would split the
 * world into triangles (20 large triangles, each split into many smaller
 * triangles).
 *
 * There are: 5 large triangles along top and bottom, and 10 on the equator.
 *
 * @author Samuel Penn
 */
public class Icosahedron {
    private final static double  ROOT3 = Math.sqrt(3.0);

    // The number of horizontal faces. 5 is a dodecahedron.
    private final static int     FACES = 5;

    private final int faceSize;
    private final int numRows;

    /** Width of each row, in tiles. */
    private final int[] rowWidths;

    /** X position of each triangle. */
    private final int[][] xpos;

    /** Direction of each triangle, either up (-ve) or down (+ve). */
    private final int[][] vdir;

    /** Contains values for each point on the map. */
    private final Tile[][] map;

    /** Values for height map for each tile. */
    private final int[][] heightMap;

    /**
     * Construct a new Icosahedron world map of the given size.
     * The size is the number of vertical tile rows on each face, so the total
     * height of the map will be size *3, and the width about size * 10.
     *
     * @param faceSize  Number of rows in each face.
     */
    public Icosahedron(final int faceSize) {
        this.faceSize = faceSize;
        numRows = faceSize * 3;

        rowWidths = new int[numRows];
        xpos = new int[numRows][];
        vdir = new int[numRows][];
        map = new Tile[numRows][];
        heightMap = new int[numRows][];

        calculateDimensions();

        for (int tileY = 0; tileY < getNumRows(); tileY++) {
            map[tileY] = new Tile[getWidthAtY(tileY)];
        }
    }

    /**
     * Gets the size of each face of the Icosahedron. This is the height from the
     * base to the top. The total height of the map is three times this.
     *
     * @return  Height (in tiles) of each face of the Icosahedron.
     */
    public int getFaceSize() {
        return faceSize;
    }

    /**
     * Gets the total height of the map in tiles. This is always three times the
     * size of each face.
     *
     * @return  Map height in tiles.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Given a map size, calculate the coordinates of the map tiles.
     * The map size is the height of each face in tiles. The entire
     * map will be faceSize * 3 tiles tall.
     */
    private void calculateDimensions() {
        // Calculate how many tiles there are in each row.
        int row = 0, w = 1;
        int column;

        // Calculate the coordinates of the tiles in the northern faces.
        while (row < faceSize) {
            rowWidths[row] = w * Icosahedron.FACES;
            xpos[row] = new int[rowWidths[row]];
            vdir[row] = new int[rowWidths[row]];
            w += 2;
            row++;
        }

        // Calculate the coordinates of the tiles in the equatorial faces.
        while (row < faceSize * 2) {
            rowWidths[row] = (faceSize * 2 * Icosahedron.FACES);
            xpos[row] = new int[rowWidths[row]];
            vdir[row] = new int[rowWidths[row]];
            row++;
        }

        // Calculate the coordinates of the tiles in the southern faces.
        while (row < faceSize * 3) {
            w -= 2;
            rowWidths[row] = (w * Icosahedron.FACES);
            xpos[row] = new int[rowWidths[row]];
            vdir[row] = new int[rowWidths[row]];
            row++;
        }

        // Now figure out X position of each tile. Start with the northern and equatorial faces.
        // Also calculate the direction of each tile.
        // -1 means that the pointy end is at the top.
        // +1 means that the pointy end is at the bottom.

        row = 0;
        while (row < faceSize *2) {
            column = 0;
            for (int f = 0; f < Icosahedron.FACES; f++) {
                int direction = -1;
                int startX = ((f+1) * faceSize * 2) - row;
                for (int x = 0; x < xpos[row].length / Icosahedron.FACES; x++) {
                    xpos[row][column] = startX + x;
                    vdir[row][column] = direction;
                    direction *= -1;
                    column++;
                }
            }
            row++;
        }

        // Finally the southern faces.
        while (row < faceSize * 3) {
            column = 0;
            for (int f = 0; f < Icosahedron.FACES; f++) {
                int direction = +1;
                int startX = ((f+1) * faceSize * 2) + (row - (faceSize * 3)) - faceSize + 1;
                for (int x = 0; x < xpos[row].length / Icosahedron.FACES; x++) {
                    xpos[row][column] = startX + x;
                    vdir[row][column] = direction;
                    direction *= -1;
                    column++;
                }
            }
            row++;
        }

        Tile    grey = new Tile("Grey", "#777777", false);
        for (int tileY=0; tileY < rowWidths.length; tileY++) {
            map[tileY] = new Tile[getWidthAtY(tileY)];
            heightMap[tileY] = new int[getWidthAtY(tileY)];
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                map[tileY][tileX] = grey;
                heightMap[tileY][tileX] = 50;
            }
        }

    }

    /**
     * Sets the tile at the given coordinates. If the given coordinates are out of bounds, then they
	 * will be wrapped until they are within bounds.
     *
     * @param tileX     X coordinate of tile.
     * @param tileY     Y coordinate of tile.
     * @param tile      Tile to set it to.
     */
    public void setTile(int tileX, int tileY, Tile tile) {
        if (tile == null) {
            throw new IllegalArgumentException("Cannot set tile to be null");
        }
        // TODO: This should wrap by switching X to the 'other side' and working back down/up the world.
        if (tileY >= getNumRows()) {
        	tileY = getNumRows() - 1;
		}
		if (tileY < 0) {
        	tileY = 0;
		}

		tileX = tileX % getWidthAtY(tileY);
		while (tileX < 0) {
        	tileX += getWidthAtY(tileY);
		}
        map[tileY][tileX] = tile;
    }

    public Tile getTile(int tileX, int tileY) {
        return map[tileY][tileX];
    }

    /**
     * Sets the height of the given tile on the height map. A height can be between 1 and 100 inclusive.
     * Values outside of this range will be capped.
     *
     * @param tileX     X coordinate of tile.
     * @param tileY     Y coordinate of tile.
     * @param height    Height of the tile.
     */
    public void setHeight(int tileX, int tileY, int height) {
        height = Math.max(height, 1);
        height = Math.min(height, 100);

        if (tileY >= getNumRows()) {
            tileY = getNumRows() - 1;
        }
        if (tileY < 0) {
            tileY = 0;
        }

        tileX = tileX % getWidthAtY(tileY);
        while (tileX < 0) {
            tileX += getWidthAtY(tileY);
        }
        heightMap[tileY][tileX] = height;
    }

    /**
     * Gets the height of the given tile.
     *
     * @param tileX     X coordinate of tile.
     * @param tileY     Y coordinate of tile.
     * @return          Height, in range 1..100 inclusive.
     */
    public int getHeight(int tileX, int tileY) {
        if (tileY < 0) {
            tileY = 0;
        } else if (tileY >= numRows) {
            tileY = numRows - 1;
        }
        if (tileX < 0) {
            tileX += getWidthAtY(tileY);
        }
        if (tileX >= getWidthAtY(tileY)) {
            tileX -= getWidthAtY(tileY);
        }
        return heightMap[tileY][tileX];
    }

    public void copyHeightMap(Icosahedron source) {
        if (getNumRows() != source.getNumRows()) {
            throw new IllegalArgumentException("Source must be the same size.");
        }
        for (int tileY = 0; tileY < getNumRows(); tileY++) {
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                setHeight(tileX, tileY, source.getHeight(tileX, tileY));
            }
        }
    }

    /**
     * Checks whether a given coordinate is valid. Bounds check based on height,
     * and also width. Width varies according to the row on the map.
     *
     * @param tileX     X coordinate of tile.
     * @param tileY     Y coordinate of tile.
     * @return          True iff this coordinate is a valid tile.
     */
	private boolean isValid(final int tileX, final int tileY) {
		for (int x = 0; x < xpos[tileY].length; x++) {
			if (xpos[tileY][x] == tileX) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the width of the world at the given y coordinate.
	 * Y coordinate ranges from 0 (north pole) to faceSize * 3 - 1 (south pole).
	 *
	 * @param tileY		Y coordinate on map, from 0 to faceSize * 3 - 1.
	 * @return          Width of the world in tiles at this latitude.
	 */
	public final int getWidthAtY(final int tileY) {
		if (tileY < 0 || tileY >= rowWidths.length) {
			throw new IllegalArgumentException("Y coordinate [" + tileY
					+ "] is outside bounds 0 - " + (rowWidths.length -1));
		}
		return rowWidths[tileY];
	}

    /**
     * Gets the pixel coordinates of the base of this tile. The base is the left corner of
     * the triangle. This will be the bottom corner if the tile direction is upwards, or
     * the top if the tile points downwards.
     *
     * @param tileX		    X coordinate to check.
     * @param tileY		    Y coordinate to check.
     * @param tileWidthPx   Width if each tile in pixels.
     *
     * @return          Pixel coordinates of this tile.
     */
	private Point getBase(final int tileX, final int tileY, final int tileWidthPx) {
		int px = (xpos[tileY][tileX] -1) * tileWidthPx;
		int py = (int) (tileY * tileWidthPx * ROOT3);

		if (getDirection(tileX, tileY) > 0) {
			py -= (tileWidthPx * ROOT3);
		}

		return new Point(px, py);
	}

    /**
     * Gets the vertical direction of this tile.
     *
     * @param tileX		X coordinate to check.
     * @param tileY		Y coordinate to check.
     *
     * @return          -1 if the tile points up, +1 if tile points down.
     */
	private int getDirection(final int tileX, final int tileY) {
	    return vdir[tileY][tileX];
	}

	/**
	 * Gets the X coordinate of the tile to the West of the specified tile.
	 * This will be X-1, unless we need to wrap around the world, in which
	 * case it is the far East of the map.
	 *
	 * @param tileX		X coordinate to check.
	 * @param tileY		Y coordinate to check.
	 * @return			X coordinate of new tile.
	 */
	protected int getWest(final int tileX, final int tileY) {
		int x = tileX - 1;
		if (x < 0) {
			x = getWidthAtY(tileY) - 1;
		}
		return x;
	}

	/**
	 * Gets the X coordinate of the tile to the East of the specified tile.
	 * This will be X+1, unless we need to wrap around the world, in which
	 * case it is the far West of the map.
	 *
	 * @param tileX		X coordinate to check.
	 * @param tileY		Y coordinate to check.
	 * @return			X coordinate of new tile.
	 */
	protected int getEast(final int tileX, final int tileY) {
		int x= tileX + 1;
		if (x >= getWidthAtY(tileY)) {
			x = 0;
		}
		return x;
	}

	/**
	 * Gets the X/Y coordinate of the tile either directly to the north or
	 * directly to the south of the specified tile. Which way depends on
	 * the facing of the current tile. There is never a wrap, since tiles
	 * at the poles will always return a tile nearer the equator.
	 *
	 * @param tileX		X coordinate to check.
	 * @param tileY		Y coordinate to check.
	 * @return			Y coordinate of new tile.
	 */
	protected final Point getUpDown(final int tileX, final int tileY) {
		int d = vdir[tileY][tileX];
		int x = tileX;
		int y = tileY - d;

		if (tileY >= faceSize * 2 || tileY < faceSize) {
			// Bottom or top third (move from tileY to y).
			int		orgWidth = getWidthAtY(tileY) / FACES;
			int		newWidth = getWidthAtY(y) / FACES;

			if (orgWidth == 0) {
			    return new Point(x, y);
            }

			int		ts = (tileX / orgWidth);
			x -= (orgWidth - newWidth) * ts;
			x -= (orgWidth - newWidth) / 2;
		} else if (tileY == faceSize && y == faceSize - 1) {
			x -= x / (faceSize * 2) + 1;
		} else if (tileY == (faceSize * 2 - 1) && y == (faceSize * 2)) {
			x -= x / (faceSize * 2);
		} else if (tileY >= faceSize) {
			x -= vdir[tileY][tileX];
		}

		return new Point(x, y);
	}

	protected final Point getOpposite(final int tileX, final int tileY) {
        int d = vdir[tileY][tileX];
        int x = tileX;
        int y = tileY + d;

        if (tileY >= faceSize * 2 || tileY < faceSize) {
            // Bottom or top third (move from tileY to y).
            int		orgWidth = getWidthAtY(tileY) / FACES;
            int		newWidth = getWidthAtY(y) / FACES;

            if (orgWidth == 0) {
                return new Point(x, y);
            }

            int		ts = (tileX / orgWidth);
            x -= (orgWidth - newWidth) * ts;
            x -= (orgWidth - newWidth) / 2;
        } else if (tileY == faceSize && y == faceSize - 1) {
            x -= x / (faceSize * 2) + 1;
        } else if (tileY == (faceSize * 2 - 1) && y == (faceSize * 2)) {
            x += x / (faceSize * 2);
        } else if (tileY >= faceSize) {
            x += vdir[tileY][tileX];
        }

        return new Point(x, y);
    }

    /**
     * Generate a completely random height map for the entire map. This is the basis for generating
     * a fractal map. This is designed to be called with the faceSize for the map is a small number,
     * such as 3. The fractal generator should then iterate on successively larger maps by calling
     * fractal(Icosahedron, int).
     */
	public void fractal() {
	    for (int tileY=0; tileY < getNumRows(); tileY++) {
	        for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
	            setHeight(tileX, tileY, Die.d100());
            }
        }
    }

    private int getParentHeight(Icosahedron parent, int tileX, int tileY) {
        int divisor = getNumRows() / parent.getNumRows();
        int parentY = tileY / divisor;
        int parentX = (int) (tileX / ((1.0 * getWidthAtY(tileY)) / parent.getWidthAtY(parentY)));

        return parent.getHeight(parentX, parentY);
    }

    /**
     * Generates a random height map for the entire map, based off a lower resolution parent height map.
     * This takes a parent map which has had fractal() called on it. It assumes (but doesn't enforce)
     * that this map is twice the faceSize of the parent that is passed in.
     *
     * The height variation should be reduced on each iterative call for larger maps, so as finer detail
     * is generated the randomness is reduced.
     *
     * @param parent        Parent Icosahedron map, should be at least half the size of this map.
     * @param variation     Size of random variation of heights.
     */
    public void fractal(Icosahedron parent, int variation) {
	    if (parent.getNumRows() >= getNumRows()) {
	        throw new IllegalArgumentException("Parent map must be smaller than this map.");
        }

        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
                int h0 = getParentHeight(parent, tileX, tileY);
                Point p = getUpDown(tileX, tileY);
                int h1 = getParentHeight(parent, (int) p.getX(), (int) p.getY());
                int h2 = getParentHeight(parent, tileX - 1 , tileY);
                int h3 = getParentHeight(parent, tileX + 1 , tileY);

                int h = (h0 + h1 + h2 + h3) / 4 + Die.dieV(variation);

                setHeight(tileX, tileY, h);
            }
        }
    }

    /**
     * Copies the height map to the tile map, using greyscale to denote height. This is mostly used for
     * testing purposes so that it is possible to see what a generated height map looks like. The original
     * map will be overwritten when this is done.
     *
     * May be useful for generating bump maps in the future.
     */
    private void heightToTiles() {
        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                setTile(tileX, tileY, new Tile(getHeight(tileX, tileY) * 2, false));
            }
        }
    }

    private void heightToTransparency(String colour) {
        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                setTile(tileX, tileY, new Tile("T", colour,(int) (getHeight(tileX, tileY) * 2.5), false, 2));
            }
        }
    }

    /**
     * Gets the sea level given a known percentage cover. More generally, this works out what height
     * covers a given percentage of the surface, by checking the height map.
     *
     * @param percentage    Percentage of surface to be covered.
     *
     * @return              Height at which all levels below this cover the given percentage of the surface.
     */
    protected int getSeaLevel(int percentage) {
        int[] sorted = new int[101];
        int   count = 0;

        // Produce a count of the total number of tiles at each height level.
        for (int tileY=0; tileY < getNumRows(); tileY++) {
            for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                sorted[getHeight(tileX, tileY)] ++;
                count++;
            }
        }

        int numberToCover = (count * percentage) / 100;
        int height = 0;
        while (numberToCover > 0 && height < 100) {
            numberToCover -= sorted[height];
            height++;
        }

        return height;
    }


    /**
     * Used for testing. Generates a random map.
     */
	public void generate() {
		Tile	light = new Tile ("Light", "#606060", false);
		Tile	dark = new Tile("Dark", "#404040", false);
		Tile	red = new Tile("Red", "#FF0000", false);

		for (int tileY=0; tileY < getNumRows(); tileY++) {
			for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
				if (Die.d20() == 1) {
                    map[tileY][tileX] = dark;
                } else if (Die.d4() == 1) {
                    map[tileY][tileX] = new Cratered(light);
				} else {
					map[tileY][tileX] = light;
				}
			}
		}

		flood(dark, 4);

	}

    /**
     * Add a border to the specified type of tiles, growing it in size. This is different to a simple flood(),
     * in that it doesn't use random spread, but bases growth on the number of neighbours of the specified
     * type (between 1 and 3) a tile has. If it has at least that many neighbours, then the tile is converted
     * to the given tile type.
     *
     * @param borderTile    Type of tile to grow by adding border.
     * @param neighbours    Number of neighbours needed for the border to grow (1-3).
     * @param thickness     Thickness by which to grow the border.
     */
	protected void growBorder(Tile borderTile, int neighbours, int thickness) {
	    if (neighbours < 1 || neighbours > 3) {
	        throw new IllegalArgumentException("Number of neighbours must be between 1 and 3");
        }
        for (; thickness > 0; thickness--) {
            Tile[][] tmp = new Tile[rowWidths.length][];

            for (int tileY = 0; tileY < rowWidths.length; tileY++) {
                tmp[tileY] = new Tile[getWidthAtY(tileY)];
                for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                    tmp[tileY][tileX] = map[tileY][tileX];
                }
            }
            for (int tileY = 0; tileY < rowWidths.length; tileY++) {
                for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                    if (!tmp[tileY][tileX].equals(borderTile)) {
                        try {
                            int n = 0;
                            if (map[tileY][getWest(tileX, tileY)].equals(borderTile)) {
                                n++;
                            }
                            if (map[tileY][getEast(tileX, tileY)].equals(borderTile)) {
                                n++;
                            }
                            Point p = getUpDown(tileX, tileY);
                            if (map[(int) p.getY()][(int) p.getX()].equals(borderTile)) {
                                n++;
                            }
                            if (n >= neighbours) {
                                setTile(tileX, tileY, borderTile);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Array out of bounds exception at [" + tileX + ", " + tileY + "]");
                        }
                    }
                }
            }
        }
    }

    /**
     * Grow a type of tile across the surface. For each tile of the specified type found, 'grow' it in a
     * random direction. This is repeated a number of times set by the iterations. This will tend to result
     * in a circular growth of that Tile type.
     *
     * @param floodTile     Tile to expand across the surface.
     * @param iterations    Number of times to run the expansion.
     */
	protected void flood(Tile floodTile, int iterations) {
        for (int i=0; i < iterations; i++) {
            Tile[][] tmp = new Tile[rowWidths.length][];

            for (int tileY=0; tileY < rowWidths.length; tileY++) {
                tmp[tileY] = new Tile[getWidthAtY(tileY)];
                for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                    tmp[tileY][tileX] = map[tileY][tileX];
                }
            }
            for (int tileY=0; tileY < rowWidths.length; tileY++) {
                for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
                    if (tmp[tileY][tileX].equals(floodTile)) {
                        try {
                            switch (Die.d3()) {
                                case 1:
                                    // West.
                                    map[tileY][getWest(tileX, tileY)] = floodTile;
                                    break;
                                case 2:
                                    // East.
                                    map[tileY][getEast(tileX, tileY)] = floodTile;
                                    break;
                                case 3:
                                    // North/South.
                                    Point p = getUpDown(tileX, tileY);
                                    map[(int)p.getY()][(int)p.getX()] = floodTile;
                                    break;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Array out of bounds exception at [" + tileX +", "+ tileY + "]");
                        }
                    }
                }
            }
        }
    }

    /**
     * Counts the number of tiles of the given type on the map.
     *
     * @param tile  Type of tile to count.
     * @return      Number of tiles counted.
     */
    private int countTilesOfType(Tile tile) {
	    int count = 0;
        for (int tileY=0; tileY < rowWidths.length; tileY++) {
            for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
                if (map[tileY][tileX] == tile) {
                    count++;
                }
            }
        }
        return count;
    }

    protected void floodToPercentage(Tile floodTile, int percentage) {
        floodToPercentage(floodTile, percentage, false);
    }

	protected void floodToPercentage(Tile floodTile, int percentage, boolean useHeights) {
	    int totalTiles = 0;
	    for (int y=0; y < rowWidths.length; y++) {
	        totalTiles += getWidthAtY(y);
        }
        int requiredTiles = (totalTiles * percentage) / 100;

	    int flooded = countTilesOfType(floodTile);
		while (flooded <= requiredTiles) {
			Tile[][] tmp = new Tile[rowWidths.length][];

			for (int tileY=0; tileY < rowWidths.length; tileY++) {
				tmp[tileY] = new Tile[getWidthAtY(tileY)];
				for (int tileX = 0; tileX < getWidthAtY(tileY); tileX++) {
					tmp[tileY][tileX] = map[tileY][tileX];
				}
			}
			for (int tileY=0; tileY < rowWidths.length; tileY++) {
				for (int tileX = 0; tileX < getWidthAtY(tileY) && flooded <= requiredTiles; tileX++) {
					if (tmp[tileY][tileX].equals(floodTile)) {
						try {
							switch (Die.d3()) {
								case 1:
									// West.
                                    int westX = getWest(tileX, tileY);
                                    if (!useHeights || Die.d100() <= getHeight(westX, tileY)) {
                                        if (map[tileY][westX] != floodTile) {
                                            map[tileY][westX] = floodTile;
                                            flooded++;
                                        }
                                    }
									break;
								case 2:
									// East.
                                    int eastX = getEast(tileX, tileY);
                                    if (!useHeights || Die.d100() <= getHeight(eastX, tileY)) {
                                        if (map[tileY][getEast(tileX, tileY)] != floodTile) {
                                            map[tileY][getEast(tileX, tileY)] = floodTile;
                                            flooded++;
                                        }
                                    }
									break;
								case 3:
									// North/South.
									Point p = getUpDown(tileX, tileY);
									int px = (int) p.getX();
									int py = (int) p.getY();
									if (!useHeights || Die.d100() <= getHeight(px, py)) {
                                        if (map[py][px] != floodTile) {
                                            map[py][px] = floodTile;
                                            flooded++;
                                        }
                                    }
									break;
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("Array out of bounds exception at [" + tileX +", "+ tileY + "]");
						}
					}
				}
			}
            flooded = countTilesOfType(floodTile);
		}
	}

	public SimpleImage draw(int width) throws IOException {
	    return draw(map, width);
    }

    public SimpleImage drawHeight(int width) throws IOException {
        heightToTiles();
        return draw(map, width);
    }

    public SimpleImage drawTransparency(String colour, int width) throws IOException {
        heightToTransparency(colour);
        return draw(map, width);
    }

    // Background colour to use for the maps. This is fully transparent white.
    private static int BACKGROUND = 0xFFFFFF00;

    /**
     * Creates an image from the map. The optimum tile size will be calculated for the
     * requested map width, but the actual width will be chosen to perfectly fit the map.
     * This is likely to be slightly smaller than the requested width.
     *
     * @param map           Array of tiles for this map.
     * @param width         Requested width of the map, in pixels.
     *
     * @return              Bitmap image representation of the map.
     * @throws IOException  Error writing the image.
     */
	public SimpleImage draw(Tile[][] map, int width) throws IOException {
	    int maxColumns = 0;
	    for (int row = 0; row < xpos.length; row++) {
	        int w = xpos[row][xpos[row].length-1];
	        if (w > maxColumns) {
	            maxColumns = w;
            }
        }
	    int tileWidthPx = (int) Math.floor((width / (maxColumns)) );
	    // We want the map to perfectly fit the image, so resize image based on tile size.
        int actualWidth = tileWidthPx * maxColumns + tileWidthPx;

		SimpleImage image = new SimpleImage(actualWidth, (int) (rowWidths.length * tileWidthPx * ROOT3), BACKGROUND);

		int baseX = 0;
		int baseY = (int)(tileWidthPx * ROOT3);

		int maxX = 0;
		for (int tileY=0; tileY < rowWidths.length; tileY++) {
			for (int tileX=0; tileX < getWidthAtY(tileY); tileX++) {
				Point	point = getBase(tileX, tileY, tileWidthPx);
				int		h = (int)(tileWidthPx * ROOT3 * getDirection(tileX, tileY));

				int		px = baseX + (int)point.getX();
				int		py = baseY + (int)point.getY();
				if (px > maxX) maxX = px;

				Tile t = map[tileY][tileX];

				if (t != null) {
                    image.triangleFill(px, py, tileWidthPx, h, t.getRGB());
                    image.triangle(px, py, tileWidthPx, h, t.getRGB());
                    t.addDetail(image, px, py, tileWidthPx, h);
                } else {
                    image.triangleFill(px, py, tileWidthPx, h, "#010101");
                    image.triangleFill(px, py, tileWidthPx, h, "#010101");
                }
			}
		}
		return image;
	}

	private static int nonAlpha(int rgb) {
	    return (rgb & 0xFFFFFF00) / 0xFF;
    }

    /**
     * Create a flat, stretched, version of the map. This takes an icosahedral map and distorts it
     * so that it entirely fills a rectangle of the given pixel width. The algorithm assumes that
     * pure white (0xFFFFFF) pixels are 'empty', so other pixels on each row will be stretched into
     * them. This makes it important that any real parts of the map not be pure white.
     *
     * This is principally designed to creature texture images to wrap around spheres, which require
     * the texture to be continuous.
     *
     * @param image     Image of a map to be stretched.
     * @param size      Width of the target image in pixels.
     * @return          Image stretched to fit into a rectangle.
     * @throws IOException
     */
	@SuppressWarnings("restriction")
	public static SimpleImage stretchImage(SimpleImage image, int size) throws IOException {
		// Stretch
		BufferedImage b = image.getBufferedImage();

		// First, shift the right hand side of the map into the left hand side.
        // This is so that when the stretch is performed, the middle of the map
        // doesn't get skewed due to the original icosahedron being slanted.
        int shiftWidth = b.getWidth() / 11;
        int rightBase = b.getWidth() - shiftWidth;
        for (int y=0; y < b.getHeight(); y++) {
            for (int x=0; x < shiftWidth; x++) {
                int rgb = b.getRGB(rightBase + x, y);
                if (nonAlpha(rgb) != nonAlpha(BACKGROUND)) {
                    b.setRGB(x, y, rgb);
                    b.setRGB(rightBase + x, y, BACKGROUND);
                }
            }
        }

		// Now do the stretch operation. Stretch each individual row so it fills
        // the entire map. This stretches the poles considerably, but the centre
        // should remain mostly intact.
		for (int y=0; y < b.getHeight(); y++) {
			int count = 0;
			List<Integer>  nonWhite = new ArrayList<Integer>();
			for (int x=0; x < b.getWidth(); x++) {
				if (nonAlpha(b.getRGB(x, y)) != nonAlpha(BACKGROUND) && nonAlpha(b.getRGB(x, y)) != 0) {
					nonWhite.add(b.getRGB(x, y));
					count++;
				}
			}

			int x = 0;
			double stretch = 1.0 * b.getWidth() / count;
			double total = 0;
			int	lastRgb = 0;
			for (int rgb : nonWhite) {
				lastRgb = rgb;
				total += stretch;
				int i = (int)(total);
				total -= i;
				while (i-- > 0) {
					b.setRGB(x++, y, rgb);
				}
			}
			while (x < b.getWidth()) {
				b.setRGB(x++, y, lastRgb);
			}
		}

		Image i = b.getScaledInstance(size * 2, size, BufferedImage.SCALE_FAST);
		SimpleImage si = new SimpleImage(i);

		return si;
	}

	public static void main(String[] args) throws Exception {
	    int size = 3, variation = 48;
		Icosahedron ico = new Icosahedron(size);
        ico.fractal();

		while (size < 24) {
		    Icosahedron parent = ico;
		    size *= 2;
		    variation /= 2;
		    ico = new Icosahedron(size);
		    ico.fractal(parent, variation);
        }


		ico.heightToTiles();
		//ico.generate();
		SimpleImage img = ico.draw(ico.map, 2048);

		img = Icosahedron.stretchImage(img, 2048);
		img.save(new File("/home/sam/tmp/foo.jpg"));

	}
}
