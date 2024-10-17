package com.nilga.demotwitter.service

import com.nilga.demotwitter.security.CustomUserDetails
import com.nilga.demotwitter.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Service class for loading user details for authentication.
 */
@Service
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository

    /**
     * Constructs a CustomUserDetailsService with the given user repository.
     *
     * @param userRepository the repository for accessing user data
     */
    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    /**
     * Loads a user by their username.
     *
     * @param username the username of the user to load
     * @return UserDetails of the loaded user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        def user = userRepository.findByUsername(username)
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: $username")
        }
        return new CustomUserDetails(user)
    }
}
