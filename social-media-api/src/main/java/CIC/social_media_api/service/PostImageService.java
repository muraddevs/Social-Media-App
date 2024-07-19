package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.repository.PostImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;

    public PostImage createPostImage(PostImage postImage) {
        return postImageRepository.save(postImage);
    }

    public PostImage getPostImageById(Long id) {
        return postImageRepository.findById(id).orElse(null);
    }

    public List<PostImage> getAllPostImages() {
        return postImageRepository.findAll();
    }

    public void deletePostImageById(Long id) {
        postImageRepository.deleteById(id);
    }

    public List<PostImage> findImagesByPostId(Long postId) {
        return postImageRepository.findByPostId(postId);
    }

    public PostImage storeImage(MultipartFile file, Post post) throws IOException {
        // Validate the file name
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IOException("Invalid file name");
        }

        // Validate the file type
        String fileType = file.getContentType();
        if (fileType == null || fileType.trim().isEmpty()) {
            throw new IOException("Invalid file type");
        }

        // Get file data
        byte[] data = file.getBytes();
        String base64Data = Base64.getEncoder().encodeToString(data);

        // Create a new PostImage object
        PostImage postImage = new PostImage();
        postImage.setName(sanitizeFileName(fileName));
        postImage.setType(fileType);
        postImage.setData(base64Data); // Store Base64-encoded data as String
        postImage.setPost(post);

        // Save and return the PostImage
        return postImageRepository.save(postImage);
    }

    // Helper method to sanitize the file name
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}
