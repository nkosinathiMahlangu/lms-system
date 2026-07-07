package team2.lmssystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Request body for a self-service user registration flow.
 *
 * <p><b>Note:</b> This DTO is not currently wired to any active endpoint.
 * In the LMS, user accounts are created by an admin via
 * {@link CreateUserRequest} and {@code POST /admin/users}.
 * This class is retained for potential future use (e.g. a public
 * self-registration endpoint) and provides stricter validation rules
 * than {@link CreateUserRequest} (username length, password min-length).</p>
 */
@Data
@Builder
public class RegisterRequest {

    /** First name of the user. Must not be blank. */
    @NotBlank(message = "First name is required")
    private String firstName;

    /** Last name of the user. Must not be blank. */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Desired login username. Must be between 4 and 20 characters.
     * Must not be blank.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    /**
     * User's email address in standard format.
     * Must not be blank.
     */
    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * Plaintext password. Will be BCrypt-hashed before storage.
     * Must be at least 6 characters.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
