package CIC.social_media_api.service;

import CIC.social_media_api.dto.PostImageDTO;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.repository.PostImageRepository;
import CIC.social_media_api.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostImageService {

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostRepository postRepository;

    public PostImageDTO createPostImage(PostImageDTO postImageDTO) {
        PostImage postImage = convertToEntity(postImageDTO);
        PostImage savedPostImage = postImageRepository.save(postImage);
        return convertToDTO(savedPostImage);
    }

    public List<PostImageDTO> getAllPostImages() {
        return postImageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PostImageDTO> getPostImageById(Long id) {
        return postImageRepository.findById(id)
                .map(this::convertToDTO);
    }

    public void deletePostImageById(Long id) {
        postImageRepository.deleteById(id);
    }

    public List<PostImageDTO> findImagesByPostId(Long postId) {
        return postImageRepository.findByPostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostImageDTO storeImage(MultipartFile file, PostImageDTO postDTO) throws IOException {
        PostImage postImage = new PostImage();
        postImage.setName(file.getOriginalFilename());
        postImage.setType(file.getContentType());
        postImage.setData(Arrays.toString(file.getBytes())); // Store image data
        postImage.setPost(postRepository.findById(postDTO.getId()).orElse(null));

        PostImage savedPostImage = postImageRepository.save(postImage);
        return convertToDTO(savedPostImage);
    }

    private PostImageDTO convertToDTO(PostImage postImage) {
        PostImageDTO dto = new PostImageDTO();
        dto.setId(postImage.getId());
        dto.setName(postImage.getName());
        dto.setType(postImage.getType());
        dto.setData(new String(postImage.getData())); // Convert byte array to string
        dto.setPostId(postImage.getPost().getId());
        return dto;
    }

    private PostImage convertToEntity(PostImageDTO dto) {
        PostImage postImage = new PostImage();
        postImage.setId(dto.getId());
        postImage.setName(dto.getName());
        postImage.setType(dto.getType());
        postImage.setData(Arrays.toString(dto.getData().getBytes())); // Convert string to byte array
        postImage.setPost(postRepository.findById(dto.getPostId()).orElse(null));
        return postImage;
    }
}
