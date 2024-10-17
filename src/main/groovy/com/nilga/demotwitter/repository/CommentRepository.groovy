package com.nilga.demotwitter.repository

import com.nilga.demotwitter.model.Comment
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for managing Comment entities in MongoDB.
 */
@Repository
interface CommentRepository extends MongoRepository<Comment, String> {

    /**
     * Finds all comments associated with a given post ID.
     *
     * @param postId the ID of the post for which to retrieve comments.
     * @return a list of comments for the specified post.
     */
    List<Comment> findByPostId(String postId)
}
