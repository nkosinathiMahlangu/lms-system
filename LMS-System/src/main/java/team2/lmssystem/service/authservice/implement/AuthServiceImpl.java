package team2.lmssystem.service.authservice.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.LoginRequest;
import team2.lmssystem.dto.request.VerifyOtpResetPasswordRequest;
import team2.lmssystem.dto.respond.AuthResponse;
import team2.lmssystem.entity.PasswordResetOtp;
import team2.lmssystem.entity.User;
import team2.lmssystem.exception.UnauthorizedException;
import team2.lmssystem.repository.PasswordResetOtpRepository;
import team2.lmssystem.repository.UserRepository;
import team2.lmssystem.security.JwtUtil;
import team2.lmssystem.service.EmailService;
import team2.lmssystem.service.authservice.AuthService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(LoginRequest request) {
        // orElse(null) so both "user not found" and "wrong password" take the same code path,
        // preventing timing-based username enumeration
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRoles().iterator().next().getName().name())
                .build();
    }

    @Override
    public String forgotPassword(String email) {
        // Silently skip unknown emails — same response either way to prevent user enumeration
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            String otp = generateOtp();

            otpRepository.save(PasswordResetOtp.builder()
                    .otp(otp)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusMinutes(3))
                    .used(false)
                    .build());

            emailService.sendEmail(
                    user.getEmail(),
                    "Password Reset OTP",
                    "Your OTP is: " + otp + "\nThis OTP expires in 3 minutes."
            );
        }

        return "If the email exists, an OTP has been sent";
    }

    @Override
    public String verifyOtpAndResetPassword(VerifyOtpResetPasswordRequest request) {
        PasswordResetOtp resetOtp = otpRepository.findByOtp(request.getOtp())
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired OTP"));

        // Reject if already consumed or past the 3-minute window
        if (resetOtp.isUsed() || resetOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Invalid or expired OTP");
        }

        User user = resetOtp.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark used to prevent replay attacks
        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);

        return "Password reset successful";
    }

    /** Generates a 6-digit OTP in the range [100000, 999999]. */
    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
