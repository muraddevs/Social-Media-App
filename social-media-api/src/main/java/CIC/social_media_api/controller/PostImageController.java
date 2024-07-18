package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.service.PostImageService;
import CIC.social_media_api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post-images")
public class PostImageController {

    @Autowired
    private PostImageService postImageService;

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<PostImage>> getAllPostImages() {
        List<PostImage> postImages = postImageService.getAllPostImages();
        return ResponseEntity.ok(postImages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostImage> getPostImageById(@PathVariable Long id) {
        PostImage postImage = postImageService.getPostImageById(id);
        if (postImage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postImage);
    }

    @PostMapping("/upload")
    public ResponseEntity<PostImage> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("postId") Long postId) {
        try {
            Post post = postService.findPostById(postId);
            if (post == null) {
                return ResponseEntity.badRequest().body(null);
            }

            PostImage postImage = postImageService.storeImage(file, post);
            return ResponseEntity.ok(postImage);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostImage(@PathVariable Long id) {
        postImageService.deletePostImageById(id);
        return ResponseEntity.noContent().build();
    }
}
