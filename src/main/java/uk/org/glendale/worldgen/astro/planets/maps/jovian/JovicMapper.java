/**
 * JovicMapper.java
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
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;

import java.io.File;
import java.io.IOException;

/**
 * A type of large Jovian world, similar to Jupiter. Tends to have brownish/reddish bands,
 * consisting of many colours. Storms tend to be quite active.
 */
public class JovicMapper extends JovianMapper {

    protected static final Tile CREAM = new Tile("Cream", "#DDDDAA", false, 6);
    protected static final Tile DARK_BROWN = new Tile("Dark Brown", "#C69E60", false, 4);
    protected static final Tile LIGHT_BROWN = new Tile("Light Brown", "#D3AD6E", false, 4);
    protected static final Tile YELLOW = new Tile("Yellow", "#CCCC99", false, 6);

    public JovicMapper(final Planet planet, final int size) {
        super(planet, size);
    }
    public JovicMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private Tile getRandomColour() {
        switch (Die.d6()) {
            case 1:
                return JovicMapper.CREAM;
            case 2:case 3:
                return JovicMapper.YELLOW;
            case 4:case 5:
                return JovicMapper.LIGHT_BROWN;
            case 6:
                return JovicMapper.DARK_BROWN;
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
        Planet planet = new Planet();
        planet.addFeature(Jovian.JovianFeature.AmmoniaClouds);
        PlanetMapper jovian = new JovicMapper(null,12);

        System.out.println("Jovic:");
        jovian.generate();
        SimpleImage img = jovian.draw(2048);
        img.save(new File("/home/sam/tmp/jovic.jpg"));
    }
}
