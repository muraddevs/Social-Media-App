package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Like;
import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.service.LikeService;
import CIC.social_media_api.service.PostService;
import CIC.social_media_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<Like> getLikeById(@PathVariable Long id) {
        Like like = likeService.getLikeById(id);
        return like != null ? ResponseEntity.ok(like) : ResponseEntity.notFound().build();
    }

    @PostMapping("/upvote")
    public ResponseEntity<String> upVote(@RequestBody LikeRequest likeRequest, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> optionalUser = userService.findByUserName(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        Long postId = likeRequest.getPostId();
        Optional<Post> optionalPost = Optional.ofNullable(postService.findPostById(postId));
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        Post post = optionalPost.get();
        if (likeService.hasLikedPost(user.getId(), postId)) {
            likeService.removeLike(user.getId(), postId);
            return ResponseEntity.ok("Upvote removed");
        } else {
            if (likeService.hasDislikedPost(user.getId(), postId)) {
                likeService.removeDislike(user.getId(), postId);
            }
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            like.setDislike(false); // Explicitly setting dislike to false
            likeService.createLike(like);
            return ResponseEntity.ok("Post upvoted");
        }
    }

    @PostMapping("/downvote")
    public ResponseEntity<String> downVote(@RequestBody LikeRequest likeRequest, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> optionalUser = userService.findByUserName(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        Long postId = likeRequest.getPostId();
        Optional<Post> optionalPost = Optional.ofNullable(postService.findPostById(postId));
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        Post post = optionalPost.get();
        if (likeService.hasDislikedPost(user.getId(), postId)) {
            likeService.removeDislike(user.getId(), postId);
            return ResponseEntity.ok("Downvote removed");
        } else {
            if (likeService.hasLikedPost(user.getId(), postId)) {
                likeService.removeLike(user.getId(), postId);
            }
            Like dislike = new Like();
            dislike.setUser(user);
            dislike.setPost(post);
            dislike.setDislike(true); // Explicitly setting dislike to true
            likeService.createDislike(dislike);
            return ResponseEntity.ok("Post downvoted");
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getLikesCount(@RequestParam Long postId) {
        int likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/dislikeCount")
    public ResponseEntity<Integer> getDislikesCount(@RequestParam Long postId) {
        int dislikeCount = likeService.getDislikeCount(postId);
        return ResponseEntity.ok(dislikeCount);
    }

    // Inner class to handle request bodies for upvote and downvote
    public static class LikeRequest {
        private Long postId;

        public Long getPostId() {
            return postId;
        }

        public void setPostId(Long postId) {
            this.postId = postId;
        }
    }
}
