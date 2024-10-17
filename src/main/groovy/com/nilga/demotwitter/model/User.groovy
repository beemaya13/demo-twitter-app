package com.nilga.demotwitter.model

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Represents a user in the Demo Twitter application.
 */
@Document(collection = "users")
@EqualsAndHashCode
class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    private String id

    /**
     * The username of the user.
     */
    private String username

    /**
     * The encoded password of the user.
     */
    private String password

    /**
     * A set of user IDs who follow this user.
     */
    private Set<String> followers = new HashSet<>()

    /**
     * A set of user IDs whom this user is following.
     */
    private Set<String> following = new HashSet<>()

    /**
     * Gets the unique identifier of the user.
     *
     * @return the user ID.
     */
    String getId() {
        return id
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the user ID to set.
     */
    void setId(String id) {
        this.id = id
    }

    /**
     * Gets the username of the user.
     *
     * @return the username.
     */
    String getUsername() {
        return username
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username to set.
     */
    void setUsername(String username) {
        this.username = username
    }

    /**
     * Gets the encoded password of the user.
     *
     * @return the password.
     */
    String getPassword() {
        return password
    }

    /**
     * Sets the encoded password of the user.
     *
     * @param password the password to set.
     */
    void setPassword(String password) {
        this.password = password
    }

    /**
     * Gets the set of user IDs who follow this user.
     *
     * @return the set of followers.
     */
    Set<String> getFollowers() {
        return followers
    }

    /**
     * Sets the set of user IDs who follow this user.
     *
     * @param followers the set of followers to set.
     */
    void setFollowers(Set<String> followers) {
        this.followers = followers
    }

    /**
     * Gets the set of user IDs whom this user is following.
     *
     * @return the set of following.
     */
    Set<String> getFollowing() {
        return following
    }

    /**
     * Sets the set of user IDs whom this user is following.
     *
     * @param following the set of following to set.
     */
    void setFollowing(Set<String> following) {
        this.following = following
    }
}
