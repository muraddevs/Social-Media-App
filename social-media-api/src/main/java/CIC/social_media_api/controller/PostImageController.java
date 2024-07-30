package CIC.social_media_api.controller;

import CIC.social_media_api.dto.PostImageDTO;
import CIC.social_media_api.service.PostImageService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public ResponseEntity<PostImageDTO> createPostImage(@RequestBody PostImageDTO postImageDTO) {
        PostImageDTO createdPostImage = postImageService.createPostImage(postImageDTO);
        return ResponseEntity.ok(createdPostImage);
    }

    @GetMapping
    public ResponseEntity<List<PostImageDTO>> getAllPostImages() {
        List<PostImageDTO> images = postImageService.getAllPostImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostImageDTO> getPostImageById(@PathVariable Long id) {
        return postImageService.getPostImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostImageById(@PathVariable Long id) {
        postImageService.deletePostImageById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<PostImageDTO> storeImage(@RequestParam("file") MultipartFile file, @RequestParam("postId") Long postId) throws IOException {
        PostImageDTO postImageDTO = new PostImageDTO();
        postImageDTO.setPostId(postId);
        PostImageDTO savedPostImage = postImageService.storeImage(file, postImageDTO);
        return ResponseEntity.ok(savedPostImage);
    }
}
