package CIC.social_media_api.repository;

import CIC.social_media_api.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    void deleteByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostIdAndDislike(Long userId, Long postId, boolean dislike);

    boolean existsByUserIdAndPostIdAndDislike(Long userId, Long postId, boolean dislike);

    List<Like> findByPostId(Long postId); // Added method
}
