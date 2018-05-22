/*
 * Copyright (c) 2017, Samuel Penn (sam@glendale.org.uk).
 * See the file LICENSE at the root of the project.
 */

package uk.org.glendale.worldgen.astro.planets.maps;

import uk.org.glendale.utils.graphics.Icosahedron;
import uk.org.glendale.utils.graphics.Tile;
import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.planets.Planet;
import uk.org.glendale.worldgen.astro.planets.generators.SmallBody;

/**
 * A Small Body is not really a planet, or even a dwarf planet. They are asteroids, comets or other
 * small objects at most a few hundred kilometres across.
 *
 * There maps tend to be incredibly simple.
 */
public class SmallBodyMapper extends PlanetMapper {
    protected static final int    DEFAULT_FACE_SIZE = 12;

    public SmallBodyMapper(final Planet planet, final int size) {
        super(planet, size);
    }

    public SmallBodyMapper(final Planet planet) {
        super(planet, DEFAULT_FACE_SIZE);
    }

    /**
     * A SmallBody has a deform map rather than a height map. The height map is normally used
     * just for bump mapping. A deform map actually deforms the object, so is far more radical.
     * This better suits asteroids which are often not entirely round.
     */
    public void generate() {
        generateHeightMap(12, DEFAULT_FACE_SIZE);
        hasHeightMap = false;
        hasDeformMap = true;
    }

    /**
     * Gets transform for the planet based on any physical features set. Each element in the
     * array is a height multiplier for that row on the surface map.
     *
     * @param planet        Planet to get transform for.
     * @return              Transform array, or null if not needed.
     */
    private double[] getDeformModifier(Planet planet) {
        double[] modifier = null;

        if (getFaceSize() != 12) {
            throw new IllegalStateException("getDeformModifier() requires that face size is set to 12");
        }

        if (planet.hasFeature(SmallBody.SmallBodyFeature.POTATO_SHAPED)) {
            modifier = new double[] { 1.8, 1.7, 1.6, 1.5, 1.4, 1.3, 1.2, 1.1, 1.0, 0.9, 0.8, 0.7,
                                      0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6,
                                      0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8};
        } else if (planet.hasFeature(SmallBody.SmallBodyFeature.EGG_SHAPED)) {
            modifier = new double[] { 1.0, 1.2, 1.4, 1.6, 1.8, 2.0, 1.8, 1.6, 1.4, 1.3, 1.2, 1.1,
                                      1.0, 1.0, 1.0, 1.0, 1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.2,
                                      0.2, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
        } else if (false) {
            modifier = new double[] { 1.8, 1.6, 1.4, 1.2, 1.0, 0.8, 0.6, 0.4, 0.2, 0.1, 0.1, 0.1,
                                      0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1,
                                      0.1, 0.1, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8};
        } else if (false) {
            modifier = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2,
                                      1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.8, 1.7, 1.6, 1.5, 1.4, 1.3,
                                      1.2, 1.2, 1.0, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};
        }

        return modifier;
    }

    /**
     * Smooth out the surface height map. This reduces the chance of a broken surface mesh,
     * which happens if neighbouring tiles are too different. The amount of smoothing is
     * controlled by the heightDividor, which is normally based on the size of the planet.
     * The larger the planet (heightDividor), the more spherical it will be. It should be
     * at least three (if not, it will be set to three.
     *
     * Also applies a deformation transform on the surface, if the planet has any shape
     * features defined.
     *
     * @param planet            Planet to smooth.
     * @param heightDividor     Number to divide height by. Minimum of three.
     */
    protected void smoothHeights(Planet planet, int heightDividor) {
        double[] modifier = getDeformModifier(planet);

        if (heightDividor < 3) {
            heightDividor = 3;
        }

        for (int y=0; y < getNumRows(); y++) {
            for (int x=0; x < getWidthAtY(y); x++) {
                int h = getHeight(x - 1, y) + getHeight(x, y) + getHeight(x + 1, y);
                setHeight(x, y, h / heightDividor);
            }
            if (modifier != null) {
                for (int x = 0; x < getWidthAtY(y); x++) {
                    setHeight(x, y, (int) (getHeight(x, y) * modifier[y]));
                }
            }
        }

    }
}
