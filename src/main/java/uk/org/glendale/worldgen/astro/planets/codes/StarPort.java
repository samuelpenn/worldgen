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
 * The type of starport may modify the World Trade Number, and the types define
 * how this modification happens. Modifiers are stored in an array, with the
 * modifier for an initial WTN of 0 to 7. These modifiers are from GURPS Free
 * Trader.
 *
 * Starports also have a minimum Tech Level, which is required to support a
 * starport of the given type.
 *
 * @author Samuel Penn
 */
public enum StarPort {

	A(10, 1000000, new double[] { 1.5, 1, 1, 0.5, 0.5, 0, 0, 0 }, "Major"),
	B(9,  100000, new double[] { 1, 1, 0.5, 0.5, 0, 0, -0.5, -1 }, "Large"),
	C(8,  10000, new double[] { 1, 0.5, 0.5, 0, 0, -0.5, -1, -1.5 }, "Medium"),
	D(7,  1000, new double[] { 0.5, 0.5, 0, 0, -0.5, -1, -1.5, -2 }, "Small"),
	E(5,  0, new double[] { 0.5, 0, 0, -0.5, -1, -1.5, -2, -2.5 }, "Minimal"),
	X(0,  0, new double[] { 0, 0, -2.5, -3, -3.5, -4, -4.5, -5 }, "None");

	int			minTechLevel	= 0;
	int			minPopulation	= 0;
	double[]	wtnModifier		= null;
	String		description		= null;

	StarPort(int minTechLevel, int minPopulation, double[] wtn,
			String description) {
		this.minTechLevel = minTechLevel;
		this.minPopulation = minPopulation;
		this.wtnModifier = wtn;
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
	 * Given a World Trade Number, returns the modified WTN depending on this
	 * type of starport. Small starports tend to heavily modify large WTNs
	 * downwards, but have a smaller (or even beneficial) effect on small WTNs.
	 */
	public double getModifiedWTN(double wtn) {
		int i = (int) wtn;
		if (i >= wtnModifier.length) {
			i = wtnModifier.length - 1;
		}
		if (i < 0) {
			i = 0;
		}
		return wtn + wtnModifier[i];
	}

	/**
	 * Gets the star port type one better than this one. A Star port type of A
	 * will return A.
	 */
	public StarPort getBetter() {
		switch (this) {
		case A:
			return A;
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
			return X;
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
