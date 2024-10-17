package com.nilga.demotwitter.repository

import com.nilga.demotwitter.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for managing User entities in MongoDB.
 */
@Repository
interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to retrieve.
     * @return the User object with the specified username, or null if no user is found.
     */
    User findByUsername(String username)
}
