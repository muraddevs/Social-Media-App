package CIC.social_media_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {

    private Long id;
    private String description;  // Match entity field name
    private LocalDateTime createdAt;
    private Long userId;
    private String userName; // Add userName for convenience
    private List<PostImageDTO> postImages;
    private List<CommentDTO> comments;
    private List<LikeDTO> likes;

    // Default constructor
    public PostDTO() {}

    // Parameterized constructor
    public PostDTO(Long id, String description, LocalDateTime createdAt, Long userId, String userName, List<PostImageDTO> postImages, List<CommentDTO> comments, List<LikeDTO> likes) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
        this.postImages = postImages;
        this.comments = comments;
        this.likes = likes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<PostImageDTO> getPostImages() {
        return postImages;
    }

    public void setPostImages(List<PostImageDTO> postImages) {
        this.postImages = postImages;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<LikeDTO> getLikes() {
        return likes;
    }

    public void setLikes(List<LikeDTO> likes) {
        this.likes = likes;
    }
}
