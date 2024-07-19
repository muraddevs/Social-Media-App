package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return postRepository.findById(id).orElse(null);
    }

    // Find all posts with associated user details
    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    // Delete a post by ID
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // Update an existing post
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }
}
