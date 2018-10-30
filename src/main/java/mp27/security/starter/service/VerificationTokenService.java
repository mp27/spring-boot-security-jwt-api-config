package mp27.security.starter.service;

import mp27.security.starter.model.User;
import mp27.security.starter.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService extends CrudService<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
}
