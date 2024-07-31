package CIC.social_media_api.controller;

import CIC.social_media_api.dto.CommentDTO;
import CIC.social_media_api.entity.Comment;
import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.service.CommentService;
import CIC.social_media_api.service.PostService;
import CIC.social_media_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO commentDTO = commentService.getCommentById(id);
        if (commentDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentDTO);
    }

    // Create a new comment
    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody Map<String, Object> payload) {
        try {
            String description = (String) payload.get("description");
            Long postId = ((Number) payload.get("postId")).longValue();
            Long userId = ((Number) payload.get("userId")).longValue();

            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description cannot be empty");
            }

            Post post = postService.findPostById(postId);
            Optional<User> optionalUser = userService.getUserById(userId);

            if (post == null) {
                return ResponseEntity.badRequest().body("Post not found");
            }

            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = optionalUser.get();
            Comment comment = new Comment(description, post, user);

            CommentDTO createdCommentDTO = commentService.createComment(comment);
            return ResponseEntity.ok("Comment created successfully with ID: " + createdCommentDTO.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // Get comments by Post ID with associated Post and User
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDTO> commentsDTO = commentService.getCommentsByPostId(postId);
        if (commentsDTO.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(commentsDTO);
    }

    // Delete a comment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}
