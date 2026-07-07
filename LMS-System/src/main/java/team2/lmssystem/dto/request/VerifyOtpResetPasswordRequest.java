package team2.lmssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for the {@code POST /auth/verify-otp} endpoint.
 *
 * <p>The user submits the 6-digit OTP they received by email together
 * with their desired new password. Both values are required and validated
 * before the service layer is invoked.</p>
 */
@Getter
@Setter
public class VerifyOtpResetPasswordRequest {

    /**
     * The 6-digit OTP received via email.
     * Must not be blank — format validation (digits only) can be added
     * with {@code @Pattern(regexp = "\\d{6}")} if stricter input control is needed.
     */
    @NotBlank(message = "OTP is required")
    private String otp;

    /**
     * The new password the user wants to set.
     * Must not be blank and must be at least 6 characters long.
     * Will be BCrypt-hashed before persistence.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
