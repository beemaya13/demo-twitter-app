package com.nilga.demotwitter.controller

import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification
import spock.lang.Unroll

class UserControllerSpec extends Specification {

    UserService userService = Mock(UserService)
    UserController userController = new UserController(userService)

    @Unroll
    def "should return user by id"() {
        given:
        def userId = "123"
        def user = new User(id: userId, username: "testUser")
        userService.getUserById(userId) >> user

        when:
        ResponseEntity<?> response = userController.getUserById(userId)

        then:
        response.statusCode == HttpStatus.OK
        response.body == user
    }

    def "should return 404 when user is not found by id"() {
        given:
        def userId = "notFoundId"
        userService.getUserById(userId) >> { throw new UserNotFoundException("User not found.") }

        when:
        ResponseEntity<?> response = userController.getUserById(userId)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body.toString() == '{"error": "User not found."}'
    }

    def "should edit user and return updated user"() {
        given:
        def userId = "123"
        def userDetails = new User(username: "updatedUser")
        def updatedUser = new User(id: userId, username: "updatedUser")
        userService.editUser(userId, userDetails) >> updatedUser

        when:
        ResponseEntity<User> response = userController.editUser(userId, userDetails)

        then:
        response.statusCode == HttpStatus.OK
        response.body == updatedUser
    }

    def "should delete user and return no content"() {
        given:
        def userId = "123"
        userService.deleteUser(userId) >> null

        when:
        ResponseEntity<Void> response = userController.deleteUser(userId)

        then:
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should subscribe to user and return success"() {
        given:
        def currentUser = Mock(UserDetails)
        currentUser.username >> "followerId"
        def userToFollowId = "123"
        userService.getUserIdByUsername("followerId") >> "followerId"
        userService.subscribe("followerId", userToFollowId) >> true

        when:
        ResponseEntity<?> response = userController.subscribeToUser(userToFollowId, currentUser)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "You have successfully subscribed to the user with ID: 123."
    }

    def "should return 409 when already subscribed"() {
        given:
        def currentUser = Mock(UserDetails)
        currentUser.username >> "followerId"
        def userToFollowId = "123"
        userService.getUserIdByUsername("followerId") >> "followerId"
        userService.subscribe("followerId", userToFollowId) >> false

        when:
        ResponseEntity<?> response = userController.subscribeToUser(userToFollowId, currentUser)

        then:
        response.statusCode == HttpStatus.CONFLICT
        response.body == "Error: You are already subscribed to this user."
    }

    def "should return 400 when subscribing to yourself"() {
        given:
        def currentUser = Mock(UserDetails)
        currentUser.username >> "followerId"
        userService.getUserIdByUsername("followerId") >> "followerId"

        when:
        ResponseEntity<?> response = userController.subscribeToUser("followerId", currentUser)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "Error: You cannot subscribe to yourself."
    }

    def "should unsubscribe from user and return success"() {
        given:
        def currentUser = Mock(UserDetails)
        currentUser.username >> "followerId"
        def userToUnfollowId = "123"
        userService.getUserIdByUsername("followerId") >> "followerId"
        userService.unsubscribe("followerId", userToUnfollowId) >> true

        when:
        ResponseEntity<?> response = userController.unsubscribeFromUser(userToUnfollowId, currentUser)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "You have successfully unsubscribed from the user with ID: 123."
    }

    def "should return 409 when not subscribed during unsubscribe"() {
        given:
        def currentUser = Mock(UserDetails)
        currentUser.username >> "followerId"
        def userToUnfollowId = "123"
        userService.getUserIdByUsername("followerId") >> "followerId"
        userService.unsubscribe("followerId", userToUnfollowId) >> false

        when:
        ResponseEntity<?> response = userController.unsubscribeFromUser(userToUnfollowId, currentUser)

        then:
        response.statusCode == HttpStatus.CONFLICT
        response.body == "Error: You are not subscribed to this user."
    }

    def "should return 400 when unsubscribing from yourself"() {
        given:
        def currentUser = Mock(UserDetails)
        currentUser.username >> "followerId"
        userService.getUserIdByUsername("followerId") >> "followerId"

        when:
        ResponseEntity<?> response = userController.unsubscribeFromUser("followerId", currentUser)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "Error: You cannot unsubscribe from yourself."
    }

    def "should return followers of the current user"() {
        given:
        UserDetails currentUser = Mock(UserDetails)
        currentUser.username >> "testuser"

        def currentUserId = "userId123"
        def followers = [new User(id: "follower1"), new User(id: "follower2")]

        when:
        userService.getUserIdByUsername("testuser") >> currentUserId
        userService.getFollowers(currentUserId) >> followers
        ResponseEntity<?> response = userController.getMyFollowers(currentUser)

        then:
        response.statusCode == HttpStatus.OK
        response.body == followers
    }

    def "should return message when there are no followers"() {
        given:
        UserDetails currentUser = Mock(UserDetails)
        currentUser.username >> "testuser"

        def currentUserId = "userId123"
        def followers = []

        when:
        userService.getUserIdByUsername("testuser") >> currentUserId
        userService.getFollowers(currentUserId) >> followers
        ResponseEntity<?> response = userController.getMyFollowers(currentUser)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "You have no followers."
    }

    def "should return following users of the current user"() {
        given:
        UserDetails currentUser = Mock(UserDetails)
        currentUser.username >> "testuser"

        def currentUserId = "userId123"
        def following = [new User(id: "userToFollow1"), new User(id: "userToFollow2")]

        when:
        userService.getUserIdByUsername("testuser") >> currentUserId
        userService.getFollowing(currentUserId) >> following
        ResponseEntity<?> response = userController.getMyFollowing(currentUser)

        then:
        response.statusCode == HttpStatus.OK
        response.body == following
    }

    def "should return message when there are no following users"() {
        given:
        UserDetails currentUser = Mock(UserDetails)
        currentUser.username >> "testuser"

        def currentUserId = "userId123"
        def following = []

        when:
        userService.getUserIdByUsername("testuser") >> currentUserId
        userService.getFollowing(currentUserId) >> following
        ResponseEntity<?> response = userController.getMyFollowing(currentUser)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "You are not following anyone."
    }
}