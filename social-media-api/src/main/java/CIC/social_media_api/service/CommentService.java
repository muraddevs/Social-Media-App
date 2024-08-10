package CIC.social_media_api.service;

import CIC.social_media_api.dto.CommentDTO;
import CIC.social_media_api.dto.UserDTO;
import CIC.social_media_api.entity.Comment;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // Convert User entity to UserDTO
    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getUserName());
    }

    // Convert Comment entity to CommentDTO
    private CommentDTO convertToCommentDTO(Comment comment) {
        UserDTO userDTO = convertToUserDTO(comment.getUser());
        return new CommentDTO(comment.getId(), comment.getDescription(), userDTO, comment.getCreatedDate());
    }

    // Fetch comment by ID and return CommentDTO
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        return comment != null ? convertToCommentDTO(comment) : null;
    }

    // Create a new comment and return CommentDTO
    public CommentDTO createComment(Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        return convertToCommentDTO(savedComment);
    }

    // Fetch comments by post ID and return List<CommentDTO>
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdWithUserAndPost(postId);
        return comments.stream().map(this::convertToCommentDTO).collect(Collectors.toList());
    }

    // Delete a comment by ID
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public long countCommentsByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
