/*
/**
 * Tile.java
 *
 * Copyright (C) 2011, 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.utils.graphics;


import uk.org.glendale.utils.rpg.Die;

import java.util.HashMap;


/**
 * A tile represents a triangular section of world map. When a world is first
 * mapped, it is done so at a low resolution. Eventually each tile will be
 * scaled up using a fractal algorithm to give a more graphically appealing map,
 * but until then the low resolution tiles are used to store terrain and
 * ecological information about the world's surface.
 *
 * @author Samuel Penn
 */
public class Tile {
	private String name;
	private String rgb;
	private boolean isWater;
	private int random;
	private int opacity = 0xFF;

	public Tile(final String name, final String rgb, final boolean isWater) {
		this.name = name;
		this.rgb = rgb;
		this.isWater = isWater;
		this.random = 3;
	}

    public Tile(final String name, final String rgb, final boolean isWater, int random) {
        this.name = name;
        this.rgb = rgb;
        this.isWater = isWater;
        this.random = random;
    }

    public Tile(final String name, final String rgb, final int opacity) {
        this.name = name;
        this.rgb = rgb;
        this.opacity = opacity;
        this.isWater = false;
        this.random = 3;
    }

    public Tile(final String name, final String rgb, final int opacity, final boolean isWater, final int random) {
        this.name = name;
        this.rgb = rgb;
        this.opacity = opacity;
        this.isWater = isWater;
        this.random = random;
    }


	/**
	 * Create a Tile which is based on a grey scale value. Designed for use with height maps.
     * Grey scale values outside the range 1..254 will be capped, since pure black and pure
     * white are used to denote 'outside bounds' on a drawn map.
     *
	 * @param greyScale     Grey scale value, from 1 to 254.
	 * @param isWater       Mark whether this is a water tile or not.
	 */
	public Tile(final int greyScale, final boolean isWater) {
		this.name = "G" + greyScale;
		this.isWater = isWater;
		this.random = 0;

		this.rgb = "#" + getHex(greyScale) + getHex(greyScale) + getHex(greyScale);
	}

	/**
	 * Gets a new tile instance which is shaded darker or lighter according to the value passed.
	 * If shade is less than 100%, the new tile will be darker. If it is greater than 100%, it
     * will be lighter. Shading is not guaranteed to preserve colour, especially for values
     * not near 100%.
     *
	 * @param shade     Shade as a percentage.
	 * @return          New tile instance, shaded as appropriate.
	 */
	public Tile getShaded(int shade) {
        int r1 = Integer.parseInt(rgb.substring(1, 3), 16);
        int g1 = Integer.parseInt(rgb.substring(3, 5), 16);
        int b1 = Integer.parseInt(rgb.substring(5, 7), 16);

        String hex = "#" + getHex((r1 * shade) / 100) + getHex((g1 * shade) / 100) + getHex((b1 * shade) / 100);
        return new Tile(name, hex, opacity, isWater, random);
    }

    /**
     * Gets a variant of this tile. The variant has the same name, but a
     * colour modified by the specified amount. The modifier can be positive
     * or negative, and is applied to red, green and blue equally.
     *
     * @param var   Amount to vary the colour by.
     *
     * @return  New variant tile.
     */
    public Tile getVariant(int var) {
        int r = Integer.parseInt(rgb.substring(1, 3), 16) + var;
        int g = Integer.parseInt(rgb.substring(3, 5), 16) + var;
        int b = Integer.parseInt(rgb.substring(5, 7), 16) + var;

        String hex = "#" + getHex(r) + getHex(g) + getHex(b);

        return new Tile(name, hex, isWater, random);
    }

    /**
     * Gets a mix between this tile and the next one. The colours of the
     * two tiles are averaged, to allow fading between the two colours.
     *
     * @param next  Tile to mix this one with.
     * @return  Mixed tile.
     */
    public Tile getMix(Tile next) {
        int r1 = Integer.parseInt(rgb.substring(1, 3), 16);
        int g1 = Integer.parseInt(rgb.substring(3, 5), 16);
        int b1 = Integer.parseInt(rgb.substring(5, 7), 16);

        int r2 = Integer.parseInt(next.getRGB(0).substring(1, 3), 16);
        int g2 = Integer.parseInt(next.getRGB(0).substring(3, 5), 16);
        int b2 = Integer.parseInt(next.getRGB(0).substring(5, 7), 16);

        String hex = "#" + getHex((r1 + r2) / 2) + getHex((g1+g2)/2) + getHex((b1+b2)/2);
        return new Tile(name, hex, isWater, random);
    }

	public final String getName() {
		return name;
	}

	public final int getRandom() {
        return random;
    }

	public final void setRGB(final String rgb) {
		this.rgb = rgb;
	}

    /**
     * Gets the two digit hex value for the given value. Used for creating
     * RGB colour codes. A returned value will never be less than 1 (01) or
     * greater than 254 (FE). This is so that pure black and white can be
     * used to find map edges.
     *
     * @param v     Value to convert to hexadecimal.
     * @return      Two digit hexadecimal value, between 01 and FE.
     */
	private String getHex(int v) {
		if (v > 254)
			v = 254;
		if (v < 1)
			v = 1;

		return ((v < 16) ? "0" : "") + Integer.toHexString(v);
	}

	private String getRawHex(int v) {
        return ((v < 16) ? "0" : "") + Integer.toHexString(v);
    }

	private int getRandomised(int base, int var) {
	    if (var > 0) {
            base = base + Die.die(var) - Die.die(var);
        }
        if (base < 1) {
	        base = 1;
        } else if (base > 254) {
	        base = 254;
        }
        return base;
    }

	public final String getRGB(final int modifier) {
		int r = getRandomised(Integer.parseInt(rgb.substring(1, 3), 16), modifier);
		int g = getRandomised(Integer.parseInt(rgb.substring(3, 5), 16), modifier);
		int b = getRandomised(Integer.parseInt(rgb.substring(5, 7), 16), modifier);

		if (opacity == 0xFF) {
            return "#" + getHex(r) + getHex(g) + getHex(b);
        } else {
            return "#" + getHex(r) + getHex(g) + getHex(b) + getRawHex(opacity);
        }
	}

    public final String getRGB() {
	    return getRGB(random);
    }

    public final String getShiftedColour(double shift) {
        int r = Integer.parseInt(rgb.substring(1, 3), 16);
        int g = Integer.parseInt(rgb.substring(3, 5), 16);
        int b = Integer.parseInt(rgb.substring(5, 7), 16);

        return "#" + getHex((int)(r * shift)) + getHex((int)(g * shift)) + getHex((int)(b * shift));
    }

	public final boolean isWater() {
		return isWater;
	}

	/**
	 * Override this method to add complex detail to a tile. This is called
	 * whenever a tile is plotted on the image.
	 *
	 * param builder	Reference to map of configuration.
	 */
	public void addDetail(SimpleImage image, int x, int y, int w, int h) {
		// Empty by default.
	}

	public final String toString() {
		return rgb;
	}

	public boolean equals(Tile o) {
	    if (o != null && o.getName().equals(name)) {
	        return true;
        }
        return false;
    }

}
