/**
 * StarPort.java
 *
 * Copyright (c) 2007, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.astro.planets.codes;

/**
 * Starport classification for a world. This uses the standard Traveller
 * classifications, from A (best) to E (worst) and X as no starport at all.
 *
 * Starports also have a minimum Tech Level, which is required to support a
 * starport of the given type.
 *
 * @author Samuel Penn
 */
public enum StarPort {

	A(10, 1_000_000, "Major"),
	Ao(10, 1_000_000, "Major Orbital"),
	B(9,  100_000, "Large"),
	Bo(9, 100_000, "Large Orbital"),
	C(8,  10_000, "Medium"),
	Co(8, 10_000, "Medium Orbital"),
	D(7,  1_000, "Small"),
	Do(7, 1_000, "Small Orbital"),
	E(5,  0, "Minimal"),
	Eo(5, 0, "Minimal Orbital"),
	X(0,  0, "None");

	int			minTechLevel	= 0;
	int			minPopulation	= 0;
	String		description		= null;

	StarPort(int minTechLevel, int minPopulation, String description) {
		this.minTechLevel = minTechLevel;
		this.minPopulation = minPopulation;
		this.description = description;
	}

	/**
	 * Get the minimum tech level that is required to support a starport of this
	 * type. Large starports generally require a good technology base to be
	 * built upon, plus provide the resources to boost the planet's own
	 * technology.
	 */
	public int getMinimumTechLevel() {
		return minTechLevel;
	}

	/**
	 * Large starports not only require a large number of people to keep them
	 * working, they attract a large number of people as well, since they tend
	 * to be trade hubs.
	 */
	public int getMinimumPopulation() {
		return minPopulation;
	}

	/**
	 * Gets the star port type one better than this one. A Star port type of A
	 * will return A.
	 */
	public StarPort getBetter() {
		switch (this) {
            case A:
            case B:
                return A;
            case C:
                return B;
            case D:
                return C;
            case E:
                return D;
            case X:
                return E;
            case Ao:
            case Bo:
                return Ao;
            case Co:
                return Bo;
            case Do:
                return Co;
            case Eo:
                return Do;
        }

		return E;
	}

	/**
	 * Gets the star port type one worse than this one. A star port type of X
	 * will return X.
	 */
	public StarPort getWorse() {
		switch (this) {
            case A:
                return B;
            case B:
                return C;
            case C:
                return D;
            case D:
                return E;
            case E:
            case Eo:
                return X;
            case Ao:
                return Bo;
            case Bo:
                return Co;
            case Co:
                return Do;
            case Do:
                return Eo;
        }

		return X;
	}

	public boolean isBetterThan(StarPort port) {
		return ordinal() > port.ordinal();
	}

	public boolean isWorseThan(StarPort port) {
		return ordinal() < port.ordinal();
	}

	public String getDescription() {
		return description;
	}

}
