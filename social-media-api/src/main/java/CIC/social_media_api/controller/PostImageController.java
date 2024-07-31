package CIC.social_media_api.controller;

import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.service.PostImageService;
import CIC.social_media_api.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post-images")
public class PostImageController {

    private static final Logger logger = LoggerFactory.getLogger(PostImageController.class);

    private final PostImageService postImageService;
    private final PostService postService;

    @Autowired
    public PostImageController(PostImageService postImageService, PostService postService) {
        this.postImageService = postImageService;
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostImage>> getAllPostImages() {
        try {
            List<PostImage> postImages = postImageService.getAllPostImages();
            return ResponseEntity.ok(postImages);
        } catch (Exception e) {
            logger.error("Error retrieving all post images: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPostImageById(@PathVariable Long id) {
        try {
            PostImage postImage = postImageService.getPostImageById(id);
            if (postImage == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, postImage.getType());

            return ResponseEntity.ok().headers(headers).body(postImage.getData());
        } catch (Exception e) {
            logger.error("Error retrieving post image with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostImage>> getImagesByPostId(@PathVariable Long postId) {
        try {
            List<PostImage> postImages = postImageService.findImagesByPostId(postId);
            if (postImages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(postImages);
            }
            return ResponseEntity.ok(postImages);
        } catch (Exception e) {
            logger.error("Error retrieving images for post ID {}: ", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("postId") Long postId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file provided for upload.");
        }

        try {
            PostImage postImage = postImageService.storeImage(file, postId);
            return ResponseEntity.ok("Image uploaded successfully: " + postImage.getId());
        } catch (IOException e) {
            logger.error("Error uploading image for post ID {}: ", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument error while uploading image for post ID {}: ", postId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePostImage(@PathVariable Long id) {
        try {
            postImageService.deletePostImageById(id);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting post image with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting image: " + e.getMessage());
        }
    }
}
