package CIC.social_media_api.service;

import CIC.social_media_api.entity.User;
import CIC.social_media_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends CustomUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public User createUser(String userName, String password, String email, String name, String lastName, String role) {
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalArgumentException("Username already exists: " + userName);
        }

        User user = new User();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password)); // Encode password before saving
        user.setEmail(email);
        user.setName(name);
        user.setLastName(lastName);
        user.setRole(role != null ? role : "ROLE_USER"); // Set default role if not provided
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(userDetails.getName());
            existingUser.setLastName(userDetails.getLastName());
            existingUser.setUserName(userDetails.getUserName());
            existingUser.setEmail(userDetails.getEmail());
            // Only update password if it's not empty
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            existingUser.setRole(userDetails.getRole());
            return userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userName));
    }

    public boolean existsByUsername(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
