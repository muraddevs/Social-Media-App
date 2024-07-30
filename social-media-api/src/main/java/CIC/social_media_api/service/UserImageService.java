package CIC.social_media_api.service;

import CIC.social_media_api.dto.UserDTO;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.entity.UserImage;
import CIC.social_media_api.repository.UserImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserImageService {

    @Autowired
    private UserImageRepository userImageRepository;

    @Autowired
    private UserService userService;

    public UserImage createUserImage(Long userId, MultipartFile file) throws IOException {
        // Fetch the User entity from UserService
        Optional<User> optionalUser = userService.getUserById(userId)
                .map(dto -> {
                    User user = new User();
                    user.setId(dto.getId());
                    // Set other fields if necessary
                    return user;
                });

        if (optionalUser.isPresent()) {
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            byte[] data = file.getBytes();

            UserImage userImage = new UserImage();
            userImage.setName(fileName);
            userImage.setType(fileType);
            userImage.setData(data);
            userImage.setUser(optionalUser.get());

            return userImageRepository.save(userImage);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    public Optional<UserImage> getUserImageByUserId(Long userId) {
        return userImageRepository.findByUserId(userId); // Assuming findByUserId is correctly implemented in UserImageRepository
    }

    public void deleteUserImage(Long id) {
        userImageRepository.deleteById(id);
    }
}
