package CIC.social_media_api.controller;

import CIC.social_media_api.entity.User;
import CIC.social_media_api.entity.UserImage;
import CIC.social_media_api.service.UserImageService;
import CIC.social_media_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-images")
public class UserImageController {

    @Autowired
    private UserImageService userImageService;

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/upload")
    public ResponseEntity<UserImage> uploadImage(@PathVariable Long userId,
                                                 @RequestParam("file") MultipartFile file) throws IOException {
        Optional<User> optionalUser = userService.getUserById(userId);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        UserImage userImage = userImageService.createUserImage(user.getId(), file);
        return ResponseEntity.ok(userImage);
    }

    @GetMapping("/{userId}/download")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long userId) {
        Optional<UserImage> optionalUserImage = userImageService.getUserImageByUserId(userId);
        if (!optionalUserImage.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserImage userImage = optionalUserImage.get();
        ByteArrayResource resource = new ByteArrayResource(userImage.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(userImage.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userImage.getName() + "\"")
                .body(resource);
    }
}
