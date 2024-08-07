package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.exception.ResourceNotFoundException;
import CIC.social_media_api.repository.PostImageRepository;
import CIC.social_media_api.repository.PostRepository;
import CIC.social_media_api.repository.CommentRepository;
import CIC.social_media_api.repository.LikeRepository; // Import LikeRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

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

        // Fetch the post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + postId));

        // Log associated entities (for debugging purposes)
        System.out.println("Deleting post with comments: " + post.getComments());
        System.out.println("Deleting post with likes: " + post.getLikes());
        System.out.println("Deleting post with images: " + post.getPostImages());

        // Remove associated likes and comments
        likeRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);

        // Remove images related to the post
        postImageRepository.deleteByPostId(postId);

        // Remove the post itself
        postRepository.delete(post);

        // Verify deletion (for debugging purposes)
        if (!postRepository.existsById(postId)) {
            System.out.println("Post deleted successfully");
        } else {
            System.out.println("Post still exists after deletion");
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
