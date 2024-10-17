package com.nilga.demotwitter.controller

import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.Post
import com.nilga.demotwitter.service.CommentService
import com.nilga.demotwitter.service.PostService
import com.nilga.demotwitter.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

class PostControllerSpec extends Specification {

    def postService = Mock(PostService)
    def userService = Mock(UserService)
    def commentService = Mock(CommentService)
    def postController = new PostController(postService, userService, commentService)

    def userDetails = Mock(UserDetails) {
        getUsername() >> "testUser"
    }

    def "should create a new post"() {
        given:
        def post = new Post(content: "New post")
        userService.getUserIdByUsername("testUser") >> "userId"
        postService.createPost("userId", post) >> post

        when:
        def response = postController.createPost(userDetails, post)

        then:
        response.statusCode == HttpStatus.OK
        response.body == post
    }

    def "should return posts for the specified user"() {
        given:
        userService.getUserIdByUsername("testUser") >> "userId"
        def posts = [new Post(content: "Post 1"), new Post(content: "Post 2")]
        postService.getPostsByUser("userId") >> posts

        when:
        def response = postController.getPostsByUser(userDetails, null)

        then:
        response.statusCode == HttpStatus.OK
        response.body == posts
    }

    def "should return 403 when trying to edit a post that does not belong to the user"() {
        given:
        def postId = "postId"
        def newPost = new Post(content: "Updated content")
        userService.getUserIdByUsername("testUser") >> "userId"
        def existingPost = new Post(userId: "anotherUserId", content: "Old content")
        postService.getPostById(postId) >> existingPost

        when:
        def response = postController.editPost(postId, newPost, userDetails)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
        response.body == "You are not authorized to edit this post."
    }

    def "should update post successfully"() {
        given:
        def postId = "postId"
        def newPost = new Post(content: "Updated content")
        userService.getUserIdByUsername("testUser") >> "userId"
        def existingPost = new Post(userId: "userId", content: "Old content")
        postService.getPostById(postId) >> existingPost
        postService.editPost(postId, newPost) >> newPost

        when:
        def response = postController.editPost(postId, newPost, userDetails)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Post with ID: ${postId} updated successfully."
    }

    def "should delete post successfully"() {
        given:
        def postId = "postId"
        userService.getUserIdByUsername("testUser") >> "userId"
        def post = new Post(userId: "userId", content: "Some content")
        postService.getPostById(postId) >> post
        postService.deletePost(postId) >> true

        when:
        def response = postController.deletePost(postId, userDetails)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Post with ID: ${postId} deleted successfully."
    }

    def "should return 403 when trying to delete a post that does not belong to the user"() {
        given:
        def postId = "postId"
        userService.getUserIdByUsername("testUser") >> "userId"
        def post = new Post(userId: "anotherUserId", content: "Some content")
        postService.getPostById(postId) >> post

        when:
        def response = postController.deletePost(postId, userDetails)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
        response.body == "You are not authorized to delete this post."
    }

    def "should like a post successfully"() {
        given:
        def postId = "postId"
        userService.getUserIdByUsername("testUser") >> "userId"
        postService.likePost(postId, "userId") >> true

        when:
        def response = postController.likePost(postId, userDetails)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Post with ID: ${postId} liked successfully."
    }

    def "should return bad request when trying to like a post that is already liked"() {
        given:
        def postId = "postId"
        userService.getUserIdByUsername("testUser") >> "userId"
        postService.likePost(postId, "userId") >> false

        when:
        def response = postController.likePost(postId, userDetails)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "You have already liked this post."
    }

    def "should return feed for the current user"() {
        given:
        userService.getUserIdByUsername("testUser") >> "userId"
        def feed = [new Post(content: "Post 1"), new Post(content: "Post 2")]
        postService.getFeedForUser("userId") >> feed

        when:
        def response = postController.getFeedForCurrentUser(userDetails)

        then:
        response.statusCode == HttpStatus.OK
        response.body == feed
    }

    def "should return 404 when user is not found while fetching the feed"() {
        given:
        userService.getUserIdByUsername("testUser") >> "userId"
        postService.getFeedForUser("userId") >> { throw new UserNotFoundException("User not found") }

        when:
        def response = postController.getFeedForCurrentUser(userDetails)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == '{"error": "User not found"}'
    }
}

