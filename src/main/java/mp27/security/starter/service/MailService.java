package mp27.security.starter.service;

import org.springframework.stereotype.Service;

public interface MailService {
    void sendMail(String recipientAddress, String subject, String message);
}
