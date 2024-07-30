package CIC.social_media_api.dto;

public class LikeDTO {

    private Long id;
    private Long userId;
    private Long postId;
    private boolean dislike;
    private String userName; // Add userName for convenience

    // Default constructor
    public LikeDTO() {}

    // Parameterized constructor
    public LikeDTO(Long id, Long userId, Long postId, boolean dislike, String userName) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.dislike = dislike;
        this.userName = userName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
