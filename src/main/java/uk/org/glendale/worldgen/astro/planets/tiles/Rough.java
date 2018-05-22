/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.tiles;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;

/**
 * Generates a dappled pattern on the tiles when they are displayed, giving a rougher
 * look to the surface.
 */
public class Rough extends Tile {

    public Rough(Tile tile) {
        super(tile.getName(), tile.getRGB(), tile.isWater(), tile.getRandom());
    }

    public void addDetail(SimpleImage image, int x, int y, int w, int h) {
        String dark = getShiftedColour(0.9);
        String light = getShiftedColour(1.1);

        int height = Math.abs(h);
        for (int yy = 0; yy < height; yy++) {
            int width = (int) ((1.0 * w * (height - yy)) / (1.0 * height));
            for (int xx = -width; xx < width; xx++) {
                String colour = getRGB();
                switch (Die.d3()) {
                    case 1:
                        colour = dark;
                        break;
                    case 2:
                        colour = light;
                        break;
                }
                image.dot(x + xx + w, y + (int)(Math.signum(h) * yy), colour);
            }
        }
    }
}
