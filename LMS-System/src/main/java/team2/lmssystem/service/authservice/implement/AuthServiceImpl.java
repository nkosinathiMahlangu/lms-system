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

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        // Secure login
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        //token generator
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRoles().iterator().next().getName().name())
                .build();
    }

    @Override
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user != null) {

            String otp = generateOtp();

            PasswordResetOtp resetOtp =
                    PasswordResetOtp.builder()
                            .otp(otp)
                            .user(user)
                            .expiryDate(LocalDateTime.now().plusMinutes(3))
                            .used(false)
                            .build();

            otpRepository.save(resetOtp);

            emailService.sendEmail(
                    user.getEmail(),
                    "Password Reset OTP",
                    "Your OTP is: " + otp +
                            "\nExpires in 3 minutes."
            );
        }

        return "If the email exists, an OTP has been sent";
    }

    @Override
    public String verifyOtpAndResetPassword(
            VerifyOtpResetPasswordRequest request) {

        PasswordResetOtp resetOtp =
                otpRepository.findByOtp(request.getOtp())
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid or expired OTP"));

        if (resetOtp.isUsed() ||
                resetOtp.getExpiryDate()
                        .isBefore(LocalDateTime.now())) {

            throw new UnauthorizedException(
                    "Invalid or expired OTP");
        }

        User user = resetOtp.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        userRepository.save(user);

        resetOtp.setUsed(true);

        otpRepository.save(resetOtp);

        return "Password reset successful";
    }
    //otp generator method
    private String generateOtp() {

        int otp = (int) (Math.random() * 900000) + 100000;

        return String.valueOf(otp);
    }
}