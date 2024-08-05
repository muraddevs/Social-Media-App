package CIC.social_media_api.service;

import CIC.social_media_api.entity.User;
import CIC.social_media_api.entity.UserImage;
import CIC.social_media_api.repository.UserImageRepository;
import CIC.social_media_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class UserImageService {

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserImage> getAllUserImages() {
        return userImageRepository.findAll();
    }

    public UserImage getUserImageById(Long id) {
        return userImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserImage not found with ID: " + id));
    }

    public UserImage storeImage(MultipartFile file, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        UserImage userImage = new UserImage();
        userImage.setName(file.getOriginalFilename());
        userImage.setType(file.getContentType());
        userImage.setData(file.getBytes());
        userImage.setUser(user);

        return userImageRepository.save(userImage);
    }

    public boolean deleteUserImageById(Long id) {
        if (userImageRepository.existsById(id)) {
            userImageRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
