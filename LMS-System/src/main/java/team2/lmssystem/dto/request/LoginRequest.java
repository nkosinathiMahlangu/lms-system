package team2.lmssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for the {@code POST /auth/login} endpoint.
 *
 * <p>Both fields are validated with {@code @NotBlank} so that Spring's
 * {@code @Valid} mechanism rejects empty submissions before the service
 * layer is even reached.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** The user's unique login identifier. Must not be blank. */
    @NotBlank(message = "Username is required")
    private String username;

    /** The user's plaintext password (transmitted over HTTPS, hashed server-side). Must not be blank. */
    @NotBlank(message = "Password is required")
    private String password;
}
