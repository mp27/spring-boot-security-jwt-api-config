package mp27.security.starter.listener;

import lombok.extern.slf4j.Slf4j;
import mp27.security.starter.event.OnRegistrationCompleteEvent;
import mp27.security.starter.model.User;
import mp27.security.starter.service.MailService;
import mp27.security.starter.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService userService;
    private final MailService mailService;


    public RegistrationListener(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event){
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "?token=" + token + "&email=" + user.getEmail();
        String message = "User registered , please confirm your email \n";

        mailService.sendMail(recipientAddress, subject, message + confirmationUrl);
    }
}
