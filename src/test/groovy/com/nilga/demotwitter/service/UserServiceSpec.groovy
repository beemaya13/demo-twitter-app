package com.nilga.demotwitter.service

import com.nilga.demotwitter.exception.UserAlreadyExistsException
import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock(UserRepository)
    BCryptPasswordEncoder passwordEncoder = Mock(BCryptPasswordEncoder)
    UserService userService = new UserService(userRepository, null, passwordEncoder)

    def "should register a new user successfully"() {
        given:
        def user = new User(username: "newUser", password: "password123")
        userRepository.findByUsername(user.username) >> null
        passwordEncoder.encode(user.password) >> "encodedPassword"
        userRepository.save(user) >> user

        when:
        def result = userService.register(user)

        then:
        result == user
        result.password == "encodedPassword"
    }

    def "should throw UserAlreadyExistsException if username already exists"() {
        given:
        def user = new User(username: "existingUser")
        userRepository.findByUsername(user.username) >> user

        when:
        userService.register(user)

        then:
        thrown(UserAlreadyExistsException)
    }

    def "should return true if username exists"() {
        given:
        def username = "existingUser"
        userRepository.findByUsername(username) >> new User(username: username)

        when:
        def result = userService.existsByUsername(username)

        then:
        result
    }

    def "should return false if username does not exist"() {
        given:
        def username = "nonExistingUser"
        userRepository.findByUsername(username) >> null

        when:
        def result = userService.existsByUsername(username)

        then:
        !result
    }

    def "should throw UserNotFoundException if user is not found by username"() {
        given:
        def username = "nonExistingUser"
        userRepository.findByUsername(username) >> null

        when:
        userService.getUserIdByUsername(username)

        then:
        def ex = thrown(UserNotFoundException)
        ex.message == "User with username $username not found."
    }

    def "should return user ID if user is found by username"() {
        given:
        def username = "existingUser"
        def user = new User(id: "123", username: username)
        userRepository.findByUsername(username) >> user

        when:
        def result = userService.getUserIdByUsername(username)

        then:
        result == "123"
    }

    def "should hash password when registering a user"() {
        given:
        def user = new User(username: "newUser", password: "plainPassword")
        userRepository.findByUsername(user.username) >> null
        passwordEncoder.encode(user.password) >> "hashedPassword"

        // Мокаем сохранение с обновленным паролем
        userRepository.save(_ as User) >> { User u ->
            u.password = "hashedPassword"
            return u
        }

        when:
        def result = userService.register(user)

        then:
        result.password == "hashedPassword"
        result.username == "newUser"
        1 * passwordEncoder.encode("plainPassword")
    }

    def "should throw UserNotFoundException when editing non-existing user"() {
        given:
        def userId = "nonExistingId"
        def userDetails = new User(username: "newUser")
        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.editUser(userId, userDetails)

        then:
        thrown(UserNotFoundException)
    }

    def "should edit and save user successfully"() {
        given:
        def userId = "123"
        def userDetails = new User(username: "newUsername", password: "newPassword")
        def existingUser = new User(id: userId, username: "oldUsername", password: "oldPassword")
        userRepository.findById(userId) >> Optional.of(existingUser)
        userRepository.save(existingUser) >> existingUser

        when:
        def result = userService.editUser(userId, userDetails)

        then:
        result.username == userDetails.username
        result.password == userDetails.password
    }

    def "should throw UserNotFoundException when deleting non-existing user"() {
        given:
        def userId = "nonExistingId"
        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.deleteUser(userId)

        then:
        thrown(UserNotFoundException)
    }

    def "should delete user successfully"() {
        given:
        def userId = "123"
        def user = new User(id: userId)
        userRepository.findById(userId) >> Optional.of(user)

        when:
        userService.deleteUser(userId)

        then:
        1 * userRepository.deleteById(userId)
    }

    def "should successfully subscribe to a user"() {
        given:
        def followerId = "follower1"
        def userToFollowId = "user1"
        def follower = new User(id: followerId, followers: [], following: [])
        def userToFollow = new User(id: userToFollowId, followers: [], following: [])

        userRepository.findById(followerId) >> Optional.of(follower)
        userRepository.findById(userToFollowId) >> Optional.of(userToFollow)

        when:
        def result = userService.subscribe(followerId, userToFollowId)

        then:
        result
        1 * userRepository.save(follower)
        1 * userRepository.save(userToFollow)
    }

    def "should return false if user already subscribed"() {
        given:
        def followerId = "follower1"
        def userToFollowId = "user1"
        def follower = new User(id: followerId, followers: [], following: [userToFollowId])
        def userToFollow = new User(id: userToFollowId, followers: [followerId], following: [])

        userRepository.findById(followerId) >> Optional.of(follower)
        userRepository.findById(userToFollowId) >> Optional.of(userToFollow)

        when:
        def result = userService.subscribe(followerId, userToFollowId)

        then:
        !result
        0 * userRepository.save(_)
    }

    def "should throw UserNotFoundException when trying to subscribe to non-existing user"() {
        given:
        def followerId = "follower1"
        def userToFollowId = "nonExistingUser"
        userRepository.findById(userToFollowId) >> Optional.empty()

        when:
        userService.subscribe(followerId, userToFollowId)

        then:
        thrown(UserNotFoundException)
    }

    def "should unsubscribe from user successfully"() {
        given:
        def followerId = "follower1"
        def userToUnfollowId = "user1"
        def follower = new User(id: followerId, followers: [], following: [userToUnfollowId])
        def userToUnfollow = new User(id: userToUnfollowId, followers: [followerId], following: [])

        userRepository.findById(followerId) >> Optional.of(follower)
        userRepository.findById(userToUnfollowId) >> Optional.of(userToUnfollow)

        when:
        def result = userService.unsubscribe(followerId, userToUnfollowId)

        then:
        result
        1 * userRepository.save(follower)
        1 * userRepository.save(userToUnfollow)
    }

    def "should return false if user was not subscribed when trying to unsubscribe"() {
        given:
        def followerId = "follower1"
        def userToUnfollowId = "user1"
        def follower = new User(id: followerId, followers: [], following: [])
        def userToUnfollow = new User(id: userToUnfollowId, followers: [], following: [])

        userRepository.findById(followerId) >> Optional.of(follower)
        userRepository.findById(userToUnfollowId) >> Optional.of(userToUnfollow)

        when:
        def result = userService.unsubscribe(followerId, userToUnfollowId)

        then:
        !result
        0 * userRepository.save(_)
    }

    def "should throw UserNotFoundException when trying to unsubscribe from non-existing user"() {
        given:
        def followerId = "follower1"
        def userToUnfollowId = "nonExistingUser"
        userRepository.findById(userToUnfollowId) >> Optional.empty()

        when:
        userService.unsubscribe(followerId, userToUnfollowId)

        then:
        thrown(UserNotFoundException)
    }

    def "should return followers of the user"() {
        given:
        def userId = "userId123"
        def user = new User(id: userId, followers: ["follower1", "follower2"])
        def followers = [new User(id: "follower1"), new User(id: "follower2")]

        when:
        userRepository.findById(userId) >> Optional.of(user)
        userRepository.findAllById(user.followers) >> followers
        List<User> result = userService.getFollowers(userId)

        then:
        result == followers
    }

    def "should throw exception when user is not found for followers"() {
        given:
        def userId = "nonExistentUserId"

        when:
        userRepository.findById(userId) >> Optional.empty()
        userService.getFollowers(userId)

        then:
        thrown(UserNotFoundException)
    }

    def "should return following users of the user"() {
        given:
        def userId = "userId123"
        def user = new User(id: userId, following: ["following1", "following2"])
        def following = [new User(id: "following1"), new User(id: "following2")]

        when:
        userRepository.findById(userId) >> Optional.of(user)
        userRepository.findAllById(user.following) >> following
        List<User> result = userService.getFollowing(userId)

        then:
        result == following
    }

    def "should throw exception when user is not found for following"() {
        given:
        def userId = "nonExistentUserId"

        when:
        userRepository.findById(userId) >> Optional.empty()
        userService.getFollowing(userId)

        then:
        thrown(UserNotFoundException)
    }
}

