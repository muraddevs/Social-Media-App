package CIC.social_media_api.dto;


public class UserDTO {
    private Long id;
    private String userName;

    // Default constructor
    public UserDTO() {}

    // Parameterized constructor
    public UserDTO(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
