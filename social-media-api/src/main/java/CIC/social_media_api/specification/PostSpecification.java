package CIC.social_media_api.specification;

import CIC.social_media_api.entity.Post;
import org.springframework.data.jpa.domain.Specification;


public class PostSpecification {
    public static Specification<Post> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Post> hasDescription(String description) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<Post> createdAtBetween(String startDate, String endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
    }

}
