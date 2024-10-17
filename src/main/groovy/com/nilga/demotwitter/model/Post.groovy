package com.nilga.demotwitter.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Transient
import java.time.LocalDateTime

/**
 * Represents a post in the Demo Twitter application.
 */
@Document(collection = "posts")
class Post {

    /**
     * Unique identifier of the post.
     */
    @Id
    private String id

    /**
     * The ID of the user who created the post.
     */
    private String userId

    /**
     * The content of the post.
     */
    private String content

    /**
     * The timestamp when the post was created.
     */
    private LocalDateTime createdAt = LocalDateTime.now()

    /**
     * A set of user IDs who liked the post.
     */
    private Set<String> likes = new HashSet<>()

    /**
     * Transient list of comments associated with the post.
     * This field is not stored in the database but can be used for runtime processing.
     */
    @Transient
    private List<Comment> comments = new ArrayList<>()

    /**
     * Gets the comments associated with the post.
     *
     * @return the list of comments.
     */
    List<Comment> getComments() {
        return comments
    }

    /**
     * Sets the comments associated with the post.
     *
     * @param comments the list of comments to set.
     */
    void setComments(List<Comment> comments) {
        this.comments = comments
    }

    /**
     * Gets the unique identifier of the post.
     *
     * @return the post ID.
     */
    String getId() {
        return id
    }

    /**
     * Sets the unique identifier of the post.
     *
     * @param id the post ID to set.
     */
    void setId(String id) {
        this.id = id
    }

    /**
     * Gets the user ID of the post creator.
     *
     * @return the user ID.
     */
    String getUserId() {
        return userId
    }

    /**
     * Sets the user ID of the post creator.
     *
     * @param userId the user ID to set.
     */
    void setUserId(String userId) {
        this.userId = userId
    }

    /**
     * Gets the content of the post.
     *
     * @return the post content.
     */
    String getContent() {
        return content
    }

    /**
     * Sets the content of the post.
     *
     * @param content the post content to set.
     */
    void setContent(String content) {
        this.content = content
    }

    /**
     * Gets the timestamp when the post was created.
     *
     * @return the creation timestamp.
     */
    LocalDateTime getCreatedAt() {
        return createdAt
    }

    /**
     * Sets the creation timestamp of the post.
     *
     * @param createdAt the timestamp to set.
     */
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt
    }

    /**
     * Gets the set of user IDs who liked the post.
     *
     * @return the set of likes.
     */
    Set<String> getLikes() {
        return likes
    }

    /**
     * Sets the set of user IDs who liked the post.
     *
     * @param likes the set of likes to set.
     */
    void setLikes(Set<String> likes) {
        this.likes = likes
    }
}
