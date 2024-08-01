package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.repository.PostImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostService postService;

    // Create a new PostImage
    public PostImage createPostImage(PostImage postImage) {
        return postImageRepository.save(postImage);
    }

    // Get all PostImages
    public List<PostImage> getAllPostImages() {
        return postImageRepository.findAll();
    }

    // Get a PostImage by its ID
    public PostImage getPostImageById(Long id) {
        return postImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PostImage not found with ID: " + id));
    }

    // Delete a PostImage by its ID
    public boolean deletePostImageById(Long id) {
        if (postImageRepository.existsById(id)) {
            postImageRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Find PostImages associated with a specific Post ID
    public List<PostImage> findImagesByPostId(Long postId) {
        return postImageRepository.findByPostId(postId);
    }

    // Store an image and associate it with a Post
    public PostImage storeImage(MultipartFile file, Long postId) throws IOException {
        Post post = postService.findPostById(postId);

        PostImage postImage = new PostImage();
        postImage.setName(file.getOriginalFilename());
        postImage.setType(file.getContentType());
        postImage.setData(file.getBytes());
        postImage.setPost(post);

        return postImageRepository.save(postImage);
    }

    // Delete all images associated with a specific Post
    public void deleteImagesByPostId(Long postId) {
        List<PostImage> images = postImageRepository.findByPostId(postId);
        if (!images.isEmpty()) {
            postImageRepository.deleteAll(images);
        }
    }

    // Get the list of existing image IDs
    public List<Long> getExistingImageIds() {
        return postImageRepository.findAll()
                .stream()
                .map(PostImage::getId) // Ensure PostImage has getId()
                .collect(Collectors.toList());
    }

    // Filter posts by valid images
    public List<Post> filterPostsWithValidImages(List<Post> posts) {
        List<Long> existingImageIds = getExistingImageIds();

        return posts.stream()
                .filter(post -> post.getPostImages().stream() // Updated method name
                        .anyMatch(image -> existingImageIds.contains(image.getId()))) // Ensure PostImage has getId()
                .collect(Collectors.toList());
    }
}
