package CIC.social_media_api.service;

import CIC.social_media_api.dto.LikeDTO;
import CIC.social_media_api.dto.PostDTO;
import CIC.social_media_api.dto.UserDTO;
import CIC.social_media_api.entity.Like;
import CIC.social_media_api.entity.Post;
import CIC.social_media_api.entity.User;
import CIC.social_media_api.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public LikeDTO getLikeById(Long id) {
        return likeRepository.findById(id)
                .map(like -> new LikeDTO(
                        like.getId(),
                        like.getUser().getId(),
                        like.getPost().getId(),
                        like.isDislike(),
                        like.getUser().getUserName()
                ))
                .orElse(null);
    }

    public LikeDTO createLike(LikeDTO likeDTO) {
        Like like = new Like();

        // Fetch User entity from UserService
        User user = userService.getUserById(likeDTO.getUserId())
                .map(dto -> {
                    User u = new User();
                    u.setId(dto.getId());
                    // Populate other fields if needed
                    return u;
                })
                .orElse(null);
        like.setUser(user);

        // Get Post entity from PostService
        Post post = postService.findPostById(likeDTO.getPostId())
                .map(dto -> {
                    Post p = new Post();
                    p.setId(dto.getId());
                    return p;
                })
                .orElse(null);

        like.setPost(post);
        like.setDislike(likeDTO.isDislike());
        Like savedLike = likeRepository.save(like);

        return new LikeDTO(
                savedLike.getId(),
                savedLike.getUser().getId(),
                savedLike.getPost().getId(),
                savedLike.isDislike(),
                savedLike.getUser().getUserName()
        );
    }

    public void removeLike(Long userId, Long postId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public void removeDislike(Long userId, Long postId) {
        likeRepository.deleteByUserIdAndPostIdAndDislike(userId, postId, true);
    }

    public boolean hasLikedPost(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostIdAndDislike(userId, postId, false);
    }

    public boolean hasDislikedPost(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostIdAndDislike(userId, postId, true);
    }
}
