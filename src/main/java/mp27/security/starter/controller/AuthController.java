package mp27.security.starter.controller;

import mp27.security.starter.exception.AppException;
import mp27.security.starter.model.Role;
import mp27.security.starter.model.RoleName;
import mp27.security.starter.model.User;
import mp27.security.starter.payload.ApiResponse;
import mp27.security.starter.payload.JwtAuthenticationResponse;
import mp27.security.starter.payload.LoginRequest;
import mp27.security.starter.payload.SignUpRequest;
import mp27.security.starter.repository.RoleRepository;
import mp27.security.starter.security.JwtTokenProvider;
import mp27.security.starter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final RoleRepository roleRepository;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, RoleRepository roleRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken !"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getName(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userService.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
