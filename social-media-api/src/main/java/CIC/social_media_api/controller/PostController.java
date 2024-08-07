package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.exception.ResourceNotFoundException;
import CIC.social_media_api.service.PostService;
import CIC.social_media_api.service.PostImageService;
import CIC.social_media_api.service.UserService;
import CIC.social_media_api.response.PostResponse;// Import the PostResponse class
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
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        try {
            List<Post> posts = postService.findAllPosts();
            List<PostResponse> postResponses = posts.stream().map(this::convertToPostResponse).toList();
            return ResponseEntity.ok(postResponses);
        } catch (Exception e) {
            logger.error("Error retrieving all posts: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.findPostById(id);
            return post != null ? ResponseEntity.ok(convertToPostResponse(post)) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving post with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponse> createPost(@RequestParam(value = "description", required = false) String description,
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

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToPostResponse(createdPost));

        } catch (Exception e) {
            logger.error("Error creating post: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
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

    private PostResponse convertToPostResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.setDescription(post.getDescription());
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUserId(post.getUser().getId());
        postResponse.setUserName(post.getUser().getUserName());
        postResponse.setPostImages(post.getPostImages().stream()
                .map(img -> new PostResponse.PostImageResponse(img.getName(), img.getType(), img.getData()))
                .toList());
        postResponse.setLikeCount(post.getLikes().size()); // Adjust this if you have a separate like count service
        postResponse.setDislikeCount(post.getDislikes()); // Adjust this if you have a separate dislike count service
        return postResponse;
    }
}
