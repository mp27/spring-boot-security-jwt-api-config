package mp27.security.starter.service;

import mp27.security.starter.model.User;
import mp27.security.starter.model.VerificationToken;
import mp27.security.starter.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VerificationTokenImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public Optional<VerificationToken> findByUser(User user) {
        return verificationTokenRepository.findByUser(user);
    }

    @Override
    public List<VerificationToken> findAll() {
        return verificationTokenRepository.findAll();
    }

    @Override
    public VerificationToken findById(Long aLong) {
        return verificationTokenRepository.findById(aLong).orElse(null);
    }

    @Override
    public List<VerificationToken> findByIdIn(List<Long> longs) {
        return verificationTokenRepository.findByIdIn(longs);
    }

    @Override
    public VerificationToken save(VerificationToken object) {
        return verificationTokenRepository.save(object);
    }

    @Override
    public void delete(VerificationToken object) {
        verificationTokenRepository.delete(object);
    }

    @Override
    public void deleteById(Long aLong) {
        verificationTokenRepository.deleteById(aLong);
    }
}
