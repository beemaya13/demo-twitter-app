package com.nilga.demotwitter.exception

/**
 * Exception thrown when a user attempts to register with a username that already exists.
 */
class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message.
     */
    UserAlreadyExistsException(String message) {
        super(message)
    }
}
