/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.dwarf;

import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetFeature;
import uk.org.glendale.worldgen.astro.planets.generators.Dwarf;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;
import uk.org.glendale.worldgen.astro.planets.tiles.Cratered;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static uk.org.glendale.worldgen.astro.planets.generators.Dwarf.DwarfFeature.*;

/**
 * MesoArean worlds are wet versions of Mars.
 */
public class MesoAreanMapper extends PlanetMapper {

    protected static final Tile DARK_RED = new Tile("Dark Red", "#A07055", false, 3);
    protected static final Tile MID_RED = new Tile("Mid Red", "#E07040", false, 2);
    protected static final Tile LIGHT_RED = new Tile("Light Red", "#F08050", false, 2);

    protected static final Tile RIFT = new Tile("Rift", "#604040", false, 1);
    protected static final Tile WATER = new Tile("Water", "#6666AA", true, 3);


    public MesoAreanMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public MesoAreanMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    private Tile getRandomColour() {
        switch (Die.d6(2)) {
            case 2: case 3: case 4: case 5:
                return LIGHT_RED;
            case 6: case 7: case 8: case 9: case 10: case 11:
                return MID_RED;
            case 12:
                return DARK_RED;
        }
        throw new IllegalStateException("getRandomColour: Invalid switch value.");
    }

    /**
     * Generate a Hermian surface landscape. This will be grey, barren and cratered.
     */
    public void generate() {
        // Basic barren landscape.
        int totalTiles = 0;
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                Tile tile = getRandomColour();
                setTile(x, y, tile);
                totalTiles++;
            }
        }
        System.out.println("Total tiles: " + totalTiles);

        // Expand light and dark areas.
        flood(DARK_RED, 4);
        flood(LIGHT_RED, 3);

        // Apply craters, frequency depending on tile type.
        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                if (getTile(x, y).equals(LIGHT_RED)) {
                    if (Die.d12() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (getTile(x, y).equals(MID_RED)) {
                    if (Die.d6() == 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                } else if (getTile(x, y).equals(DARK_RED)) {
                    if (Die.d3() != 1) {
                        setTile(x, y, new Cratered(getTile(x, y)));
                    }
                }
            }
        }

        if (planet.getHydrographics() > 0) {
            int maxWater = 1 + (totalTiles * planet.getHydrographics()) / 1000;
            System.out.println("Seeds: " + maxWater);
            for (int i=0; i < maxWater; i++) {
                int y = Die.rollZero(getNumRows());
                int x = Die.rollZero(getWidthAtY(y));
                setTile(x, y, WATER);
            }
            floodToPercentage(WATER, planet.getHydrographics());
        }

        if (planet != null) {
            List<PlanetFeature> features = planet.getFeatures();
            if (features != null && features.size() != 0) {
                generateFeatures(features);
            }
        }
    }

    private void generateFeatures(List<PlanetFeature> features) {
        for (PlanetFeature f : features) {
            if (f == GreatRift) {
                // A single rift split across the world.
                addRift(RIFT,8 + Die.d6(2));
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Planet       planet = new Planet();
        planet.setHydrographics(10);
        PlanetMapper p = new MesoAreanMapper(planet,12);

        System.out.println("MesoArean:");
        p.generate();
        SimpleImage img = p.draw(2048);
        img.save(new File("/home/sam/tmp/mesoArean.jpg"));

    }
}
