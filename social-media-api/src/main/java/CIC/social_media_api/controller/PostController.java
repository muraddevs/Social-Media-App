package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.service.PostService;
import CIC.social_media_api.service.PostImageService;
import CIC.social_media_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostImageService postImageService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.findPostById(id);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPost(@RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "file", required = false) MultipartFile file,
                                        Authentication authentication) {
        try {
            String username = authentication.getName(); // Get authenticated user's username

            Optional<User> optionalUser = userService.findByUserName(username);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found: " + username);
            }

            User user = optionalUser.get();

            Post post = new Post();
            post.setUser(user); // Set the User entity as the post's user

            if (description != null && !description.isEmpty()) {
                post.setDescription(description);
            }

            post.setCreatedAt(LocalDateTime.now()); // Set createdAt explicitly

            if (file != null && !file.isEmpty()) {
                PostImage postImage = postImageService.storeImage(file, post);
                post.getPostImages().add(postImage);
            }

            Post createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating post: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
