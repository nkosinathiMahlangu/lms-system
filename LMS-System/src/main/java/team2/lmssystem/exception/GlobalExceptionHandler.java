package team2.lmssystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team2.lmssystem.dto.respond.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Central exception handler for the entire LMS REST API.
 *
 * Uses @RestControllerAdvice so every exception thrown from any controller
 * (or service called by a controller) is caught here and converted into a
 * consistent {@link ApiResponse} JSON response.
 *
 * Handled exceptions and their HTTP status codes:
 * <ul>
 *   <li>{@link ResourceNotFoundException}   → 404 Not Found</li>
 *   <li>{@link DuplicateResourceException}  → 409 Conflict</li>
 *   <li>{@link BadRequestException}         → 400 Bad Request</li>
 *   <li>{@link UnauthorizedException}       → 401 Unauthorized</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 with per-field messages</li>
 *   <li>{@link IllegalArgumentException}    → 400 Bad Request (e.g. invalid enum value)</li>
 *   <li>{@link Exception}                   → 500 Internal Server Error (catch-all)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // 404 — Resource not found
    // -------------------------------------------------------------------------

    /**
     * Handles cases where a requested entity does not exist in the database.
     * Returns HTTP 404 with the exception message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // -------------------------------------------------------------------------
    // 409 — Duplicate / conflict
    // -------------------------------------------------------------------------

    /**
     * Handles uniqueness violations (e.g. duplicate username or email).
     * Returns HTTP 409 with the exception message.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // -------------------------------------------------------------------------
    // 400 — Bad request / business rule violations
    // -------------------------------------------------------------------------

    /**
     * Handles business-rule violations (e.g. invalid date range, insufficient balance).
     * Returns HTTP 400 with the exception message.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    /**
     * Handles Bean Validation failures triggered by @Valid on request bodies.
     * Collects all field-level errors into a map and returns HTTP 400.
     *
     * Response structure example:
     * {
     *   "success": false,
     *   "message": "Validation failed",
     *   "data": {
     *     "username": "must not be blank",
     *     "email": "must be a well-formed email address"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Collect each field error into a field → message map
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(fieldErrors)
                        .build());
    }

    /**
     * Handles IllegalArgumentException which is thrown by RoleName.valueOf()
     * when an invalid role string is supplied during user creation.
     * Returns HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // -------------------------------------------------------------------------
    // 401 — Unauthorized
    // -------------------------------------------------------------------------

    /**
     * Handles authentication failures (bad credentials, expired/used OTP).
     * Returns HTTP 401.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }

    // -------------------------------------------------------------------------
    // 400 — Unreadable request body (e.g. invalid date format)
    // -------------------------------------------------------------------------

    // Thrown when Spring can't deserialize the request body —
    // e.g. "2026-13-45" can't be parsed into LocalDate
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Invalid request format. Check your date fields — expected format is YYYY-MM-DD.")
                        .build());
    }

    // -------------------------------------------------------------------------
    // 409 — Database constraint violation (e.g. delete leave type still in use)
    // -------------------------------------------------------------------------

    // Thrown by PostgreSQL when a foreign key constraint is violated —
    // e.g. deleting a leave_type that still has rows in leave_balances or leave_requests
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Cannot delete this leave type — employees still have balances or leave history linked to it. Remove those records first.")
                        .build());
    }

    // -------------------------------------------------------------------------
    // 500 — Catch-all for unexpected errors
    // -------------------------------------------------------------------------

    /**
     * Catch-all handler for any unhandled exception.
     * Logs the error and returns a generic HTTP 500 response so that
     * internal stack traces are never exposed to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        // In production you would log this with a proper logger (SLF4J / Logback)
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("An unexpected error occurred. Please try again later.")
                        .build());
    }
}
