package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Like;
import CIC.social_media_api.entity.Post;
import CIC.social_media_api.service.LikeService;
import CIC.social_media_api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Like> createLike(@RequestBody Like like) {
        // Check if the user has already liked the post
        Like existingLike = likeService.getLikeByUserAndPost(like.getUser().getId(), like.getPost().getId());
        if (existingLike != null) {
            // User has already liked this post
            return ResponseEntity.badRequest().build();
        }

        // Update the like count of the post
        Post post = like.getPost();
        post.setLikeCount(post.getLikeCount() + 1);
        postService.updatePost(post);

        // Save the new like
        Like createdLike = likeService.createLike(like);
        return ResponseEntity.ok(createdLike);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Like> getLikeById(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);
        if (like == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(like);
    }

    @GetMapping
    public List<Like> getAllLikes() {
        return likeService.getAllLikes();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);
        if (like == null) {
            return ResponseEntity.notFound().build();
        }

        // Update the like count of the post
        Post post = like.getPost();
        post.setLikeCount(post.getLikeCount() - 1);
        postService.updatePost(post);

        likeService.deleteLikeById(id);
        return ResponseEntity.noContent().build();
    }
}
