package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Like;
import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.jwt.JwtTokenProvider;
import CIC.social_media_api.service.LikeService;
import CIC.social_media_api.service.PostService;
import CIC.social_media_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Like> createLike(@RequestBody Like like, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            // Extract userId from the JWT token
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            // Check if the user has already liked the post
            Optional<Like> existingLike = likeService.getLikeByUserAndPost(userId, like.getPost().getId());
            if (existingLike.isPresent()) {
                // User has already liked this post
                return ResponseEntity.badRequest().body(null);
            }

            // Get the user by ID
            Optional<User> optionalUser = userService.getUserById(userId);
            if (!optionalUser.isPresent()) {
                // User not found
                return ResponseEntity.notFound().build();
            }

            User user = optionalUser.get();
            like.setUser(user);

            // Ensure the post exists
            Optional<Post> optionalPost = Optional.ofNullable(postService.findPostById(like.getPost().getId()));
            if (!optionalPost.isPresent()) {
                // Post not found
                return ResponseEntity.notFound().build();
            }

            Post post = optionalPost.get();
            like.setPost(post);

            // Save the new like
            Like createdLike = likeService.createLike(like);

            // Update the like count of the post
            post.setLikeCount(post.getLikeCount() + 1);
            postService.updatePost(post);

            return ResponseEntity.ok(createdLike);
        } catch (Exception e) {
            // Log the specific error
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }



    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> unlikePost(@RequestParam Long postId, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the token from the Authorization header
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            // Extract userId from the JWT token
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            // Check if the user has liked the post
            Optional<Like> existingLike = likeService.getLikeByUserAndPost(userId, postId);
            if (!existingLike.isPresent()) {
                // Like not found
                return ResponseEntity.notFound().build();
            }

            Like like = existingLike.get();

            // Update the like count of the post
            Post post = like.getPost();
            post.setLikeCount(post.getLikeCount() - 1);
            postService.updatePost(post);

            // Delete the like
            likeService.deleteLikeById(like.getId());

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Log the specific error
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
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
    public ResponseEntity<List<Like>> getAllLikes() {
        List<Like> likes = likeService.getAllLikes();
        return ResponseEntity.ok(likes);
    }
}
