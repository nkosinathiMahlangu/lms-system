package team2.lmssystem.dto.respond;

import lombok.*;

/**
 * Generic wrapper for all REST API responses in the LMS.
 *
 * <p>Every endpoint returns this envelope so the client always receives
 * a consistent JSON structure regardless of whether the request succeeded
 * or failed. The {@link team2.lmssystem.exception.GlobalExceptionHandler}
 * also uses this class to wrap error responses.</p>
 *
 * <p>Example success response:</p>
 * <pre>
 * {
 *   "success": true,
 *   "message": "Login successful",
 *   "data": {
 *     "token": "eyJhbGci...",
 *     "username": "john.doe",
 *     "role": "EMPLOYEE"
 *   }
 * }
 * </pre>
 *
 * <p>Example error response:</p>
 * <pre>
 * {
 *   "success": false,
 *   "message": "User not found with username : 'unknown'",
 *   "data": null
 * }
 * </pre>
 *
 * @param <T> the type of the {@link #data} payload; use {@link Void} when
 *            no data needs to be returned alongside the message
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * Whether the operation completed successfully.
     * {@code true} for 2xx responses, {@code false} for all error responses.
     */
    private boolean success;

    /**
     * Human-readable description of the outcome.
     * On success: a confirmation message (e.g. "User created successfully").
     * On failure: the error description from the thrown exception.
     */
    private String message;

    /**
     * The response payload. Null when there is no data to return
     * (e.g. create/update operations that only confirm success).
     */
    private T data;
}
