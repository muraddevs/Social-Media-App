package CIC.social_media_api.controller;

import CIC.social_media_api.entity.UserImage;
import CIC.social_media_api.service.UserImageService;
import CIC.social_media_api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-images")
public class UserImageController {

    private static final Logger logger = LoggerFactory.getLogger(UserImageController.class);

    private final UserImageService userImageService;
    private final UserService userService;

    @Autowired
    public UserImageController(UserImageService userImageService, UserService userService) {
        this.userImageService = userImageService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserImage>> getAllUserImages() {
        try {
            List<UserImage> userImages = userImageService.getAllUserImages();
            return ResponseEntity.ok(userImages);
        } catch (Exception e) {
            logger.error("Error retrieving all user images: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable Long userId) {
        try {
            Optional<UserImage> optionalUserImage = userImageService.getProfileImageByUserId(userId);
            if (!optionalUserImage.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UserImage userImage = optionalUserImage.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, userImage.getType());

            return ResponseEntity.ok().headers(headers).body(userImage.getData());
        } catch (Exception e) {
            logger.error("Error retrieving profile image for user ID {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getUserImageById(@PathVariable Long id) {
        try {
            Optional<UserImage> optionalUserImage = Optional.ofNullable(userImageService.getUserImageById(id));
            if (!optionalUserImage.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UserImage userImage = optionalUserImage.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, userImage.getType());

            return ResponseEntity.ok().headers(headers).body(userImage.getData());
        } catch (Exception e) {
            logger.error("Error retrieving user image with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> uploadUserImage(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file provided for upload.");
        }

        try {
            // Check if the user exists
            if (userService.getUserById(userId).isEmpty()) {
                return ResponseEntity.badRequest().body("User not found with ID: " + userId);
            }

            // Check file size (example limit: 5MB)
            long maxFileSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest().body("File size exceeds the limit of 5MB.");
            }

            UserImage userImage = userImageService.storeImage(file, userId);
            return ResponseEntity.ok("Image uploaded successfully: " + userImage.getId());
        } catch (IOException e) {
            logger.error("Error uploading image for user ID {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument error while uploading image for user ID {}: ", userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserImage(@PathVariable Long id) {
        try {
            boolean deleted = userImageService.deleteUserImageById(id);
            if (deleted) {
                return ResponseEntity.ok("Image deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting user image with ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting image: " + e.getMessage());
        }
    }
}
