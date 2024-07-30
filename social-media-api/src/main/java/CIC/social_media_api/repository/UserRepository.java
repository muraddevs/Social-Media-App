package CIC.social_media_api.repository;

import CIC.social_media_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    /**
     * Finds a user by either username or email.
     *
     * @param userName the username of the user
     * @param email the email of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUserNameOrEmail(String userName, String email);
}
