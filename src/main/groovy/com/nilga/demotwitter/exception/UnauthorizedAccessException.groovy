package com.nilga.demotwitter.exception

/**
 * Exception thrown when a user attempts to access a resource they are not authorized to access.
 */
class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedAccessException with the specified detail message.
     *
     * @param message the detail message.
     */
    UnauthorizedAccessException(String message) {
        super(message)
    }
}
