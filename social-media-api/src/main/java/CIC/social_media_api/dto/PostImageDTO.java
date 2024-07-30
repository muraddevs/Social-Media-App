package CIC.social_media_api.dto;

public class PostImageDTO {

    private Long id;
    private String name;
    private String type;
    private String data; // Image URL or path
    private Long postId;

    // Default constructor
    public PostImageDTO() {}

    // Parameterized constructor
    public PostImageDTO(Long id, String name, String type, String data, Long postId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.data = data;
        this.postId = postId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
