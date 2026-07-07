package team2.lmssystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.lmssystem.dto.request.ForgotPasswordRequest;
import team2.lmssystem.dto.request.LoginRequest;
import team2.lmssystem.dto.request.VerifyOtpResetPasswordRequest;
import team2.lmssystem.dto.respond.ApiResponse;
import team2.lmssystem.dto.respond.AuthResponse;
import team2.lmssystem.service.authservice.AuthService;

/** Public authentication endpoints — no JWT required. */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(authService.forgotPassword(request.getEmail()))
                .build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpResetPasswordRequest request) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(authService.verifyOtpAndResetPassword(request))
                .build());
    }
}
