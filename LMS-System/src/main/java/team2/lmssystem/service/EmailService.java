package team2.lmssystem.service;

/**
 * Contract for sending outbound emails from the LMS.
 *
 * <p>Implemented by {@link EmailServiceImpl} using Spring's
 * {@link org.springframework.mail.javamail.JavaMailSender}.</p>
 *
 * <p>Currently used for one purpose: delivering the password-reset OTP
 * to a user's registered email address. Additional notification types
 * (e.g. leave approval/rejection alerts) can be added here as needed.</p>
 */
public interface EmailService {

    /**
     * Sends a plain-text email to a single recipient.
     *
     * @param to      the recipient's email address
     * @param subject the email subject line
     * @param body    the plain-text email body content
     */
    void sendEmail(String to, String subject, String body);
}
