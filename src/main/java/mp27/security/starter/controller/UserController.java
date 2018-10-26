package mp27.security.starter.controller;

import lombok.extern.slf4j.Slf4j;
import mp27.security.starter.exception.ResourceNotFoundException;
import mp27.security.starter.model.User;
import mp27.security.starter.payload.UserIdentityAvailability;
import mp27.security.starter.payload.UserProfile;
import mp27.security.starter.payload.UserSummary;
import mp27.security.starter.security.CurrentUser;
import mp27.security.starter.security.UserPrincipal;
import mp27.security.starter.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        return  new UserSummary(currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getName());
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value="username") String username){
        Boolean isAvailable = !userService.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userService.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable String username){
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return new UserProfile(user.getId(), user.getUsername(), user.getName(),
                user.getCreatedAt());
    }
}
