package com.nilga.demotwitter.exception

/**
 * Exception thrown when a requested post is not found.
 */
class PostNotFoundException extends RuntimeException {

    /**
     * Constructs a new PostNotFoundException with the specified detail message.
     *
     * @param message the detail message.
     */
    PostNotFoundException(String message) {
        super(message)
    }
}
