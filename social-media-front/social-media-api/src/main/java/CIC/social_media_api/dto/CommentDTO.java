package CIC.social_media_api.dto;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long id;
    private String description;
    private UserDTO user;
    private LocalDateTime createdDate;

    // Default constructor
    public CommentDTO() {}

    // Parameterized constructor
    public CommentDTO(Long id, String description, UserDTO user, LocalDateTime createdDate) {
        this.id = id;
        this.description = description;
        this.user = user;
        this.createdDate = createdDate;
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
