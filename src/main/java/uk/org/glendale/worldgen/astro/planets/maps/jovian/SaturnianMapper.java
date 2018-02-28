/**
 * SaturnianMapper.java
 *
 * Copyright (C) 2017 Samuel Penn, sam@glendale.org.uk
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.maps.jovian;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.generators.Jovian;
import uk.org.glendale.worldgen.astro.planets.maps.JovianMapper;

import java.io.File;
import java.io.IOException;

import static uk.org.glendale.worldgen.astro.planets.generators.Jovian.JovianFeature.*;

/**
 * A type of Jovian world which tends to consist of rather bland, light coloured clouds.
 */
public class SaturnianMapper extends JovianMapper {

    protected static final Tile PALE_CREAM = new Tile("Pale Cream", "#EEEECC", false, 2);
    protected static final Tile LIGHT_CREAM = new Tile("Cream", "#DDDDAA", false, 5);
    protected static final Tile DARK_CREAM = new Tile("Dark Brown", "#CCCC99", false, 3);
    protected static final Tile LIGHT_BROWN = new Tile("Light Brown", "#D3AD6E", false, 3);
    protected static final Tile YELLOW = new Tile("Yellow", "#CCCC99", false, 4);

    public SaturnianMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public SaturnianMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }


    /**
     * Gets a random band colour for the clouds, based on the type of clouds prominent in the atmosphere.
     */
    private Tile getRandomColour() {
        if (planet.hasFeature(AmmoniaClouds)) {
            switch (Die.d4(2)) {
                case 2:
                    return YELLOW;
                case 3: case 4: case 5:
                    return SaturnianMapper.LIGHT_CREAM;
                case 6:
                    return SaturnianMapper.DARK_CREAM;
                case 7: case 8:
                    return SaturnianMapper.LIGHT_BROWN;
            }
        } else if (planet.hasFeature(WaterClouds)) {
            switch(Die.d6()) {
                case 1: case 2:
                    return PALE_CREAM;
                case 3: case 4: case 5:
                    return LIGHT_CREAM;
                case 6:
                    return DARK_CREAM;
            }

        } else {
            switch (Die.d3()) {
                case 1:
                    return PALE_CREAM;
                case 2:
                    return LIGHT_CREAM;
                case 3:
                    return DARK_CREAM;
            }
        }
        throw new IllegalStateException("getRandomColour: Invalid switch value.");
    }

    protected Tile getBandColour(Tile previousColour, Tile nextColour) {
        if (nextColour != null) {
            // We've already chosen the next colour, so return that.
            return nextColour;
        }
        if (previousColour == null) {
            return getRandomColour();
        }
        if (Die.d4() == 1) {
            return getRandomColour();
        } else {
            return previousColour.getVariant(Die.d8() - Die.d8());
        }
    }


    public static void main(String[] args) throws IOException {
        SaturnianMapper jovian = new SaturnianMapper(new Planet(),12);

        System.out.println("Saturnian:");
        jovian.generate();
        SimpleImage img = jovian.draw(2048);
        img.save(new File("/home/sam/tmp/saturnian.jpg"));
    }
}
