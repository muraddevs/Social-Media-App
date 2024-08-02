package CIC.social_media_api.service;

import CIC.social_media_api.entity.User;
import CIC.social_media_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(login);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUserName(login);
        }

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user.getUserName(), user.getPassword(), user.getId(), user.getAuthorities());
    }
}