package team2.lmssystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for the {@code POST /auth/forgot-password} endpoint.
 *
 * <p>The user submits their registered email address to trigger the
 * OTP-based password reset flow. A 6-digit OTP will be emailed to
 * this address if it belongs to a registered account.</p>
 */
@Getter
@Setter
public class ForgotPasswordRequest {

    /**
     * The registered email address of the account whose password should be reset.
     * Must be a well-formed email address and must not be blank.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;
}
