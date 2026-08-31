package team2.lmssystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Sends email through the Resend HTTP API (https, port 443) instead of SMTP.
 *
 * Cloud hosts like Railway commonly block outbound SMTP ports (465/587) to
 * prevent spam abuse, which makes JavaMailSender hang and time out. The HTTP
 * API avoids that entirely since it uses standard HTTPS.
 *
 * Active when app.email.mode=resend-api.
 */
@Service
@ConditionalOnProperty(name = "app.email.mode", havingValue = "resend-api")
public class ResendApiEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendApiEmailService.class);

    // Your Resend API key (the re_xxx value) — injected from MAIL_PASSWORD
    @Value("${resend.api-key}")
    private String apiKey;

    // Verified sender. onboarding@resend.dev works on the free tier without a domain.
    @Value("${resend.from-address:onboarding@resend.dev}")
    private String fromAddress;

    private final RestClient restClient = RestClient.create("https://api.resend.com");

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            var response = restClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", fromAddress,
                            "to", to,
                            "subject", subject,
                            "text", body
                    ))
                    .retrieve()
                    .toEntity(String.class);

            log.info("Resend API response [{}]: {}", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            // Log the real cause so it shows in Railway logs, then rethrow
            log.error("Resend API email send FAILED: {}", e.getMessage(), e);
            throw e;
        }
    }
}
