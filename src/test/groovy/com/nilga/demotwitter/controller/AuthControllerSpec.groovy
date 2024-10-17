package com.nilga.demotwitter.controller

import com.nilga.demotwitter.model.AuthRequest
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.security.JwtTokenProvider
import com.nilga.demotwitter.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import spock.lang.Specification

class AuthControllerSpec extends Specification {

    // Моки зависимостей контроллера
    AuthenticationManager authenticationManager = Mock()
    UserService userService = Mock()
    JwtTokenProvider jwtTokenProvider = Mock()

    // Тестируемый объект
    AuthController authController = new AuthController(authenticationManager, userService, jwtTokenProvider)

    def "should register user successfully"() {
        given: "A new user registration request"
        def user = new User(username: "newUser", password: "password123")
        userService.existsByUsername(user.username) >> false
        userService.register(user) >> user

        when: "Registering a new user"
        ResponseEntity<?> response = authController.registerUser(user)

        then: "The user is registered successfully and status is OK"
        response.statusCode == HttpStatus.OK
        response.body == user
    }

    def "should return error if username is already taken"() {
        given: "A user registration request with an existing username"
        def user = new User(username: "existingUser", password: "password123")
        userService.existsByUsername(user.username) >> true

        when: "Registering a user with an existing username"
        ResponseEntity<?> response = authController.registerUser(user)

        then: "The response contains an error and status is BAD_REQUEST"
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "{\"error\": \"Username is already taken\"}"
    }

    def "should authenticate user and return JWT token successfully"() {
        given: "A valid authentication request"
        def authRequest = new AuthRequest(username: "validUser", password: "password123")
        def token = "jwtToken123"

        authenticationManager.authenticate(_ as UsernamePasswordAuthenticationToken) >> _
        jwtTokenProvider.generateToken(authRequest.username) >> token

        when: "Authenticating the user"
        ResponseEntity<?> response = authController.authenticateUser(authRequest)

        then: "The user is authenticated successfully and token is returned"
        response.statusCode == HttpStatus.OK
        response.body == [message: "Login successful", token: token]
    }

    def "should return error on invalid credentials during authentication"() {
        given: "An invalid authentication request"
        def authRequest = new AuthRequest(username: "invalidUser", password: "wrongPassword")

        authenticationManager.authenticate(_ as UsernamePasswordAuthenticationToken) >> { throw new AuthenticationException("Invalid credentials") {} }

        when: "Authenticating with invalid credentials"
        ResponseEntity<?> response = authController.authenticateUser(authRequest)

        then: "The response contains an error and status is UNAUTHORIZED"
        response.statusCode == HttpStatus.UNAUTHORIZED
        response.body == "{\"error\": \"Invalid credentials\"}"
    }
}

