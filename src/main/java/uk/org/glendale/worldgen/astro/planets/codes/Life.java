/**
 * Life.java
 *
 * Copyright (c) 2007, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Defines the evolutionary stage of life on a planet.
 *
 * Proteins: Simple replicators.
 * Protozoa: Single celled organisms
 * Metazoa: Multi-celled organisms.
 * SimpleOcean: Animal and plant life
 * ComplexOcean: Fish
 * SimpleLand: Insects, moss, simple plants.
 * ComplexLand: Early tetrapods and forests.
 * Extensive: Earth-like.
 *
 * @see http://mapcraft.glendale.org.uk/worldgen/planets/lifelevel
 *
 * @author Samuel Penn
 */
public enum Life {
    None(5),
    Organic(5),
    Archaean(5),
    Aerobic(5),
    ComplexOcean(3),
    SimpleLand(2),
    ComplexLand(1),
    Extensive(0);

    private int badness = 0;
    Life(int badness) {
        this.badness = badness;
    }

    public int getBadness() {
        return badness;
    }

    /**
     * True iff this type of life is simpler than the one passed.
     */
    public boolean isSimplerThan(Life type) {
        return type.ordinal() > ordinal();
    }

    /**
     * True iff this type of life is more complex than the one passed.
     */
    public boolean isMoreComplexThan(Life type) {
        return type.ordinal() < ordinal();
    }
}
