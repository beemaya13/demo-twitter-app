package com.nilga.demotwitter.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Represents a comment on a post in the Demo Twitter application.
 */
@Document(collection = "comments")
class Comment {

    /**
     * Unique identifier of the comment.
     */
    @Id
    private String id

    /**
     * The ID of the post to which the comment belongs.
     */
    private String postId

    /**
     * The ID of the user who made the comment.
     */
    private String userId

    /**
     * The content of the comment.
     */
    private String content

    /**
     * The timestamp when the comment was created.
     */
    private LocalDateTime createdAt = LocalDateTime.now()

    /**
     * Gets the unique identifier of the comment.
     *
     * @return the ID of the comment.
     */
    String getId() {
        return id
    }

    /**
     * Sets the unique identifier of the comment.
     *
     * @param id the ID to set.
     */
    void setId(String id) {
        this.id = id
    }

    /**
     * Gets the post ID to which this comment belongs.
     *
     * @return the post ID.
     */
    String getPostId() {
        return postId
    }

    /**
     * Sets the post ID to which this comment belongs.
     *
     * @param postId the post ID to set.
     */
    void setPostId(String postId) {
        this.postId = postId
    }

    /**
     * Gets the user ID of the comment author.
     *
     * @return the user ID.
     */
    String getUserId() {
        return userId
    }

    /**
     * Sets the user ID of the comment author.
     *
     * @param userId the user ID to set.
     */
    void setUserId(String userId) {
        this.userId = userId
    }

    /**
     * Gets the content of the comment.
     *
     * @return the comment content.
     */
    String getContent() {
        return content
    }

    /**
     * Sets the content of the comment.
     *
     * @param content the comment content to set.
     */
    void setContent(String content) {
        this.content = content
    }

    /**
     * Gets the creation timestamp of the comment.
     *
     * @return the timestamp when the comment was created.
     */
    LocalDateTime getCreatedAt() {
        return createdAt
    }

    /**
     * Sets the creation timestamp of the comment.
     *
     * @param createdAt the timestamp to set.
     */
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt
    }
}
