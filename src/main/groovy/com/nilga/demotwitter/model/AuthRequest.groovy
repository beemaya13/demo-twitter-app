package com.nilga.demotwitter.model

/**
 * Represents an authentication request with username and password.
 */
class AuthRequest {

    /**
     * The username of the user attempting to authenticate.
     */
    private String username

    /**
     * The password of the user attempting to authenticate.
     */
    private String password

    /**
     * Gets the username.
     *
     * @return the username of the user.
     */
    String getUsername() {
        return username
    }

    /**
     * Sets the username.
     *
     * @param username the username to set.
     */
    void setUsername(String username) {
        this.username = username
    }

    /**
     * Gets the password.
     *
     * @return the password of the user.
     */
    String getPassword() {
        return password
    }

    /**
     * Sets the password.
     *
     * @param password the password to set.
     */
    void setPassword(String password) {
        this.password = password
    }
}
