package CIC.social_media_api.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Arrays;

public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", user.getId());
        gen.writeStringField("name", user.getName());
        gen.writeStringField("lastName", user.getLastName());
        gen.writeStringField("userName", user.getUserName());
        gen.writeStringField("email", user.getEmail());

        // Serialize the list of posts
        gen.writeArrayFieldStart("posts");
        for (Post post : user.getPosts()) {
            gen.writeStartObject();
            gen.writeNumberField("id", post.getId());
            gen.writeStringField("description", post.getDescription());
            gen.writeStringField("createdAt", post.getCreatedAt().toString());

            // Serialize the list of post images
            gen.writeArrayFieldStart("postImages");
            for (PostImage postImage : post.getPostImages()) {
                gen.writeStartObject();
                gen.writeNumberField("id", postImage.getId());
                gen.writeStringField("url", Arrays.toString(postImage.getData()));
                gen.writeEndObject();
            }
            gen.writeEndArray();

            // Serialize the list of comments
            gen.writeArrayFieldStart("comments");
            for (Comment comment : post.getComments()) {
                gen.writeStartObject();
                gen.writeNumberField("id", comment.getId());
                gen.writeStringField("description", comment.getDescription());
                gen.writeStringField("createdAt", comment.getCreatedDate().toString());
                gen.writeEndObject();
            }
            gen.writeEndArray();

            // Serialize the number of likes (both total likes and dislikes)
            gen.writeNumberField("totalLikes", post.getLikes().size());
            gen.writeNumberField("dislikes", post.getDislikes());

            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
