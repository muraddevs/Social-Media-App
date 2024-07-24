package CIC.social_media_api.controller;

import CIC.social_media_api.entity.Comment;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Map<String, Object> payload) {
        String description = (String) payload.get("description");
        Long postId = ((Number) payload.get("postId")).longValue();
        Long userId = ((Number) payload.get("userId")).longValue();

        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setPostId(postId);
        User user = new User();
        user.setId(userId);
        comment.setUser(user);
        comment.setCreatedDate(LocalDateTime.now()); // Set the current date and time

        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }


    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
