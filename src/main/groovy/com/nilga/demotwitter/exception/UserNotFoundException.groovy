package com.nilga.demotwitter.exception

/**
 * Exception thrown when a user is not found in the system.
 */
class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message.
     */
    UserNotFoundException(String message) {
        super(message)
    }
}
