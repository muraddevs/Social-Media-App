package CIC.social_media_api.controller;

import CIC.social_media_api.dto.LikeDTO;
import CIC.social_media_api.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeDTO> createLike(@RequestBody LikeDTO likeDTO) {
        LikeDTO createdLike = likeService.createLike(likeDTO);
        return ResponseEntity.ok(createdLike);
    }

    @DeleteMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long userId, @PathVariable Long postId) {
        likeService.removeLike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/post/{postId}/dislike")
    public ResponseEntity<Void> removeDislike(@PathVariable Long userId, @PathVariable Long postId) {
        likeService.removeDislike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/post/{postId}/liked")
    public ResponseEntity<Boolean> hasLikedPost(@PathVariable Long userId, @PathVariable Long postId) {
        return ResponseEntity.ok(likeService.hasLikedPost(userId, postId));
    }

    @GetMapping("/user/{userId}/post/{postId}/disliked")
    public ResponseEntity<Boolean> hasDislikedPost(@PathVariable Long userId, @PathVariable Long postId) {
        return ResponseEntity.ok(likeService.hasDislikedPost(userId, postId));
    }
}
