package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Create a new post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Find a post by ID
    public Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }


    // Find all posts
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    // Delete a post by ID
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found with ID: " + id);
        }
        postRepository.deleteById(id);
    }

    // Update an existing post
    public Post updatePost(Post post) {
        if (post.getId() == null || !postRepository.existsById(post.getId())) {
            throw new IllegalArgumentException("Post not found with ID: " + post.getId());
        }
        return postRepository.save(post);
    }
}
