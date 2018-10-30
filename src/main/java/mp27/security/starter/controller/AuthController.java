package mp27.security.starter.controller;

import lombok.extern.slf4j.Slf4j;
import mp27.security.starter.event.OnRegistrationCompleteEvent;
import mp27.security.starter.event.OnResendConfirmationEvent;
import mp27.security.starter.exception.AppException;
import mp27.security.starter.model.Role;
import mp27.security.starter.model.RoleName;
import mp27.security.starter.model.User;
import mp27.security.starter.model.VerificationToken;
import mp27.security.starter.payload.*;
import mp27.security.starter.security.JwtTokenProvider;
import mp27.security.starter.service.RoleService;
import mp27.security.starter.service.UserService;
import mp27.security.starter.service.VerificationTokenService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final RoleService roleService;

    private final VerificationTokenService verificationTokenService;

    private final ApplicationEventPublisher applicationEventPublisher;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, RoleService roleService, VerificationTokenService verificationTokenService, ApplicationEventPublisher applicationEventPublisher) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.roleService = roleService;
        this.verificationTokenService = verificationTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
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
                signUpRequest.getPassword(),
                false);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleService.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userService.save(user);

        String confirmLocation = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/auth/registration-confirm")
                .toUriString();

        if (result.getId() != null) {
            Locale locale = Locale.getDefault();
            applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(result, locale, confirmLocation));
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @GetMapping("/registration-confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token, @RequestParam("email") String email) {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenService.findByToken(token);

        if (!verificationTokenOptional.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "The provided token  is invalid!"),
                    HttpStatus.BAD_REQUEST);
        }

        VerificationToken verificationToken = verificationTokenOptional.get();
        User user = verificationToken.getUser();

        if (user.getId() != null && user.getEmail().equals(email)) {

            if (verificationToken.isExpired()) {
                return new ResponseEntity<>(new ApiResponse(false, "The provided token  is expired!"),
                        HttpStatus.BAD_REQUEST);
            }

            user.setEnabled(true);
            userService.save(user);
            verificationTokenService.delete(verificationToken);

            return new ResponseEntity<>(new ApiResponse(true, "User confirmed"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(false, "The provided user  is invalid!"),
                HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/resend-confirmation-email")
    public ResponseEntity<?> resendConfirmationEmail(@Valid @RequestBody EmailRequest emailRequest) {
        Optional<User> optionalUser = userService.findByEmail(emailRequest.getEmail());

        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "The provided email  is invalid or doesn't exist!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (user.getEmail().equals(emailRequest.getEmail()) && user.getId() != null) {
            if (!user.getEnabled()) {

                Locale locale = Locale.getDefault();
                String confirmLocation = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/api/auth/registration-confirm")
                        .toUriString();

                applicationEventPublisher.publishEvent(new OnResendConfirmationEvent(user, locale, confirmLocation));

                return new ResponseEntity<>(new ApiResponse(true, "Email sent"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "The user is already confirmed"),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ApiResponse(false, "The provided user  is invalid!"),
                HttpStatus.BAD_REQUEST);
    }
}
