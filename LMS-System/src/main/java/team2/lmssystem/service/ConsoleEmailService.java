package team2.lmssystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Dev-only EmailService that prints emails to the console instead of sending them.
 * Activated when app.email.mode=console in application.properties.
 * Switch to app.email.mode=smtp to use the real EmailServiceImpl.
 */
@Service
@ConditionalOnProperty(name = "app.email.mode", havingValue = "console")
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("\n"
                + "========== [DEV EMAIL - NOT SENT] ==========\n"
                + "TO      : {}\n"
                + "SUBJECT : {}\n"
                + "BODY    :\n{}\n"
                + "============================================",
                to, subject, body);
    }
}
