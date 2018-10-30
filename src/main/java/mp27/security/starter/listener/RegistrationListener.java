package mp27.security.starter.listener;

import lombok.extern.slf4j.Slf4j;
import mp27.security.starter.event.OnRegistrationCompleteEvent;
import mp27.security.starter.model.User;
import mp27.security.starter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService userService;

    private MessageSource messageSource;

    @Autowired
    private JavaMailSender mailSender;

    public RegistrationListener(UserService userService, MessageSource messageSource, JavaMailSender mailSender) {
        this.userService = userService;
        this.messageSource = messageSource;
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

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(recipientAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message  + confirmationUrl);
        mailSender.send(simpleMailMessage);
    }
}
