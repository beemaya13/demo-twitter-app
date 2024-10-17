package com.nilga.demotwitter.controller

import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/**
 * Controller for managing user-related actions.
 */
@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserService userService

    /**
     * Constructor for UserController.
     *
     * @param userService the service for managing users.
     */
    @Autowired
    UserController(UserService userService) {
        this.userService = userService
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return a ResponseEntity containing the user or an error message.
     */
    @GetMapping("/{id}")
    ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        try {
            User user = userService.getUserById(id)
            return ResponseEntity.ok(user)
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\": \"${e.message}\"}")
        }
    }

    /**
     * Edits a user.
     *
     * @param id          the ID of the user to edit.
     * @param userDetails the updated user details.
     * @return a ResponseEntity containing the updated user.
     */
    @PutMapping("/{id}")
    ResponseEntity<User> editUser(@PathVariable("id") String id, @RequestBody User userDetails) {
        User updatedUser = userService.editUser(id, userDetails)
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * Deletes a user.
     *
     * @param id the ID of the user to delete.
     * @return a ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Subscribes the current user to another user.
     *
     * @param userToFollowId the ID of the user to follow.
     * @param currentUser    the current authenticated user.
     * @return a ResponseEntity with a success or error message.
     */
    @PostMapping("/{id}/subscribe")
    ResponseEntity<?> subscribeToUser(@PathVariable("id") String userToFollowId,
                                      @AuthenticationPrincipal UserDetails currentUser) {
        String followerId = userService.getUserIdByUsername(currentUser.username)

        if (followerId == userToFollowId) {
            return ResponseEntity.badRequest()
                    .body("Error: You cannot subscribe to yourself.")
        }

        boolean success = userService.subscribe(followerId, userToFollowId)
        if (success) {
            return ResponseEntity.ok("You have successfully subscribed to the user with ID: $userToFollowId.")
        } else {
            return ResponseEntity.status(409)
                    .body("Error: You are already subscribed to this user.")
        }
    }

    /**
     * Unsubscribes the current user from another user.
     *
     * @param userToUnfollowId the ID of the user to unfollow.
     * @param currentUser      the current authenticated user.
     * @return a ResponseEntity with a success or error message.
     */
    @DeleteMapping("/{id}/subscribe")
    ResponseEntity<?> unsubscribeFromUser(@PathVariable("id") String userToUnfollowId,
                                          @AuthenticationPrincipal UserDetails currentUser) {
        String followerId = userService.getUserIdByUsername(currentUser.username)

        if (followerId == userToUnfollowId) {
            return ResponseEntity.badRequest()
                    .body("Error: You cannot unsubscribe from yourself.")
        }

        boolean success = userService.unsubscribe(followerId, userToUnfollowId)

        if (success) {
            return ResponseEntity.ok("You have successfully unsubscribed from the user with ID: $userToUnfollowId.")
        } else {
            return ResponseEntity.status(409)
                    .body("Error: You are not subscribed to this user.")
        }
    }

    /**
     * Retrieves the followers of the current user.
     *
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity containing the list of followers or a message.
     */
    @GetMapping("/followers")
    ResponseEntity<?> getMyFollowers(@AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        List<User> followers = userService.getFollowers(currentUserId)
        if (followers.isEmpty()) {
            return ResponseEntity.ok("You have no followers.")
        }
        return ResponseEntity.ok(followers)
    }

    /**
     * Retrieves the list of users that the current user is following.
     *
     * @param currentUser the current authenticated user.
     * @return a ResponseEntity containing the list of followed users or a message.
     */
    @GetMapping("/following")
    ResponseEntity<?> getMyFollowing(@AuthenticationPrincipal UserDetails currentUser) {
        String currentUserId = userService.getUserIdByUsername(currentUser.username)
        List<User> following = userService.getFollowing(currentUserId)
        if (following.isEmpty()) {
            return ResponseEntity.ok("You are not following anyone.")
        }
        return ResponseEntity.ok(following)
    }
}
