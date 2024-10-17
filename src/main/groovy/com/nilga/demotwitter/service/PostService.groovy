package com.nilga.demotwitter.service

import com.nilga.demotwitter.exception.PostNotFoundException
import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.Comment
import com.nilga.demotwitter.model.Post
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.repository.CommentRepository
import com.nilga.demotwitter.repository.PostRepository
import com.nilga.demotwitter.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service class for managing posts.
 */
@Service
class PostService {

    private final PostRepository postRepository
    private final UserRepository userRepository
    private final CommentRepository commentRepository

    /**
     * Constructs a PostService with the given repositories.
     *
     * @param postRepository the repository for managing posts
     * @param userRepository the repository for managing users
     * @param commentRepository the repository for managing comments
     */
    @Autowired
    PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository
        this.userRepository = userRepository
        this.commentRepository = commentRepository
    }

    /**
     * Creates a new post for a user.
     *
     * @param userId the ID of the user creating the post
     * @param post the post to be created
     * @return the created post
     */
    Post createPost(String userId, Post post) {
        post.setUserId(userId)
        return postRepository.save(post)
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post
     * @return the post with the specified ID
     * @throws PostNotFoundException if the post is not found
     */
    Post getPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))
    }

    /**
     * Edits an existing post.
     *
     * @param postId the ID of the post to edit
     * @param newPost the new post data
     * @return the updated post or null if not found
     */
    Post editPost(String postId, Post newPost) {
        Optional<Post> postOptional = postRepository.findById(postId)
        if (postOptional.isPresent()) {
            Post post = postOptional.get()
            post.setContent(newPost.getContent())
            return postRepository.save(post)
        }
        return null
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return true if the post was deleted, false if not found
     */
    boolean deletePost(String postId) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId)
            return true
        }
        return false
    }

    /**
     * Likes a post by a user.
     *
     * @param postId the ID of the post to like
     * @param userId the ID of the user liking the post
     * @return true if the like was added, false if the user already liked the post
     * @throws PostNotFoundException if the post is not found
     */
    boolean likePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))

        if (post.likes.contains(userId)) {
            return false  // User already liked the post
        }

        post.likes.add(userId)
        postRepository.save(post)
        return true
    }

    /**
     * Removes a like from a post by a user.
     *
     * @param postId the ID of the post to unlike
     * @param userId the ID of the user unliking the post
     * @return true if the like was removed, false if the user had not liked the post
     * @throws PostNotFoundException if the post is not found
     */
    boolean unlikePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID: $postId not found."))

        if (post.likes.contains(userId)) {
            post.likes.remove(userId)
            postRepository.save(post)
            return true  // Like was removed
        }
        return false  // User had not liked this post
    }

    /**
     * Retrieves all posts made by a specific user.
     *
     * @param userId the ID of the user whose posts to retrieve
     * @return the list of posts made by the user
     * @throws UserNotFoundException if the user is not found
     */
    List<Post> getPostsByUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID $userId not found."))

        return postRepository.findAllByUserId(userId)
    }

    /**
     * Retrieves the feed for a user, including posts from users they follow.
     *
     * @param userId the ID of the user whose feed to retrieve
     * @return the list of posts from users the user follows, with comments included
     * @throws UserNotFoundException if the user is not found
     */
    List<Post> getFeedForUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID $userId not found."))

        List<Post> posts = postRepository.findAllByUserIdIn(user.following)

        posts.each { post ->
            List<Comment> comments = commentRepository.findByPostId(post.getId())
            post.setComments(comments)
        }
        return posts
    }
}
