package CIC.social_media_api.service;

import CIC.social_media_api.entity.Follow;
import CIC.social_media_api.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    public Follow createFollow(Follow follow) {
        followRepository.save(follow);
        return follow;
    }
    public Follow getFollowById(long followId) {
        return followRepository.findById(followId).get();
    }
    public List<Follow> getAllFollows() {
        return followRepository.findAll();
    }
    public void deleteFollowById(long followId) {
        followRepository.deleteById(followId);
    }
}
