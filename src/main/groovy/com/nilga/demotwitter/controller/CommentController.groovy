package com.nilga.demotwitter.controller

import com.nilga.demotwitter.exception.CommentNotFoundException
import com.nilga.demotwitter.exception.PostNotFoundException
import com.nilga.demotwitter.exception.UnauthorizedAccessException
import com.nilga.demotwitter.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import com.nilga.demotwitter.model.Comment
import com.nilga.demotwitter.service.CommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for managing comments.
 */
@RestController
@RequestMapping("/api/comments")
class CommentController {

    private final CommentService commentService
    private final UserService userService

    /**
     * Constructor for CommentController.
     *
     * @param commentService the service for comment operations.
     * @param userService the service for user operations.
     */
    @Autowired
    CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService
        this.userService = userService
    }

    /**
     * Adds a comment to a post.
     *
     * @param postId the ID of the post to add a comment to.
     * @param comment the comment to be added.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with a success message or error message.
     */
    @PostMapping("/{postId}/comments")
    ResponseEntity<?> addComment(@PathVariable("postId") String postId,
                                 @RequestBody Comment comment,
                                 @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        try {
            commentService.addComment(postId, currentUserId, comment)
            return ResponseEntity.status(201).body("Comment added successfully to post with ID: ${postId}.")
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(404).body("Post with ID: ${postId} not found.")
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred while adding the comment.")
        }
    }

    /**
     * Retrieves comments for a given post.
     *
     * @param postId the ID of the post to get comments for.
     * @return a ResponseEntity with a list of comments or an error message.
     */
    @GetMapping("/{postId}/comments")
    ResponseEntity<?> getComments(@PathVariable("postId") String postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId)

            if (comments.isEmpty()) {
                return ResponseEntity.ok("No comments found for post with ID: ${postId}.")
            }

            return ResponseEntity.ok(comments)
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(404).body("Post with ID: ${postId} not found.")
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred while fetching comments.")
        }
    }

    /**
     * Edits a comment.
     *
     * @param postId the ID of the post the comment belongs to.
     * @param commentId the ID of the comment to be edited.
     * @param updatedComment the updated comment content.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with a success message or error message.
     */
    @PutMapping("/{postId}/comments/{commentId}")
    ResponseEntity<?> editComment(@PathVariable("postId") String postId,
                                  @PathVariable("commentId") String commentId,
                                  @RequestBody Comment updatedComment,
                                  @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        try {
            Comment comment = commentService.editComment(postId, commentId, currentUserId, updatedComment)
            return ResponseEntity.ok("Comment with ID: ${commentId} updated successfully.")
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(404).body("Post with ID: ${postId} not found.")
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(404).body("Comment with ID: ${commentId} not found.")
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(403).body("You are not authorized to edit this comment.")
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred while updating the comment.")
        }
    }

    /**
     * Deletes a comment.
     *
     * @param postId the ID of the post the comment belongs to.
     * @param commentId the ID of the comment to be deleted.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with a success message or error message.
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    ResponseEntity<?> deleteComment(@PathVariable("postId") String postId,
                                    @PathVariable("commentId") String commentId,
                                    @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        try {
            commentService.deleteComment(postId, commentId, currentUserId)
            return ResponseEntity.ok("Comment with ID: ${commentId} deleted successfully.")
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(404).body("Post with ID: ${postId} not found.")
        } catch (CommentNotFoundException e) {
            return ResponseEntity.status(404).body("Comment with ID: ${commentId} not found.")
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(403).body("You are not authorized to delete this comment.")
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred while deleting the comment.")
        }
    }
}
