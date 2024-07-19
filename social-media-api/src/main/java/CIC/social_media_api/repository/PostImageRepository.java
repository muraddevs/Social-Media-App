package CIC.social_media_api.repository;

import CIC.social_media_api.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // Add method to find images by postId
    List<PostImage> findByPostId(Long postId);
}
