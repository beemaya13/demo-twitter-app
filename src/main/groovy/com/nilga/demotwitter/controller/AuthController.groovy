package com.nilga.demotwitter.controller

import com.nilga.demotwitter.security.JwtTokenProvider
import com.nilga.demotwitter.model.AuthRequest
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus

/**
 * Controller for user authentication and registration.
 */
@RestController
@RequestMapping("/api/users")
class AuthController {

    private final AuthenticationManager authenticationManager
    private final UserService userService
    private final JwtTokenProvider jwtTokenProvider

    /**
     * Constructor for AuthController.
     *
     * @param authenticationManager the authentication manager for managing user authentication.
     * @param userService the service for user operations.
     * @param jwtTokenProvider the provider for generating JWT tokens.
     */
    @Autowired
    AuthController(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager
        this.userService = userService
        this.jwtTokenProvider = jwtTokenProvider
    }

    /**
     * Registers a new user.
     *
     * @param user the user details for registration.
     * @return a ResponseEntity containing the registered user or an error message if the username is taken.
     */
    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.existsByUsername(user.username)) {
            return ResponseEntity.badRequest().body("{\"error\": \"Username is already taken\"}")
        }
        User registeredUser = userService.register(user)
        return ResponseEntity.ok(registeredUser)
    }

    /**
     * Authenticates a user and provides a JWT token.
     *
     * @param authRequest the authentication request containing username and password.
     * @return a ResponseEntity containing the JWT token if authentication is successful,
     *         or an error message if the credentials are invalid.
     */
    @PostMapping("/login")
    ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            )

            // Generate JWT token for the user
            String token = jwtTokenProvider.generateToken(authRequest.username)

            Map<String, String> response = [
                    message: "Login successful",
                    token  : token
            ]

            return ResponseEntity.ok(response)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid credentials\"}")
        }
    }
}
