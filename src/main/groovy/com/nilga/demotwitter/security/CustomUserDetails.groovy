package com.nilga.demotwitter.security

import com.nilga.demotwitter.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Custom implementation of the UserDetails interface to provide user-specific security information.
 */
class CustomUserDetails implements UserDetails {

    private final User user

    /**
     * Constructs a CustomUserDetails instance with the given User object.
     *
     * @param user the User object to wrap in this CustomUserDetails.
     */
    CustomUserDetails(User user) {
        this.user = user
    }

    /**
     * Returns the authorities granted to the user.
     * Since this example does not have specific roles, an empty list is returned.
     *
     * @return an empty list of GrantedAuthority.
     */
    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>()
    }

    /**
     * Returns the password of the wrapped User.
     *
     * @return the password of the user.
     */
    @Override
    String getPassword() {
        return user.getPassword()
    }

    /**
     * Returns the username of the wrapped User.
     *
     * @return the username of the user.
     */
    @Override
    String getUsername() {
        return user.getUsername()
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the account is non-expired, otherwise false.
     */
    @Override
    boolean isAccountNonExpired() {
        return true
    }

    /**
     * Indicates whether the user's account is locked or unlocked.
     *
     * @return true if the account is non-locked, otherwise false.
     */
    @Override
    boolean isAccountNonLocked() {
        return true
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if the credentials are non-expired, otherwise false.
     */
    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, otherwise false.
     */
    @Override
    boolean isEnabled() {
        return true
    }
}
