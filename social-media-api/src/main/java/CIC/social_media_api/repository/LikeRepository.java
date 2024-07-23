package CIC.social_media_api.repository;

import CIC.social_media_api.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    List<Like> findByPostId(Long postId);

    int countByPostIdAndDislikeFalse(Long postId);

    int countByPostIdAndDislikeTrue(Long postId);
}
