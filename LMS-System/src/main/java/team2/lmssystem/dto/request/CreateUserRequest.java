package team2.lmssystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request body for the {@code POST /admin/users} endpoint.
 *
 * <p>An admin uses this DTO to provision a new user account (either
 * an EMPLOYEE or another ADMIN). All fields are mandatory.</p>
 *
 * <p>The {@code role} field must match a valid {@link team2.lmssystem.enums.RoleName}
 * constant ({@code "ADMIN"} or {@code "EMPLOYEE"}). An invalid value will
 * trigger an {@link IllegalArgumentException} caught by the
 * {@link team2.lmssystem.exception.GlobalExceptionHandler}.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    /** Employee's first name. Must not be blank. */
    @NotBlank(message = "First name is required")
    private String firstName;

    /** Employee's last name. Must not be blank. */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Desired login username. Must be unique across all users.
     * Must not be blank.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * User's email address. Must be unique and must be a valid email format.
     * Used for password-reset OTP delivery.
     */
    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * Plaintext password. Will be BCrypt-hashed before storage.
     * Must not be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Role to assign to the new user.
     * Accepted values: {@code "ADMIN"} or {@code "EMPLOYEE"} (case-insensitive).
     * Must not be blank.
     */
    @NotBlank(message = "Role is required")
    private String role;
}
