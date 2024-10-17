package com.nilga.demotwitter.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Global exception handler to manage custom exceptions and provide meaningful error responses.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException.
     *
     * @param ex the exception.
     * @return ResponseEntity with the error message and NOT_FOUND status.
     */
    @ExceptionHandler(UserNotFoundException)
    ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND)
    }

    /**
     * Handles UserAlreadyExistsException.
     *
     * @param ex the exception.
     * @return ResponseEntity with the error message and BAD_REQUEST status.
     */
    @ExceptionHandler(UserAlreadyExistsException)
    ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage())
    }

    /**
     * Handles PostNotFoundException.
     *
     * @param ex the exception.
     * @return ResponseEntity with the error message and NOT_FOUND status.
     */
    @ExceptionHandler(PostNotFoundException)
    ResponseEntity<?> handlePostNotFoundException(PostNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND)
    }

    /**
     * Handles CommentNotFoundException.
     *
     * @param ex the exception.
     * @return ResponseEntity with the error message and NOT_FOUND status.
     */
    @ExceptionHandler(CommentNotFoundException)
    ResponseEntity<?> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND)
    }

    /**
     * Handles UnauthorizedAccessException.
     *
     * @param ex the exception.
     * @return ResponseEntity with the error message and FORBIDDEN status.
     */
    @ExceptionHandler(UnauthorizedAccessException)
    ResponseEntity<?> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN)
    }

    /**
     * Handles any other uncaught exceptions.
     *
     * @param ex the exception.
     * @return ResponseEntity with a generic error message and INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception)
    ResponseEntity<?> handleGenericException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
