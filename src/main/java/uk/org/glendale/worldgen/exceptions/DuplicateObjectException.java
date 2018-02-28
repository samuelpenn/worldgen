/**
 * DuplicateObjectException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.exceptions;

public abstract class DuplicateObjectException extends WorldGenException {
    public DuplicateObjectException(String message) {
        super(message);
    }
}
