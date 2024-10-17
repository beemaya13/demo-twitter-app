package com.nilga.demotwitter.service

import com.nilga.demotwitter.exception.CommentNotFoundException
import com.nilga.demotwitter.exception.PostNotFoundException
import com.nilga.demotwitter.exception.UnauthorizedAccessException
import com.nilga.demotwitter.model.Comment
import com.nilga.demotwitter.repository.CommentRepository
import com.nilga.demotwitter.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Service class for managing comments.
 */
@Service
class CommentService {

    private final CommentRepository commentRepository
    private final PostRepository postRepository

    /**
     * Constructs a CommentService with the given repositories.
     *
     * @param commentRepository the repository for comments
     * @param postRepository the repository for posts
     */
    @Autowired
    CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository
        this.postRepository = postRepository
    }

    /**
     * Adds a comment to a post.
     *
     * @param postId the ID of the post to comment on
     * @param currentUserId the ID of the user adding the comment
     * @param comment the comment object containing content
     * @return the saved comment
     * @throws PostNotFoundException if the post is not found
     */
    Comment addComment(String postId, String currentUserId, Comment comment) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))

        comment.setPostId(postId)
        comment.setUserId(currentUserId)
        comment.setCreatedAt(LocalDateTime.now())

        return commentRepository.save(comment)
    }

    /**
     * Retrieves comments for a given post.
     *
     * @param postId the ID of the post
     * @return a list of comments associated with the post
     * @throws PostNotFoundException if the post is not found
     */
    List<Comment> getCommentsByPostId(String postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))

        List<Comment> comments = commentRepository.findByPostId(postId)
        return comments.isEmpty() ? [] : comments
    }

    /**
     * Edits an existing comment.
     *
     * @param postId the ID of the post containing the comment
     * @param commentId the ID of the comment to edit
     * @param currentUserId the ID of the user attempting to edit the comment
     * @param updatedComment the updated comment data
     * @return the updated comment
     * @throws PostNotFoundException if the post is not found
     * @throws CommentNotFoundException if the comment is not found
     * @throws UnauthorizedAccessException if the user is not authorized to edit the comment
     */
    Comment editComment(String postId, String commentId, String currentUserId, Comment updatedComment) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with ID $commentId not found."))

        if (!existingComment.userId.equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to edit this comment.")
        }

        existingComment.setContent(updatedComment.getContent())
        existingComment.setCreatedAt(LocalDateTime.now())

        return commentRepository.save(existingComment)
    }

    /**
     * Deletes a comment from a post.
     *
     * @param postId the ID of the post containing the comment
     * @param commentId the ID of the comment to delete
     * @param currentUserId the ID of the user attempting to delete the comment
     * @throws PostNotFoundException if the post is not found
     * @throws CommentNotFoundException if the comment is not found
     * @throws UnauthorizedAccessException if the user is not authorized to delete the comment
     */
    void deleteComment(String postId, String commentId, String currentUserId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with ID $commentId not found."))

        if (!existingComment.userId.equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this comment.")
        }

        commentRepository.delete(existingComment)
    }
}
