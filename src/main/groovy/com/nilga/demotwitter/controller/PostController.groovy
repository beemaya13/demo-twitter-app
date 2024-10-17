package com.nilga.demotwitter.controller

import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.Post
import com.nilga.demotwitter.service.CommentService
import com.nilga.demotwitter.service.PostService
import com.nilga.demotwitter.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/**
 * Controller for managing posts.
 */
@RestController
@RequestMapping("/api/posts")
class PostController {

    private final PostService postService
    private final UserService userService
    private final CommentService commentService

    /**
     * Constructor for PostController.
     *
     * @param postService    the service for managing posts.
     * @param userService    the service for managing users.
     * @param commentService the service for managing comments.
     */
    @Autowired
    PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService
        this.userService = userService
        this.commentService = commentService
    }

    /**
     * Creates a new post.
     *
     * @param currentUser the current authenticated user.
     * @param post        the post to be created.
     * @return a ResponseEntity with the created post or an error message.
     */
    @PostMapping
    ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails currentUser, @RequestBody Post post) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        Post newPost = postService.createPost(currentUserId, post)
        return ResponseEntity.ok(newPost)
    }

    /**
     * Retrieves posts of a specific user or the current user if no userId is provided.
     *
     * @param currentUser the current authenticated user.
     * @param userId      the optional ID of the target user.
     * @return a ResponseEntity with the list of posts or a message indicating no posts found.
     */
    @GetMapping("/user/posts")
    ResponseEntity<?> getPostsByUser(@AuthenticationPrincipal UserDetails currentUser,
                                     @RequestParam(value = "userId", required = false) String userId) {
        String targetUserId = (userId != null) ? userId : userService.getUserIdByUsername(currentUser.username)

        List<Post> posts = postService.getPostsByUser(targetUserId)
        if (posts.isEmpty()) {
            return ResponseEntity.ok("No posts found for user with ID: ${targetUserId}")
        }
        return ResponseEntity.ok(posts)
    }

    /**
     * Edits an existing post.
     *
     * @param postId      the ID of the post to edit.
     * @param newPost     the new content of the post.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with the updated post or an error message.
     */
    @PutMapping("/{postId}")
    ResponseEntity<?> editPost(@PathVariable("postId") String postId, @RequestBody Post newPost, @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        Post post = postService.getPostById(postId)
        if (post.getUserId() != currentUserId) {
            return ResponseEntity.status(403).body("You are not authorized to edit this post.")
        }

        Post updatedPost = postService.editPost(postId, newPost)
        if (updatedPost != null) {
            return ResponseEntity.ok("Post with ID: ${postId} updated successfully.")
        }
        return ResponseEntity.status(404).body("Post with ID: ${postId} not found.")
    }

    /**
     * Deletes a post.
     *
     * @param postId      the ID of the post to delete.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/{postId}")
    ResponseEntity<?> deletePost(@PathVariable("postId") String postId, @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        Post post = postService.getPostById(postId)
        if (post.getUserId() != currentUserId) {
            return ResponseEntity.status(403).body("You are not authorized to delete this post.")
        }

        boolean deleted = postService.deletePost(postId)
        if (deleted) {
            return ResponseEntity.ok("Post with ID: ${postId} deleted successfully.")
        }
        return ResponseEntity.status(404).body("Post with ID: ${postId} not found.")
    }

    /**
     * Likes a post.
     *
     * @param postId      the ID of the post to like.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with a success message or an error message.
     */
    @PostMapping("/{postId}/like")
    ResponseEntity<?> likePost(@PathVariable("postId") String postId, @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        boolean liked = postService.likePost(postId, currentUserId)

        if (liked) {
            return ResponseEntity.ok("Post with ID: ${postId} liked successfully.")
        } else {
            return ResponseEntity.badRequest().body("You have already liked this post.")
        }
    }

    /**
     * Removes a like from a post.
     *
     * @param postId      the ID of the post to unlike.
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/{postId}/like")
    ResponseEntity<?> unlikePost(@PathVariable("postId") String postId, @AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        boolean unliked = postService.unlikePost(postId, currentUserId)

        if (unliked) {
            return ResponseEntity.ok("Like removed from post with ID: ${postId}.")
        } else {
            return ResponseEntity.badRequest().body("You haven't liked this post.")
        }
    }

    /**
     * Retrieves the feed for the current user.
     *
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity with the user's feed or an error message.
     */
    @GetMapping("/feed")
    ResponseEntity<?> getFeedForCurrentUser(@AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        try {
            List<Post> feed = postService.getFeedForUser(currentUserId)
            if (feed.isEmpty()) {
                return ResponseEntity.ok("Your feed is empty.")
            }
            return ResponseEntity.ok(feed)
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\": \"${e.message}\"}")
        } catch (Exception e) {
            String errorMessage = "An error occurred while fetching your feed. " +
                    "Error message: ${e.message}."
            return ResponseEntity.status(500).body("{\"error\": \"${errorMessage}\"}")
        }
    }
}
