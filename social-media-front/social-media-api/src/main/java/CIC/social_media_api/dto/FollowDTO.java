package CIC.social_media_api.dto;

public class FollowDTO {
    private Long id;
    private Long userId;
    private Long followingId;

    // Default constructor
    public FollowDTO() {}

    // Parameterized constructor with id
    public FollowDTO(Long id, Long userId, Long followingId) {
        this.id = id;
        this.userId = userId;
        this.followingId = followingId;
    }

    // Parameterized constructor without id
    public FollowDTO(Long userId, Long followingId) {
        this.userId = userId;
        this.followingId = followingId;
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

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}
