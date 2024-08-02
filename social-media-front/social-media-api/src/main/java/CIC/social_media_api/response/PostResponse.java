package CIC.social_media_api.response;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    private Long id;
    private String description;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private List<PostImageResponse> postImages;
    private int likeCount;
    private int dislikeCount;

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

    public List<PostImageResponse> getPostImages() {
        return postImages;
    }

    public void setPostImages(List<PostImageResponse> postImages) {
        this.postImages = postImages;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    // Nested class for post image response
    public static class PostImageResponse {
        private String name;
        private String type;
        private byte[] data;

        public PostImageResponse() {
        }

        public PostImageResponse(String name, String type, byte[] data) {
            this.name = name;
            this.type = type;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}
