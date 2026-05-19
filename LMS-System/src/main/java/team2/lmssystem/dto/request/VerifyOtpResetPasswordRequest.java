package team2.lmssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpResetPasswordRequest {

    @NotBlank
    private String otp;

    @NotBlank
    private String newPassword;
}