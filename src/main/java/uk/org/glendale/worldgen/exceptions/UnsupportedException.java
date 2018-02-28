/**
 * UnsupportedException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.exceptions;

/**
 * Exception type thrown when trying to use a feature that is not yet supported.
 * This is a runtime exception, because anything could possibly throw this.
 */
public class UnsupportedException extends RuntimeException {
    public UnsupportedException(String message) {
        super(message);
    }

    public UnsupportedException(String message, Exception e) {
        super(message, e);
    }
}
