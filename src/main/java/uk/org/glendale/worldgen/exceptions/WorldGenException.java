/**
 * WorldGenException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.exceptions;

/**
 * Generic super class of all custom Exceptions for the WorldGen application.
 */
public abstract class WorldGenException extends Exception {
    protected WorldGenException(String message) {
        super(message);
    }
}
