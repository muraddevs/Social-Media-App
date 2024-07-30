package CIC.social_media_api.dto;

import java.time.LocalDateTime;

public class CommentDTO {

    private Long id;
    private String description;
    private Long postId;
    private Long userId;
    private LocalDateTime createdDate;
    private String userName; // Add userName for convenience

    // Default constructor
    public CommentDTO() {}

    // Parameterized constructor
    public CommentDTO(Long id, String description, Long postId, Long userId, LocalDateTime createdDate, String userName) {
        this.id = id;
        this.description = description;
        this.postId = postId;
        this.userId = userId;
        this.createdDate = createdDate;
        this.userName = userName;
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

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
