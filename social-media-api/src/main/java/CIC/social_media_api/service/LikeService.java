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

    public boolean hasLikedPost(Long userId, Long postId) {
        return getLikeByUserAndPost(userId, postId)
                .map(like -> !like.isDislike())
                .orElse(false);
    }

    public boolean hasDislikedPost(Long userId, Long postId) {
        return getLikeByUserAndPost(userId, postId)
                .map(Like::isDislike)
                .orElse(false);
    }

    public void removeLike(Long userId, Long postId) {
        getLikeByUserAndPost(userId, postId)
                .ifPresent(like -> {
                    if (!like.isDislike()) {
                        likeRepository.delete(like);
                    }
                });
    }

    public void removeDislike(Long userId, Long postId) {
        getLikeByUserAndPost(userId, postId)
                .ifPresent(like -> {
                    if (like.isDislike()) {
                        likeRepository.delete(like);
                    }
                });
    }

    public void createDislike(Like dislike) {
        dislike.setDislike(true);
        createLike(dislike);
    }

    public int getLikeCount(Long postId) {
        return likeRepository.countByPostIdAndDislikeFalse(postId);
    }

    public int getDislikeCount(Long postId) {
        return likeRepository.countByPostIdAndDislikeTrue(postId);
    }
}
