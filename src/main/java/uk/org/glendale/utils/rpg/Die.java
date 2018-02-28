/*
 * BSD license.
 */

package uk.org.glendale.utils.rpg;

/**
 * Dice rolling class. Generates a random number similar to polyhedral dice.
 * The type of die and the number of dice can be specified.
 *
 * @author Samuel Penn
 */
public final class Die {

    private static int roll(int size) {
	    return (int)(Math.random()*size)+1;
	}


    /**
     * Generate the result of a single d20.
     *
     * @return      A random number.
     */
    public static int die() {
        return roll(20);
    }

    /**
     * Generate the result of a single die roll, of the specified size.
     * Basically returns a random number between 1 and size.
     *
     * @param size      Size of the die to be rolled.
     *
     * @return          Random result.
     */
    public static int die(int size) {
        return roll(size);
    }

    /**
     * Generate result of a variance using two rolls, one positive and
     * one negative.
     *
     * @param size      Size of the die to be rolled.
     * @return          Random result, between -(Size-1) and +(Size-1)
     */
    public static int dieV(int size) {
        return roll(size) - roll(size);
    }

    /**
     * Generate the result of rolling several dice of the given size.
     *
     * @param size      Size of the dice to be rolled.
     * @param number    Number of dice.
     *
     * @return          Random result.
     */
    public static int die(int size, int number) {
        int total = 0;

        for (int i=0; i < number; i++) {
            total += roll(size);
        }

        return total;
    }

    public static int d2() {
        return roll(2);
    }

    public static int d2(int number) {
    	    return die(2, number);
    }


    public static int d3() {
        return roll(3);
    }

    public static int d3(int number) {
        return die(3, number);
    }

    public static int d4() {
        return roll(4);
    }

    public static int d4(int number) {
    	    return die(4, number);
    }

    public static int d6() {
        return roll(6);
    }

    public static int d6(int number) {
    	    return die(6, number);
    }

    public static int d8() {
        return roll(8);
    }

    public static int d8(int number) {
    	    return die(8, number);
    }

    public static int d10() {
        return roll(10);
    }

    public static int d10(int number) {
    	    return die(10, number);
    }

    public static int d12() {
        return roll(12);
    }

    public static int d12(int number) {
    	    return die(12, number);
    }

    public static int d20() {
        return roll(20);
    }

    public static int d20(int number) {
    	    return die(20, number);
    }

    public static int d100() {
        return roll(100);
    }

    public static int d100(int number) {
    	    return die(100, number);
    }

    public static int rollZero(int size) {
        return (int)(Math.random()*size);
    }
}

