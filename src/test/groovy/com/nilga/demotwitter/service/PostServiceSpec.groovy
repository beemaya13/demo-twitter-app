package com.nilga.demotwitter.service

import com.nilga.demotwitter.exception.PostNotFoundException
import com.nilga.demotwitter.exception.UserNotFoundException
import com.nilga.demotwitter.model.Comment
import com.nilga.demotwitter.model.Post
import com.nilga.demotwitter.model.User
import com.nilga.demotwitter.repository.CommentRepository
import com.nilga.demotwitter.repository.PostRepository
import com.nilga.demotwitter.repository.UserRepository
import spock.lang.Specification

class PostServiceSpec extends Specification {

    PostRepository postRepository = Mock(PostRepository)
    UserRepository userRepository = Mock(UserRepository)
    CommentRepository commentRepository = Mock(CommentRepository)
    PostService postService = new PostService(postRepository, userRepository, commentRepository)

    def "should create a new post successfully"() {
        given:
        def userId = "123"
        def post = new Post(content: "New post")
        def savedPost = new Post(id: "post123", userId: userId, content: "New post")
        postRepository.save(_ as Post) >> savedPost

        when:
        def result = postService.createPost(userId, post)

        then:
        result == savedPost
    }

    def "should throw PostNotFoundException when getting a non-existing post by ID"() {
        given:
        def postId = "nonExistingId"
        postRepository.findById(postId) >> Optional.empty()

        when:
        postService.getPostById(postId)

        then:
        thrown(PostNotFoundException)
    }

    def "should return the post when getting an existing post by ID"() {
        given:
        def postId = "123"
        def post = new Post(id: postId, content: "Existing post")
        postRepository.findById(postId) >> Optional.of(post)

        when:
        def result = postService.getPostById(postId)

        then:
        result == post
    }

    def "should edit an existing post successfully"() {
        given:
        def postId = "123"
        def newPostContent = "Updated content"
        def existingPost = new Post(id: postId, content: "Old content")
        def updatedPost = new Post(id: postId, content: newPostContent)

        postRepository.findById(postId) >> Optional.of(existingPost)
        postRepository.save(_ as Post) >> updatedPost

        when:
        def result = postService.editPost(postId, updatedPost)

        then:
        result == updatedPost
    }

    def "should return null when trying to edit a non-existing post"() {
        given:
        def postId = "nonExistingId"
        postRepository.findById(postId) >> Optional.empty()

        when:
        def result = postService.editPost(postId, new Post(content: "Updated content"))

        then:
        result == null
    }

    def "should delete an existing post successfully"() {
        given:
        def postId = "123"
        postRepository.existsById(postId) >> true

        when:
        def result = postService.deletePost(postId)

        then:
        result
        1 * postRepository.deleteById(postId)
    }

    def "should return false when trying to delete a non-existing post"() {
        given:
        def postId = "nonExistingId"
        postRepository.existsById(postId) >> false

        when:
        def result = postService.deletePost(postId)

        then:
        !result
        0 * postRepository.deleteById(_)
    }

    def "should like a post successfully"() {
        given:
        def postId = "123"
        def userId = "user1"
        def post = new Post(id: postId, likes: [] as Set)
        def savedPost = new Post(id: postId, likes: [userId] as Set)

        postRepository.findById(postId) >> Optional.of(post)
        postRepository.save(_ as Post) >> { Post p ->
            p.likes.add(userId)
            return p
        }

        when:
        def result = postService.likePost(postId, userId)

        then:
        result
        post.likes.contains(userId)
        1 * postRepository.save(_ as Post)
    }

    def "should not like a post if already liked"() {
        given:
        def postId = "123"
        def userId = "user1"
        def post = new Post(id: postId, likes: [userId] as Set)
        postRepository.findById(postId) >> Optional.of(post)

        when:
        def result = postService.likePost(postId, userId)

        then:
        !result
        0 * postRepository.save(_)
    }

    def "should unlike a post successfully"() {
        given:
        def postId = "123"
        def userId = "user1"
        def post = new Post(id: postId, likes: [userId] as Set)

        postRepository.findById(postId) >> Optional.of(post)
        postRepository.save(_ as Post) >> { Post p ->
            p.likes.remove(userId)
            return p
        }

        when:
        def result = postService.unlikePost(postId, userId)

        then:
        result
        !post.likes.contains(userId)
        1 * postRepository.save(_ as Post)
    }

    def "should not unlike a post if not liked"() {
        given:
        def postId = "123"
        def userId = "user1"
        def post = new Post(id: postId, likes: [] as Set)
        postRepository.findById(postId) >> Optional.of(post)

        when:
        def result = postService.unlikePost(postId, userId)

        then:
        !result
        0 * postRepository.save(_)
    }

    def "should return posts by user successfully"() {
        given:
        def userId = "user123"
        def user = new User(id: userId)
        def posts = [new Post(userId: userId), new Post(userId: userId)]
        userRepository.findById(userId) >> Optional.of(user)
        postRepository.findAllByUserId(userId) >> posts

        when:
        def result = postService.getPostsByUser(userId)

        then:
        result == posts
    }

    def "should throw UserNotFoundException when getting posts by non-existing user"() {
        given:
        def userId = "nonExistingUser"
        userRepository.findById(userId) >> Optional.empty()

        when:
        postService.getPostsByUser(userId)

        then:
        thrown(UserNotFoundException)
    }

    def "should return feed for user including comments"() {
        given:
        def userId = "user123"
        def followingUserId = "following1"
        def user = new User(id: userId, following: [followingUserId])
        def posts = [new Post(userId: followingUserId, id: "post1"), new Post(userId: followingUserId, id: "post2")]
        def comments = [new Comment(postId: "post1", content: "Great post!"), new Comment(postId: "post2", content: "Nice post!")]

        userRepository.findById(userId) >> Optional.of(user)
        postRepository.findAllByUserIdIn(user.following) >> posts
        commentRepository.findByPostId("post1") >> [comments[0]]
        commentRepository.findByPostId("post2") >> [comments[1]]

        when:
        def result = postService.getFeedForUser(userId)

        then:
        result.size() == 2
        result[0].comments.size() == 1
        result[0].comments[0].content == "Great post!"
        result[1].comments.size() == 1
        result[1].comments[0].content == "Nice post!"
    }
}

