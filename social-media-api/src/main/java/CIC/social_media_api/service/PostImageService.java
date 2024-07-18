package CIC.social_media_api.service;

import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.PostImage;
import CIC.social_media_api.repository.PostImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public PostImage storeImage(MultipartFile file, Post post) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        byte[] data = file.getBytes();

        PostImage postImage = new PostImage();
        postImage.setName(fileName);
        postImage.setType(fileType);
        postImage.setData(data);
        postImage.setPost(post);

        return postImageRepository.save(postImage);
    }
}
