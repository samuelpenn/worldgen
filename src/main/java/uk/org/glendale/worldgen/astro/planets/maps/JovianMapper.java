/**
 * JovianMapper.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.maps;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.worldgen.astro.planets.Planet;

/**
 * Parent class for Jovian worlds. Contains some standard methods.
 * The 'main' map for these worlds is actually a cloud map, but that's all anybody ever sees.
 */
public abstract class JovianMapper extends PlanetMapper {
    protected static final int    DEFAULT_FACE_SIZE = 12;

    public JovianMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public JovianMapper(final Planet planet) {
        super(planet);
    }

    protected abstract Tile getBandColour(Tile previousColour, Tile nextColour);

    /**
     * Draw the planet's 'surface' map. This is a generic map drawer for Jovian worlds. It relies on
     * getBandColour() to select the colour of the cloud bands, which is implemented in the specific
     * world Mapper classes.
     */
    public void generate() {
        Tile    cloudColour = null;
        Tile    previousColour = null;
        Tile    nextColour = null;
        int     colour = Die.d12();
        for (int y=0; y < getNumRows(); y++) {
            if (cloudColour == null) {
                cloudColour = getBandColour(previousColour, nextColour);
            } else if (nextColour != null) {
                cloudColour = nextColour;
                nextColour = null;
            } else {
                nextColour = getBandColour(previousColour, nextColour);
                cloudColour = cloudColour.getMix(nextColour);
            }
            previousColour = cloudColour;
            for (int x=0; x < getWidthAtY(y); x++) {
                setTile(x, y, cloudColour);
            }
        }
    }
}
