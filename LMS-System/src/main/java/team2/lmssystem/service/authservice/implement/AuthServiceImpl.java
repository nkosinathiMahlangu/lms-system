package team2.lmssystem.service.authservice.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.LoginRequest;
import team2.lmssystem.dto.respond.AuthResponse;
import team2.lmssystem.entity.PasswordResetToken;
import team2.lmssystem.entity.User;
import team2.lmssystem.repository.PasswordResetTokenRepository;
import team2.lmssystem.repository.UserRepository;
import team2.lmssystem.service.authservice.AuthService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        // 🔐 Secure login (no info leak)
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = "dummy-jwt-token"; // will replace with real JWT

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRoles().iterator().next().getName().name())
                .build();
    }

    @Override
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusMinutes(30))
                    .build();

            tokenRepository.save(resetToken);

            // TODO: send email
        }

        // 🔐 Always same response
        return "If the email exists, a reset link has been sent";
    }

    @Override
    public String resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        //user.setPasswordChanged(true); // 🔥 important

        userRepository.save(user);

        return "Password reset successful";
    }
}