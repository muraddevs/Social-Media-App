package CIC.social_media_api.service;

import CIC.social_media_api.dto.PostDTO;
import CIC.social_media_api.dto.PostImageDTO;
import CIC.social_media_api.dto.CommentDTO;
import CIC.social_media_api.dto.LikeDTO;
import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.entity.Comment;
import CIC.social_media_api.entity.Like;
import CIC.social_media_api.repository.PostRepository;
import CIC.social_media_api.repository.UserRepository;
import CIC.social_media_api.repository.CommentRepository;
import CIC.social_media_api.repository.PostImageRepository;
import CIC.social_media_api.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private LikeRepository likeRepository;

    public PostDTO createPost(PostDTO postDTO) {
        Post post = convertToPostEntity(postDTO);
        Post savedPost = postRepository.save(post);
        return convertToPostDTO(savedPost);
    }

    public Optional<PostDTO> findPostById(Long id) {
        return postRepository.findById(id).map(this::convertToPostDTO);
    }

    public List<PostDTO> findAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToPostDTO)
                .collect(Collectors.toList());
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existingPost.setDescription(postDTO.getDescription());
        existingPost.setCreatedAt(postDTO.getCreatedAt());

        Post updatedPost = postRepository.save(existingPost);
        return convertToPostDTO(updatedPost);
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    public List<PostImageDTO> getImagesByPostId(Long postId) {
        return postImageRepository.findByPostId(postId).stream()
                .map(this::convertToPostImageDTO)
                .collect(Collectors.toList());
    }

    public List<LikeDTO> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(this::convertToLikeDTO)
                .collect(Collectors.toList());
    }

    private PostDTO convertToPostDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setDescription(post.getDescription());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUserId(post.getUser().getId());
        dto.setUserName(post.getUser().getUserName());
        dto.setPostImages(getImagesByPostId(post.getId()));
        dto.setComments(getCommentsByPostId(post.getId()));
        dto.setLikes(getLikesByPostId(post.getId()));
        return dto;
    }

    private Post convertToPostEntity(PostDTO dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setDescription(dto.getDescription());
        post.setCreatedAt(dto.getCreatedAt());
        post.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        // Set other fields if necessary
        return post;
    }

    private CommentDTO convertToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setDescription(comment.getDescription());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setPostId(comment.getPost().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getUserName());
        return dto;
    }

    private LikeDTO convertToLikeDTO(Like like) {
        LikeDTO dto = new LikeDTO();
        dto.setId(like.getId());
        dto.setUserId(like.getUser().getId());
        dto.setPostId(like.getPost().getId());
        dto.setDislike(like.isDislike());
        dto.setUserName(like.getUser().getUserName());
        return dto;
    }

    private PostImageDTO convertToPostImageDTO(PostImage postImage) {
        PostImageDTO dto = new PostImageDTO();
        dto.setId(postImage.getId());
        dto.setName(postImage.getName());
        dto.setType(postImage.getType());
        dto.setData(new String(postImage.getData())); // Convert byte[] to String if necessary
        dto.setPostId(postImage.getPost().getId());
        return dto;
    }
}
