package CIC.social_media_api.service;

import CIC.social_media_api.dto.FollowDTO;
import CIC.social_media_api.entity.Follow;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService;

    // Convert Follow entity to FollowDTO
    private FollowDTO convertToDTO(Follow follow) {
        // No ID in DTO now
        return new FollowDTO(follow.getUser().getId(), follow.getFollowing().getId());
    }

    // Convert FollowDTO to Follow entity
    private Follow convertToEntity(FollowDTO followDTO) {
        User user = userService.getUserById(followDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + followDTO.getUserId()));
        User following = userService.getUserById(followDTO.getFollowingId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + followDTO.getFollowingId()));

        // Create Follow entity; no ID needed here either
        return new Follow(user, following);
    }

    public FollowDTO createFollow(FollowDTO followDTO) {
        // Convert FollowDTO to Follow entity
        Follow follow = convertToEntity(followDTO);
        Follow createdFollow = followRepository.save(follow);
        // Convert Follow entity back to FollowDTO
        return convertToDTO(createdFollow);
    }

    public Optional<FollowDTO> getFollowById(long followId) {
        Optional<Follow> follow = followRepository.findById(followId);
        return follow.map(this::convertToDTO);
    }

    public List<FollowDTO> getAllFollows() {
        return followRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteFollowById(long followId) {
        if (followRepository.existsById(followId)) {
            followRepository.deleteById(followId);
        } else {
            throw new IllegalArgumentException("Follow not found with id: " + followId);
        }
    }
}
