package com.nilga.demotwitter.service

import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.exception.UserAlreadyExistsException
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.repository.PostRepository
import com.nilga.demotwitter.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service for managing user operations such as registration, editing, subscription, and retrieval.
 */
@Service
class UserService {

    private final UserRepository userRepository
    private final PostRepository postRepository
    private final BCryptPasswordEncoder passwordEncoder

    /**
     * Constructor for injecting required dependencies.
     *
     * @param userRepository repository for user data
     * @param postRepository repository for post data
     * @param passwordEncoder encoder for password hashing
     */
    @Autowired
    UserService(UserRepository userRepository, PostRepository postRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository
        this.postRepository = postRepository
        this.passwordEncoder = passwordEncoder
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @return the registered user
     * @throws UserAlreadyExistsException if the username is already taken
     */
    User register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists")
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()))
        return userRepository.save(user)
    }

    /**
     * Checks if a user with the specified username exists.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null
    }

    /**
     * Retrieves a user ID by username.
     *
     * @param username the username of the user
     * @return the ID of the user
     * @throws UserNotFoundException if the user is not found
     */
    String getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
        if (user == null) {
            throw new UserNotFoundException("User with username $username not found.")
        }
        return user.id
    }

    /**
     * Edits the details of an existing user.
     *
     * @param id the ID of the user to edit
     * @param userDetails the new user details
     * @return the updated user
     * @throws UserNotFoundException if the user is not found
     */
    User editUser(String id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID $id not found."))
        user.setUsername(userDetails.getUsername())
        user.setPassword(userDetails.getPassword())
        return userRepository.save(user)
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws UserNotFoundException if the user is not found
     */
    User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID $id not found."))
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws UserNotFoundException if the user is not found
     */
    void deleteUser(String id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID $id not found."))
        userRepository.deleteById(id)
    }

    /**
     * Subscribes a follower to a user.
     *
     * @param followerId the ID of the follower
     * @param userToFollowId the ID of the user to follow
     * @return true if the subscription was successful, false if already subscribed
     * @throws UserNotFoundException if either user is not found
     */
    boolean subscribe(String followerId, String userToFollowId) {
        User userToFollow = userRepository.findById(userToFollowId)
                .orElseThrow(() -> new UserNotFoundException("User with ID $userToFollowId not found."))
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Follower with ID $followerId not found."))

        if (!userToFollow.followers.contains(followerId)) {
            userToFollow.followers.add(followerId)
            follower.following.add(userToFollowId)
            userRepository.save(userToFollow)
            userRepository.save(follower)
            return true
        }
        return false
    }

    /**
     * Unsubscribes a follower from a user.
     *
     * @param followerId the ID of the follower
     * @param userToUnfollowId the ID of the user to unfollow
     * @return true if the unsubscription was successful, false if not subscribed
     * @throws UserNotFoundException if either user is not found
     */
    boolean unsubscribe(String followerId, String userToUnfollowId) {
        User userToUnfollow = userRepository.findById(userToUnfollowId)
                .orElseThrow(() -> new UserNotFoundException("User with ID $userToUnfollowId not found."))
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Follower with ID $followerId not found."))

        if (userToUnfollow.followers.contains(followerId)) {
            userToUnfollow.followers.remove(followerId)
            follower.following.remove(userToUnfollowId)
            userRepository.save(userToUnfollow)
            userRepository.save(follower)
            return true
        }
        return false
    }

    /**
     * Retrieves the followers of a user.
     *
     * @param userId the ID of the user
     * @return a list of users who are followers
     * @throws UserNotFoundException if the user is not found
     */
    List<User> getFollowers(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID $userId not found."))
        return userRepository.findAllById(user.followers)
    }

    /**
     * Retrieves the users followed by a user.
     *
     * @param userId the ID of the user
     * @return a list of users that the user is following
     * @throws UserNotFoundException if the user is not found
     */
    List<User> getFollowing(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID $userId not found."))
        return userRepository.findAllById(user.following)
    }
}
