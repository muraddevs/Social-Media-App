package CIC.social_media_api.service;

import CIC.social_media_api.entity.Like;
import CIC.social_media_api.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    public Like getLikeById(Long id) {
        return likeRepository.findById(id).orElse(null);
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public void deleteLikeById(Long id) {
        likeRepository.deleteById(id);
    }

    public Like getLikeByUserAndPost(Long userId, Long postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId);
    }
}
