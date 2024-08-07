package CIC.social_media_api.repository;

import CIC.social_media_api.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
