package com.nilga.demotwitter.exception

/**
 * Exception thrown when a requested comment is not found.
 */
class CommentNotFoundException extends RuntimeException {

    /**
     * Constructor for CommentNotFoundException.
     *
     * @param message the error message.
     */
    CommentNotFoundException(String message) {
        super(message)
    }
}
