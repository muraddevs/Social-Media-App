package CIC.social_media_api.repository;

import CIC.social_media_api.entity.Follow;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByUserIdAndFollowingId(Long userId, Long followingId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.user.id = :userId AND f.following.id = :followingId")
    void deleteByUserIdAndFollowingId(@Param("userId") Long userId, @Param("followingId") Long followingId);

    long countByFollowingId(Long followingId);

    long countByUserId(Long userId);

}
