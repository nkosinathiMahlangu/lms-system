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
import team2.lmssystem.dto.request.ResetPasswordRequest;
import team2.lmssystem.dto.respond.ApiResponse;
import team2.lmssystem.dto.respond.AuthResponse;
import team2.lmssystem.service.authservice.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🔐 Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

    // 📧 Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        String response = authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(response)
                        .build()
        );
    }

    // 🔑 Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        String response = authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(response)
                        .build()
        );
    }
}