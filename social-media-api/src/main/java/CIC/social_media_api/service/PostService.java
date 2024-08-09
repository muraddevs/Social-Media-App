package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.exception.ResourceNotFoundException;
import CIC.social_media_api.repository.PostImageRepository;
import CIC.social_media_api.repository.PostRepository;
import CIC.social_media_api.repository.CommentRepository;
import CIC.social_media_api.repository.LikeRepository; // Import LikeRepository
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository; // Add LikeRepository

    // Create a new post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Find a post by ID
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));
    }

    // Find all posts
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    // Delete a post by ID
    @Transactional
    public void deletePost(Long postId) {
        System.out.println("Attempting to delete post with id: " + postId);

        // Fetch the post with related entities initialized
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + postId));

        // Initialize collections to avoid lazy loading issues
        Hibernate.initialize(post.getComments());
        Hibernate.initialize(post.getLikes());
        Hibernate.initialize(post.getPostImages());

        try {
            // Step 1: Delete Post's Images
            if (!post.getPostImages().isEmpty()) {
                System.out.println("Deleting post images: " + post.getPostImages().size());
                postImageRepository.deleteAll(post.getPostImages());
            }

            // Step 2: Delete Post's Likes
            if (!post.getLikes().isEmpty()) {
                System.out.println("Deleting post likes: " + post.getLikes().size());
                likeRepository.deleteAll(post.getLikes());
            }

            // Step 3: Delete Post's Comments
            if (!post.getComments().isEmpty()) {
                System.out.println("Deleting post comments: " + post.getComments().size());
                commentRepository.deleteAll(post.getComments());
            }

            // Step 4: Delete the Post itself
            System.out.println("Deleting the post with id: " + postId);
            postRepository.delete(post);

            // Verify deletion (for debugging purposes)
            if (!postRepository.existsById(postId)) {
                System.out.println("Post deleted successfully");
            } else {
                System.out.println("Post still exists after deletion");
            }
        } catch (Exception e) {
            System.err.println("Error occurred during post deletion: " + e.getMessage());
            throw new RuntimeException("Failed to delete post with id " + postId);
        }
    }





    // Update an existing post
    public Post updatePost(Post post) {
        if (post.getId() == null || !postRepository.existsById(post.getId())) {
            throw new ResourceNotFoundException("Post not found with ID: " + post.getId());
        }
        return postRepository.save(post);
    }
}
