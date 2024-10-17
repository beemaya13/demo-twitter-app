package com.nilga.demotwitter.controller

import com.nilga.demotwitter.exception.CommentNotFoundException
import com.nilga.demotwitter.exception.PostNotFoundException
import com.nilga.demotwitter.exception.UnauthorizedAccessException
import com.nilga.demotwitter.model.Comment
import com.nilga.demotwitter.service.CommentService
import com.nilga.demotwitter.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

class CommentControllerSpec extends Specification {

    def commentService = Mock(CommentService)
    def userService = Mock(UserService)
    def commentController = new CommentController(commentService, userService)
    def userDetails = Mock(UserDetails) {
        getUsername() >> "testUser"
    }

    def "should add a comment successfully"() {
        given:
        def postId = "postId"
        def comment = new Comment(content: "New comment")
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.addComment(postId, "userId", comment) >> comment

        when:
        def response = commentController.addComment(postId, comment, userDetails)

        then:
        response.statusCode == HttpStatus.CREATED
        response.body == "Comment added successfully to post with ID: ${postId}."
    }

    def "should return 404 when trying to add a comment to a non-existent post"() {
        given:
        def postId = "nonExistentPostId"
        def comment = new Comment(content: "New comment")
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.addComment(postId, "userId", comment) >> { throw new PostNotFoundException("Post with ID: ${postId} not found.") }

        when:
        def response = commentController.addComment(postId, comment, userDetails)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == "Post with ID: ${postId} not found."
    }

    def "should get comments for a post"() {
        given:
        def postId = "postId"
        def comments = [new Comment(content: "Comment 1"), new Comment(content: "Comment 2")]
        commentService.getCommentsByPostId(postId) >> comments

        when:
        def response = commentController.getComments(postId)

        then:
        response.statusCode == HttpStatus.OK
        response.body == comments
    }

    def "should return 404 when trying to get comments for a non-existent post"() {
        given:
        def postId = "nonExistentPostId"
        commentService.getCommentsByPostId(postId) >> { throw new PostNotFoundException("Post with ID: ${postId} not found.") }

        when:
        def response = commentController.getComments(postId)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == "Post with ID: ${postId} not found."
    }

    def "should edit a comment successfully"() {
        given:
        def postId = "postId"
        def commentId = "commentId"
        def updatedComment = new Comment(content: "Updated comment")
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.editComment(postId, commentId, "userId", updatedComment) >> updatedComment

        when:
        def response = commentController.editComment(postId, commentId, updatedComment, userDetails)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Comment with ID: ${commentId} updated successfully."
    }

    def "should return 404 when trying to edit a non-existent comment"() {
        given:
        def postId = "postId"
        def commentId = "nonExistentCommentId"
        def updatedComment = new Comment(content: "Updated comment")
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.editComment(postId, commentId, "userId", updatedComment) >> { throw new CommentNotFoundException("Comment with ID: ${commentId} not found.") }

        when:
        def response = commentController.editComment(postId, commentId, updatedComment, userDetails)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == "Comment with ID: ${commentId} not found."
    }

    def "should return 403 when user is not authorized to edit the comment"() {
        given:
        def postId = "postId"
        def commentId = "commentId"
        def updatedComment = new Comment(content: "Updated comment")
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.editComment(postId, commentId, "userId", updatedComment) >> { throw new UnauthorizedAccessException("You are not authorized to edit this comment.") }

        when:
        def response = commentController.editComment(postId, commentId, updatedComment, userDetails)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
        response.body == "You are not authorized to edit this comment."
    }

    def "should delete a comment successfully"() {
        given:
        def postId = "postId"
        def commentId = "commentId"
        userService.getUserIdByUsername("testUser") >> "userId"

        when:
        def response = commentController.deleteComment(postId, commentId, userDetails)

        then:
        1 * commentService.deleteComment(postId, commentId, "userId")
        response.statusCode == HttpStatus.OK
        response.body == "Comment with ID: ${commentId} deleted successfully."
    }

    def "should return 404 when trying to delete a non-existent comment"() {
        given:
        def postId = "postId"
        def commentId = "nonExistentCommentId"
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.deleteComment(postId, commentId, "userId") >> { throw new CommentNotFoundException("Comment with ID: ${commentId} not found.") }

        when:
        def response = commentController.deleteComment(postId, commentId, userDetails)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == "Comment with ID: ${commentId} not found."
    }

    def "should return 403 when user is not authorized to delete the comment"() {
        given:
        def postId = "postId"
        def commentId = "commentId"
        userService.getUserIdByUsername("testUser") >> "userId"
        commentService.deleteComment(postId, commentId, "userId") >> { throw new UnauthorizedAccessException("You are not authorized to delete this comment.") }

        when:
        def response = commentController.deleteComment(postId, commentId, userDetails)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
        response.body == "You are not authorized to delete this comment."
    }
}

