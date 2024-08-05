package CIC.social_media_api.service;

import CIC.social_media_api.dto.FollowDTO;
import CIC.social_media_api.entity.Follow;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    @Autowired
    public FollowService(FollowRepository followRepository, UserService userService) {
        this.followRepository = followRepository;
        this.userService = userService;
    }

    // Check if a user is already following another user
    public boolean isFollowing(Long userId, Long followingId) {
        return followRepository.existsByUserIdAndFollowingId(userId, followingId);
    }

    // Convert Follow entity to FollowDTO
    private FollowDTO convertToDTO(Follow follow) {
        return new FollowDTO(follow.getUser().getId(), follow.getFollowing().getId());
    }

    // Convert FollowDTO to Follow entity
    private Follow convertToEntity(FollowDTO followDTO) {
        User user = userService.getUserById(followDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + followDTO.getUserId()));
        User following = userService.getUserById(followDTO.getFollowingId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + followDTO.getFollowingId()));

        return new Follow(user, following);
    }

    public FollowDTO createFollow(FollowDTO followDTO) {
        if (isFollowing(followDTO.getUserId(), followDTO.getFollowingId())) {
            throw new IllegalArgumentException("Already following this user");
        }
        Follow follow = convertToEntity(followDTO);
        Follow createdFollow = followRepository.save(follow);
        return convertToDTO(createdFollow);
    }

    @Transactional
    public void deleteFollow(Long userId, Long followingId) {
        if (!isFollowing(userId, followingId)) {
            throw new IllegalArgumentException("Not following this user");
        }
        followRepository.deleteByUserIdAndFollowingId(userId, followingId);
    }

    public Optional<FollowDTO> getFollowById(Long followId) {
        return followRepository.findById(followId).map(this::convertToDTO);
    }

    public List<FollowDTO> getAllFollows() {
        return followRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long countFollowers(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long countFollowing(Long userId) {
        return followRepository.countByUserId(userId);
    }
}
