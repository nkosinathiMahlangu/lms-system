package team2.lmssystem.dto.respond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response payload representing a user account summary.
 *
 * <p>Returned by:</p>
 * <ul>
 *   <li>{@code GET /admin/users} — admin retrieves the list of all users</li>
 * </ul>
 *
 * <p>Maps from a {@link team2.lmssystem.entity.User} entity.
 * Sensitive fields — specifically the BCrypt password hash — are
 * deliberately excluded from this projection.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {

    /** The database-generated user ID. */
    private Long id;

    /** User's first name. */
    private String firstName;

    /** User's last name. */
    private String lastName;

    /** User's registered email address. */
    private String email;

    /** User's login username. */
    private String username;

    // NOTE: password is intentionally omitted — never expose the hash in a response
}
