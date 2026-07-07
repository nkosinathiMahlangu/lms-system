package team2.lmssystem.dto.respond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response payload returned by the {@code POST /auth/login} endpoint
 * on successful authentication.
 *
 * <p>The client should store the {@link #token} and include it in
 * subsequent requests as an {@code Authorization: Bearer <token>} header.</p>
 *
 * <p>The {@link #role} field is provided so the client can make routing
 * decisions immediately (e.g. redirect an ADMIN to the admin dashboard
 * and an EMPLOYEE to the leave portal) without making an extra profile call.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * The signed JWT to be used for all subsequent authenticated requests.
     * Valid for 1 hour from the time of login.
     */
    private String token;

    /** The authenticated user's login username. */
    private String username;

    /**
     * The user's primary role — either {@code "ADMIN"} or {@code "EMPLOYEE"}.
     * Used by the frontend to determine which dashboard/routes to show.
     */
    private String role;
}
