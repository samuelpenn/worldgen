/**
 * InvalidConfigurationException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.exceptions;

public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(String option) {
        super(String.format("Configuration option [%s] has not been set", option));
    }

    public InvalidConfigurationException(String option, String value) {
        super(String.format("Configuration option [%s] has invalid value [%s]", option, value));
    }
}
