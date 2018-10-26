package mp27.security.starter.controller;

import lombok.extern.slf4j.Slf4j;
import mp27.security.starter.exception.ResourceNotFoundException;
import mp27.security.starter.model.User;
import mp27.security.starter.payload.UserIdentityAvailability;
import mp27.security.starter.payload.UserProfile;
import mp27.security.starter.payload.UserSummary;
import mp27.security.starter.repository.UserRepository;
import mp27.security.starter.security.CurrentUser;
import mp27.security.starter.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        UserSummary userSummary = new UserSummary(currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getName());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value="username") String username){
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return new UserProfile(user.getId(), user.getUsername(), user.getName(),
                user.getCreatedAt());
    }
}
