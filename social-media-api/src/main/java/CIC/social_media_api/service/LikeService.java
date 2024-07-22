package CIC.social_media_api.service;

import CIC.social_media_api.entity.Like;
import CIC.social_media_api.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Like getLikeById(Long id) {
        return likeRepository.findById(id).orElse(null);
    }

    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    public void deleteLikeById(Long id) {
        likeRepository.deleteById(id);
    }

    public Optional<Like> getLikeByUserAndPost(Long userId, Long postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId);
    }

    public void deleteLike(Like like) {
        // Find the like to be deleted
        Optional<Like> existingLike = getLikeByUserAndPost(like.getUser().getId(), like.getPost().getId());
        existingLike.ifPresent(likeRepository::delete);
    }
}
