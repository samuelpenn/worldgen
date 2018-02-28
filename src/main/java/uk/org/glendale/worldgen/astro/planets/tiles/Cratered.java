package uk.org.glendale.worldgen.astro.planets.tiles;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A cratered tile has one or more simple craters on it. If there are multiple craters then
 * they may be overlapping. Position and size of the craters is random, but should be within
 * the bounds of the tile.
 */
public class Cratered extends Tile {

    private class Crater {
        final int x;
        final int y;
        final int r;

        private Crater(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
    }

    public Cratered(Tile tile) {
        super(tile.getName(), tile.getRGB(), tile.isWater(), tile.getRandom());
    }

    public void addDetail(SimpleImage image, int x, int y, int w, int h) {
        String floor = getShiftedColour(0.85);
        String walls = getShiftedColour(1.1);

        int var = Math.max(2, w / 5);

        ArrayList<Crater> craters = new ArrayList<Crater>();
        int numCraters = 1;
        switch (Die.d6()) {
            case 1: case 2: case 3:
                numCraters = 1;
                break;
            case 4: case 5:
                numCraters = 2;
                break;
            case 6:
                numCraters = 3;
                break;
        }

        // Build a list of craters to draw.
        for (int c=0; c < numCraters; c++) {
            int radius = w / 4 + Die.dieV(var);
            int cx = x + w + Die.dieV(var + numCraters / 2);
            int cy = y + h/2 + Die.dieV(var + numCraters / 2);

            craters.add(new Crater(cx, cy, radius));
        }

        // Draw all the walls.
        for (Crater c : craters) {
            image.circleOutline(c.x, c.y, c.r, walls);
        }

        // Finally, draw all the floors.
        for (Crater c : craters) {
            image.circle(c.x, c.y, c.r - 1, floor);
        }
    }

}
