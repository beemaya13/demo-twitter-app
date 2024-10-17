package com.nilga.demotwitter.repository

import com.nilga.demotwitter.model.Post
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for managing Post entities in MongoDB.
 */
@Repository
interface PostRepository extends MongoRepository<Post, String> {

    /**
     * Finds all posts associated with a given user ID.
     *
     * @param userId the ID of the user whose posts are to be retrieved.
     * @return a list of posts created by the specified user.
     */
    List<Post> findAllByUserId(String userId)

    /**
     * Finds all posts created by users whose IDs are in the given set.
     *
     * @param userIds a set of user IDs for which to retrieve posts.
     * @return a list of posts created by the specified users.
     */
    List<Post> findAllByUserIdIn(Set<String> userIds)
}
