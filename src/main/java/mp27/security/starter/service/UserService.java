package mp27.security.starter.service;

import mp27.security.starter.model.User;
import mp27.security.starter.model.VerificationToken;

import java.util.Optional;

public interface UserService extends CrudService<User, Long>{
    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    void createVerificationToken(User user, String token);
    VerificationToken getVerificationToken(String verificationToken);
}
