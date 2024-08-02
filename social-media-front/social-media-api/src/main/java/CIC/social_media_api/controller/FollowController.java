package CIC.social_media_api.controller;

import CIC.social_media_api.dto.FollowDTO;
import CIC.social_media_api.service.FollowService;
import CIC.social_media_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    private static final Logger logger = Logger.getLogger(FollowController.class.getName());

    @PostMapping
    public ResponseEntity<?> createFollow(@RequestBody FollowDTO followDTO) {
        logger.info("Received FollowDTO for creation - UserId: " + followDTO.getUserId() + ", FollowingId: " + followDTO.getFollowingId());

        // Validate followDTO
        if (followDTO.getUserId() == null || followDTO.getFollowingId() == null) {
            logger.warning("Invalid FollowDTO: UserId or FollowingId is null");
            return ResponseEntity.badRequest().body("UserId or FollowingId is null");
        }

        // Check if users exist
        if (!userService.getUserById(followDTO.getUserId()).isPresent() ||
                !userService.getUserById(followDTO.getFollowingId()).isPresent()) {
            logger.warning("User not found: UserId=" + followDTO.getUserId() + ", FollowingId=" + followDTO.getFollowingId());
            return ResponseEntity.notFound().build();
        }

        // Check if already following or self-follow
        if (followDTO.getUserId().equals(followDTO.getFollowingId())) {
            return ResponseEntity.badRequest().body("Cannot follow yourself");
        }

        if (followService.isFollowing(followDTO.getUserId(), followDTO.getFollowingId())) {
            return ResponseEntity.badRequest().body("Already following");
        }

        FollowDTO createdFollowDTO = followService.createFollow(followDTO);
        return ResponseEntity.ok(createdFollowDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFollow(@RequestBody FollowDTO followDTO) {
        logger.info("Received FollowDTO for deletion - UserId: " + followDTO.getUserId() + ", FollowingId: " + followDTO.getFollowingId());

        // Validate followDTO
        if (followDTO.getUserId() == null || followDTO.getFollowingId() == null) {
            logger.warning("Invalid FollowDTO: UserId or FollowingId is null");
            return ResponseEntity.badRequest().body("UserId or FollowingId is null");
        }

        if (followDTO.getUserId().equals(followDTO.getFollowingId())) {
            return ResponseEntity.badRequest().body("Cannot unfollow yourself");
        }

        if (!followService.isFollowing(followDTO.getUserId(), followDTO.getFollowingId())) {
            return ResponseEntity.badRequest().body("Not following");
        }

        followService.deleteFollow(followDTO.getUserId(), followDTO.getFollowingId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfFollowing(@RequestParam Long userId, @RequestParam Long followingId) {
        logger.info("Received check request - UserId: " + userId + ", FollowingId: " + followingId);

        // Validate request parameters
        if (userId == null || followingId == null) {
            logger.warning("Invalid request parameters: UserId or FollowingId is null");
            return ResponseEntity.badRequest().body("UserId or FollowingId is null");
        }

        // Check if users exist
        if (!userService.getUserById(userId).isPresent() ||
                !userService.getUserById(followingId).isPresent()) {
            logger.warning("User not found: UserId=" + userId + ", FollowingId=" + followingId);
            return ResponseEntity.notFound().build();
        }

        // Check if following
        boolean isFollowing = followService.isFollowing(userId, followingId);
        return ResponseEntity.ok(Collections.singletonMap("isFollowing", isFollowing));
    }
}
