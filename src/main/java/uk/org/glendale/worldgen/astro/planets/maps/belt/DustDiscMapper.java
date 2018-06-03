/*
 * Copyright (c) 2018, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps.belt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.glendale.utils.graphics.SimpleImage;
import uk.org.glendale.worldgen.astro.Physics;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.PlanetGenerator;
import uk.org.glendale.worldgen.astro.planets.maps.PlanetMapper;

import java.util.Random;

/**
 * Mapper for a disc of dust that encircles a star. This has no surface map, but does provide an
 * orbit map which is used by the star system mapper.
 */
public class DustDiscMapper extends PlanetMapper {
    protected static final Logger logger = LoggerFactory.getLogger(DustDiscMapper.class);

    public DustDiscMapper(final Planet planet, final int size) {
        super(planet, size);

        hasMainMap = false;
        hasOrbitMap = true;
    }

    public DustDiscMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);

        hasMainMap = false;
        hasOrbitMap = true;
    }

    public void generate() {
        // Nothing do do here.
    }

    public void drawOrbit(SimpleImage image, int cx, int cy, long kmPerPixel) {
        Random  random = new Random(planet.getId());
        long    distance = planet.getDistance();

        logger.info(String.format("Drawing orbit for [%s] at [%d]km scale [%d]", planet.getName(), distance, kmPerPixel));

        long    d = distance - planet.getRadius();
        while (d < distance + planet.getRadius()) {
            String colour = planet.getType().getColour();
            switch (random.nextInt(3)) {
                case 0:
                    colour = SimpleImage.getDarker(colour, 6 + random.nextInt(6));
                    break;
                case 1:
                    colour = SimpleImage.getLighter(colour, 6 + random.nextInt(6));
                    break;
                default:
                    // Colour remains unchanged.
            }
            image.circleOutline(cx, cy, (int) (d / kmPerPixel), colour,
                    1 + random.nextInt((int) (planet.getRadius() / (kmPerPixel * 10))));
            d += 1 + random.nextInt((int) (planet.getRadius() / 10));
        }
    }
}
