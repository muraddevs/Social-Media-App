package CIC.social_media_api.service;

import CIC.social_media_api.dto.CommentDTO;
import CIC.social_media_api.entity.Comment;
import CIC.social_media_api.repository.CommentRepository;
import CIC.social_media_api.repository.PostRepository;
import CIC.social_media_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<CommentDTO> getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(this::convertToDTO);
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = convertToEntity(commentDTO);
        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setDescription(comment.getDescription());
        dto.setPostId(comment.getPost().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setUserName(comment.getUser().getUserName());
        return dto;
    }

    private Comment convertToEntity(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setDescription(dto.getDescription());
        comment.setCreatedDate(dto.getCreatedDate());
        comment.setPost(postRepository.findById(dto.getPostId()).orElse(null));
        comment.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        return comment;
    }
}
