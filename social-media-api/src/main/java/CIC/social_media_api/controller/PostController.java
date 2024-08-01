package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.service.PostService;
import CIC.social_media_api.service.PostImageService;
import CIC.social_media_api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private PostImageService postImageService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        try {
            List<Post> posts = postService.findAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            logger.error("Error retrieving all posts: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.findPostById(id);
            return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving post with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Post> createPost(@RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "file", required = false) MultipartFile file,
                                           Authentication authentication) {
        try {
            Optional<User> optionalUser = userService.findByUserName(authentication.getName());

            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            User user = optionalUser.get();
            Post post = new Post();
            post.setUser(user);
            post.setDescription(description);
            post.setCreatedAt(LocalDateTime.now());

            // Save the post first to generate the ID
            Post createdPost = postService.createPost(post);

            // If there's an image, save it and associate it with the post
            if (file != null && !file.isEmpty()) {
                try {
                    PostImage postImage = new PostImage();
                    postImage.setName(file.getOriginalFilename());
                    postImage.setType(file.getContentType());
                    postImage.setData(file.getBytes());
                    postImage.setPost(createdPost); // Associate with the saved post
                    postImageService.createPostImage(postImage);

                    // Update the post with the new image
                    createdPost.getPostImages().add(postImage);
                    postService.updatePost(createdPost);
                } catch (IOException e) {
                    logger.error("Error storing image for post ID {}: ", createdPost.getId(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid argument error while storing image for post ID {}: ", createdPost.getId(), e);
                    return ResponseEntity.badRequest().build();
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);

        } catch (Exception e) {
            logger.error("Error creating post: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting post with ID {}: ", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting post with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{postId}/images")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> uploadPostImage(@PathVariable Long postId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            postImageService.storeImage(file, postId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            logger.error("Error uploading image for post with ID {}: ", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument error while uploading image for post ID {}: ", postId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
