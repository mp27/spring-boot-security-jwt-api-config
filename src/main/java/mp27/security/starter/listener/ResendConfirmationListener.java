package mp27.security.starter.listener;

import lombok.extern.slf4j.Slf4j;
import mp27.security.starter.event.OnResendConfirmationEvent;
import mp27.security.starter.model.User;
import mp27.security.starter.model.VerificationToken;
import mp27.security.starter.service.MailService;
import mp27.security.starter.service.UserService;
import mp27.security.starter.service.VerificationTokenService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ResendConfirmationListener implements ApplicationListener<OnResendConfirmationEvent> {
    private final UserService userService;
    private final MailService mailService;
    private final VerificationTokenService verificationTokenService;

    public ResendConfirmationListener(UserService userService, MailService mailService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.mailService = mailService;
        this.verificationTokenService = verificationTokenService;
    }

    @Override
    public void onApplicationEvent(OnResendConfirmationEvent event) {
        confirmResend(event);
    }

    private void confirmResend(OnResendConfirmationEvent event) {
        User user = event.getUser();
        String token;
        if (!user.getEnabled()) {
            Optional<VerificationToken> verificationTokenOptional = verificationTokenService.findByUser(user);
            if (verificationTokenOptional.isPresent()) {
                VerificationToken verificationToken = verificationTokenOptional.get();
                if (!verificationToken.isExpired()) {
                    token = verificationToken.getToken();
                } else {
                    token = UUID.randomUUID().toString();
                    verificationTokenService.deleteById(verificationToken.getId());
                    userService.createVerificationToken(user, token);
                }
            } else {
                token = UUID.randomUUID().toString();
                userService.createVerificationToken(user, token);
            }

            String recipientAddress = user.getEmail();
            String subject = "Registration Confirmation resent";
            String confirmationUrl = event.getAppUrl() + "?token=" + token + "&email=" + user.getEmail();
            String message = "Click the new link for confirmation \n";

            mailService.sendMail(recipientAddress, subject, message + confirmationUrl);
        }
    }
}
