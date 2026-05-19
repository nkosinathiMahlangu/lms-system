package team2.lmssystem.service.authservice;

import team2.lmssystem.dto.request.LoginRequest;
import team2.lmssystem.dto.request.VerifyOtpResetPasswordRequest;
import team2.lmssystem.dto.respond.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    String forgotPassword(String email);

    String verifyOtpAndResetPassword(VerifyOtpResetPasswordRequest request);
}
