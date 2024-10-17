package com.nilga.demotwitter.service

import com.nilga.demotwitter.exception.CommentNotFoundException
import com.nilga.demotwitter.exception.PostNotFoundException
import com.nilga.demotwitter.exception.UnauthorizedAccessException
import com.nilga.demotwitter.model.Comment
import com.nilga.demotwitter.model.Post
import com.nilga.demotwitter.repository.CommentRepository
import com.nilga.demotwitter.repository.PostRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class CommentServiceSpec extends Specification {

    CommentRepository commentRepository = Mock(CommentRepository)
    PostRepository postRepository = Mock(PostRepository)

    @Subject
    CommentService commentService = new CommentService(commentRepository, postRepository)

    def "should add a comment to a post successfully"() {
        given:
        def postId = "post123"
        def currentUserId = "user123"
        def comment = new Comment(content: "Great post!")
        def savedComment = new Comment(id: "comment123", postId: postId, userId: currentUserId, content: comment.content, createdAt: LocalDateTime.now())

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.save(_) >> savedComment

        when:
        def result = commentService.addComment(postId, currentUserId, comment)

        then:
        result == savedComment
    }

    def "should throw PostNotFoundException when adding a comment to a non-existing post"() {
        given:
        def postId = "nonExistingPost"
        def currentUserId = "user123"
        def comment = new Comment(content: "Great post!")

        postRepository.findById(postId) >> Optional.empty()

        when:
        commentService.addComment(postId, currentUserId, comment)

        then:
        thrown(PostNotFoundException)
    }

    def "should get comments by post ID successfully"() {
        given:
        def postId = "post123"
        def comments = [
                new Comment(id: "comment1", postId: postId, content: "Nice!", userId: "user1"),
                new Comment(id: "comment2", postId: postId, content: "Interesting!", userId: "user2")
        ]

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findByPostId(postId) >> comments

        when:
        def result = commentService.getCommentsByPostId(postId)

        then:
        result == comments
    }

    def "should return empty list when no comments found for a post"() {
        given:
        def postId = "post123"

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findByPostId(postId) >> []

        when:
        def result = commentService.getCommentsByPostId(postId)

        then:
        result.isEmpty()
    }

    def "should throw PostNotFoundException when getting comments for a non-existing post"() {
        given:
        def postId = "nonExistingPost"

        postRepository.findById(postId) >> Optional.empty()

        when:
        commentService.getCommentsByPostId(postId)

        then:
        thrown(PostNotFoundException)
    }

    def "should edit a comment successfully"() {
        given:
        def postId = "post123"
        def commentId = "comment123"
        def currentUserId = "user123"
        def updatedComment = new Comment(content: "Updated content")
        def existingComment = new Comment(id: commentId, postId: postId, userId: currentUserId, content: "Old content")
        def savedComment = new Comment(id: commentId, postId: postId, userId: currentUserId, content: "Updated content", createdAt: LocalDateTime.now())

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findById(commentId) >> Optional.of(existingComment)
        commentRepository.save(_ as Comment) >> savedComment

        when:
        def result = commentService.editComment(postId, commentId, currentUserId, updatedComment)

        then:
        result == savedComment
    }

    def "should throw UnauthorizedAccessException when trying to edit another user's comment"() {
        given:
        def postId = "post123"
        def commentId = "comment123"
        def currentUserId = "user456"
        def updatedComment = new Comment(content: "Updated content")
        def existingComment = new Comment(id: commentId, postId: postId, userId: "user123", content: "Old content")

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findById(commentId) >> Optional.of(existingComment)

        when:
        commentService.editComment(postId, commentId, currentUserId, updatedComment)

        then:
        thrown(UnauthorizedAccessException)
    }

    def "should delete a comment successfully"() {
        given:
        def postId = "post123"
        def commentId = "comment123"
        def currentUserId = "user123"
        def existingComment = new Comment(id: commentId, postId: postId, userId: currentUserId)

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findById(commentId) >> Optional.of(existingComment)

        when:
        commentService.deleteComment(postId, commentId, currentUserId)

        then:
        1 * commentRepository.delete(existingComment)
    }

    def "should throw UnauthorizedAccessException when trying to delete another user's comment"() {
        given:
        def postId = "post123"
        def commentId = "comment123"
        def currentUserId = "user456"
        def existingComment = new Comment(id: commentId, postId: postId, userId: "user123")

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findById(commentId) >> Optional.of(existingComment)

        when:
        commentService.deleteComment(postId, commentId, currentUserId)

        then:
        thrown(UnauthorizedAccessException)
    }

    def "should throw CommentNotFoundException when trying to delete non-existing comment"() {
        given:
        def postId = "post123"
        def commentId = "nonExistingComment"
        def currentUserId = "user123"

        postRepository.findById(postId) >> Optional.of(new Post(id: postId))
        commentRepository.findById(commentId) >> Optional.empty()

        when:
        commentService.deleteComment(postId, commentId, currentUserId)

        then:
        thrown(CommentNotFoundException)
    }
}

