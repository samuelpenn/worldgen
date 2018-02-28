/**
 * UnsupportedException.java
 *
 * Copyright (c) 2017, Samuel Penn.
 * See the file LICENSE at the root of the project.
 */
package uk.org.glendale.worldgen.exceptions;

/**
 * Exception type thrown when there is an issue with the API call for an HTTP method.
 */
public class ApiException extends WorldGenException {
    /**
     * Defines the type of error that has occurred.
     */
    public enum ApiErrorType {
        // No actual error type specified.
        UNDEFINED,
        // Parameter was missing.
        MISSING,
        // Parameter was the wrong format (e.g. not an integer).
        WRONG_FORMAT,
        // Parameter was the right format, but out of bounds (e.g. too long or not positive).
        OUT_OF_BOUNDS
    }

    private int statusCode = 500;
    private ApiErrorType type = ApiErrorType.UNDEFINED;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(int statusCode, ApiErrorType type, String message) {
        super(message);
        this.statusCode = statusCode;
        this.type = type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ApiErrorType getErrorType() {
        return type;
    }
}
