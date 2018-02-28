/**
 * NoSuchObjectException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.exceptions;

/**
 * Generic abstract exception type that is thrown if a requested object is not found.
 */
public abstract class NoSuchObjectException extends WorldGenException {
    public NoSuchObjectException(String message) {
        super(message);
    }
}
