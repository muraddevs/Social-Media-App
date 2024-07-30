package CIC.social_media_api.controller;

import CIC.social_media_api.dto.UserDTO;
import CIC.social_media_api.jwt.JwtResponse;
import CIC.social_media_api.jwt.JwtTokenProvider;
import CIC.social_media_api.service.UserService;
import CIC.social_media_api.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("customUserDetailsService")
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {
        try {
            Optional<UserDTO> optionalUser = userService.findByUserNameOrEmail(authRequest.getUsernameOrEmail());

            if (optionalUser.isEmpty()) {
                logger.warn("Authentication failed: User not found for provided username or email.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: User not found");
            }

            UserDTO userDTO = optionalUser.get();
            if (passwordEncoder.matches(authRequest.getPassword(), userDTO.getPassword())) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUserName());
                Long userId = userDTO.getId();
                String jwt = jwtTokenProvider.generateToken(userDetails, userId);
                return ResponseEntity.ok(new JwtResponse(jwt));
            } else {
                logger.warn("Authentication failed: Incorrect password for user.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Incorrect password");
            }
        } catch (Exception e) {
            logger.error("Error authenticating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String name = request.getName();
        String lastName = request.getLastName();
        String role = request.getRole();

        if (userService.existsByUsername(username)) {
            logger.warn("Registration failed: Username is already taken.");
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (userService.existsByEmail(email)) {
            logger.warn("Registration failed: Email is already in use.");
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(password);
        userService.createUser(username, encodedPassword, email, name, lastName, role);

        return ResponseEntity.ok("User registered successfully");
    }

    // Inner class for better readability of request bodies
    static class AuthRequest {
        private String usernameOrEmail;
        private String password;

        public String getUsernameOrEmail() {
            return usernameOrEmail;
        }

        public void setUsernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String name;
        private String lastName;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
