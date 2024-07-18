package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.service.PostImageService;
import CIC.social_media_api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final PostImageService postImageService;
    private final PostService postService;

    @Autowired
    public PostImageController(PostImageService postImageService, PostService postService) {
        this.postImageService = postImageService;
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostImage>> getAllPostImages() {
        List<PostImage> postImages = postImageService.getAllPostImages();
        return ResponseEntity.ok(postImages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostImage> getPostImageById(@PathVariable Long id) {
        PostImage postImage = postImageService.getPostImageById(id);
        return postImage != null ? ResponseEntity.ok(postImage) : ResponseEntity.notFound().build();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("postId") Long postId) {
        try {
            // Check if the file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Empty file provided for upload.");
            }

            // Find the post by postId
            Post post = postService.findPostById(postId);
            if (post == null) {
                return ResponseEntity.badRequest().body("Post not found with id: " + postId);
            }

            // Store the image associated with the post
            PostImage postImage = postImageService.storeImage(file, post);
            return ResponseEntity.ok(postImage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePostImage(@PathVariable Long id) {
        try {
            postImageService.deletePostImageById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
