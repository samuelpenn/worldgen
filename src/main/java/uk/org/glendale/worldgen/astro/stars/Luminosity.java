/**
 * Luminosity.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.stars;

import uk.org.glendale.utils.rpg.Die;
import uk.org.glendale.worldgen.astro.Physics;

/**
 * The luminosity, which is the size of the star. This provides a finer grained
 * indication to the type of star, and can be used to determine how big to draw
 * it on the star map.
 */
public enum Luminosity {
    B("Black Hole", 0.25, 0.0000043, 1.0),
    N("Neutron Star", 0.25, 0.000013, 1.0),
    VII("White Dwarf", 0.5, 0.01, 1.0),
    VI("Sub Dwarf", 0.75, 0.5, 0.50),
    V("Main Sequence", 1.0, 1.0, 1.0),
    IV("Sub Giant", 1.25, 2.0, 1.5),
    III("Giant", 1.25, 10, 2.0),
    II("Large Giant", 1.25, 25, 2.5),
    Ib("Super Giant", 1.50, 50, 3.0),
    Ia("Super Giant", 1.50, 100, 4.0),
    O("Hypergiant", 1.50, 200, 5.0);

    private String name = null;
    private double size = 0.0;
    private double radius = 0;
    private double mass = 1.0;

    Luminosity(String name, double size, double radius, double mass) {
        this.name = name;
        this.size = size;
        this.radius = radius;
        this.mass = mass;
    }

    /**
     * Gets radius multiplier when depicting star on a map.
     *
     * @return  Size to draw star on a map.
     */
    public double getSize() {
        return size;
    }

    /**
     * Gets the standard radius of a main sequence star of this luminosity,
     * relative to Sol. This would be modified by the radius of the Spectral Type
     * for an actual star. Cool (M) stars have a much larger radius than hot (O)
     * stars of the same luminosity class.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Gets the mass multiplier for this class of star. When combined with the mass from
     * the SpectralType, will give the actual mass of the star. Note that for stellar
     * remnants, the mass is entirely dictated by its spectral type, so these luminosity
     * classes all have a mass of 1.0.
     *
     * @return  Relative Solar mass for this Luminosity class.
     */
    public double getMass() {
        return mass;
    }

    public String getDescription() {
        return name;
    }

    public boolean isBiggerThan(Luminosity compareTo) {
        if (ordinal() > compareTo.ordinal()) {
            return true;
        }

        return false;
    }

    public boolean isSmallerThan(Luminosity compareTo) {
        if (ordinal() < compareTo.ordinal()) {
            return true;
        }

        return false;
    }

    /**
     * Get a star class which is smaller than this one. Will never return
     * something smaller than a White Dwarf. This is primarily used to determine
     * a companion of this star.
     *
     * @return A smaller type of star, or null if not possible.
     */
    public Luminosity getCompanionStar() {
        Luminosity sc = null;

        switch (this) {
            case B:
            case N:
            case VII:
                sc = VII;
                break;
            case VI:
                sc = VII;
                break;
            case V:
                sc = VI;
                break;
            case IV:
            case III:
                sc = V;
                break;
            case II:
            case Ib:
            case Ia:
            case O:
                sc = III;
                break;
        }

        return sc;
    }

    /**
     * Get a suitable spectral type for a star of this class. The spectral type
     * returned is random, and is weighted according to the star class.
     *
     * @return Random spectral type.
     */
    private SpectralType getSpectralType() {
        SpectralType type = SpectralType.M0;
        SpectralType[] values = SpectralType.values();

        switch (this) {
            case B:
                // Radiate in X-ray frequencies.
                type = SpectralType.valueOf("X" + (Die.d6() + 3));
                break;
            case N:
                // Radiate in X-ray frequencies.
                type = SpectralType.valueOf("X" + (Die.d4() + 5));
                break;
            case VII:
                // Surprisingly hot, actually. These are white dwarfs.
                type = SpectralType.valueOf("D" + (Die.d8() + 1));
                break;
                /*
            case VII:
                // Brown dwarf stars, very cool, barely undergoing fusion.
                switch (Die.d6(2)) {
                    case 2:
                        type = SpectralType.valueOf("M" + (Die.d4() + 5));
                        break;
                    case 3: case 4: case 5: case 6: case 7: case 8:
                        type = SpectralType.valueOf("L" + (Die.d10() - 1));
                        break;
                    case 9: case 10: case 11:
                        type = SpectralType.valueOf("T" + (Die.d10() - 1));
                        break;
                    case 12:
                        type = SpectralType.valueOf("Y" + (Die.d6() - 1));
                        break;
                }
                break;
                */
            case VI:
                // Sub-dwarf stars, most will be cool, of type M or L.
                switch (Die.d6()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        type = SpectralType.valueOf("M" + (Die.d10() - 1));
                        break;
                    case 5:
                    case 6:
                        type = SpectralType.valueOf("L" + (Die.d10() - 1));
                        break;
                }
                break;
            case V:
                // Wide range, average around G.
                switch (Die.d6()) {
                    case 1:
                    case 2:
                        type = SpectralType.valueOf("F" + (Die.d10() - 1));
                        break;
                    case 3:
                    case 4:
                        type = SpectralType.valueOf("G" + (Die.d10() - 1));
                        break;
                    case 5:
                    case 6:
                        type = SpectralType.valueOf("K" + (Die.d10() - 1));
                        break;
                }
                break;
            case IV:
                // Sub giant stars.
                switch (Die.d6(2)) {
                    case 2:
                    case 3:
                    case 4:
                        type = SpectralType.valueOf("K" + (Die.d6() + 3));
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        type = SpectralType.valueOf("M" + (Die.d10() - 1));
                        break;
                    case 9:
                    case 10:
                    case 11:
                        type = SpectralType.valueOf("F" + (Die.d10() - 1));
                        break;
                    case 12:
                        type = SpectralType.valueOf("A" + (Die.d6() + 3));
                        break;
                }
                break;
            case III:
                // Giant stars. May be red-giants, or hot giants.
                switch (Die.d6(2)) {
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        type = SpectralType.valueOf("M" + (Die.d10() - 1));
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        type = SpectralType.valueOf("F" + (Die.d6() - 1));
                        break;
                    case 10:
                    case 11:
                        type = SpectralType.valueOf("A" + (Die.d10() - 1));
                        break;
                    case 12:
                        type = SpectralType.valueOf("B" + (Die.d6() + 3));
                        break;
                }
                break;
            case II:
                // Large (bright) giant stars
                switch (Die.d6(2)) {
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        type = SpectralType.valueOf("M" + (Die.d10() - 1));
                        break;
                    case 10:
                    case 11:
                        type = SpectralType.valueOf("A" + (Die.d4() - 1));
                        break;
                    case 12:
                        type = SpectralType.valueOf("B" + (Die.d10() - 1));
                        break;
                }
                break;
            case Ib:
                type = SpectralType.valueOf("B" + (Die.d10() - 1));
                break;
            case Ia:
                type = SpectralType.valueOf("O" + (Die.d10() - 1));
                break;
            case O:
                type = SpectralType.valueOf("O" + (Die.d6() - 1));
                break;
        }

        return type;
    }
}