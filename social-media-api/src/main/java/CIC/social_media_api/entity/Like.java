package CIC.social_media_api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private Post post;

    @Column(name = "dislike", nullable = false)
    private boolean dislike = false;  // New field to differentiate between likes and dislikes

    // Default constructor
    public Like() {}

    // Parameterized constructor
    public Like(@NotNull User user, @NotNull Post post, boolean dislike) {
        this.user = user;
        this.post = post;
        this.dislike = dislike;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return dislike == like.dislike &&
                Objects.equals(user, like.user) &&
                Objects.equals(post, like.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post, dislike);
    }

    // toString method
    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUserName() : "Unknown") +
                ", post=" + (post != null ? post.getId() : "Unknown") +
                ", dislike=" + dislike +
                '}';
    }
}
