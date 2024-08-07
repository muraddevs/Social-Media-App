package CIC.social_media_api.repository;

import CIC.social_media_api.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Post p WHERE p.id = :id")
    void deleteByIdCustom(@Param("id") Long id);
}
