package team2.lmssystem.service.authservice;

import team2.lmssystem.dto.request.LoginRequest;
import team2.lmssystem.dto.request.VerifyOtpResetPasswordRequest;
import team2.lmssystem.dto.respond.AuthResponse;

/**
 * Contract for all authentication-related operations in the LMS.
 *
 * <p>Implemented by
 * {@link team2.lmssystem.service.authservice.implement.AuthServiceImpl}.</p>
 *
 * <p>Covers three flows:</p>
 * <ol>
 *   <li><b>Login</b>         — validate credentials, return a JWT</li>
 *   <li><b>Forgot password</b> — generate and email a one-time OTP</li>
 *   <li><b>Reset password</b> — verify the OTP and set a new password</li>
 * </ol>
 */
public interface AuthService {

    /**
     * Authenticates a user with username and password.
     *
     * <p>On success, generates a JWT and returns it alongside the
     * username and the user's primary role.</p>
     *
     * @param request contains the username and plaintext password
     * @return {@link AuthResponse} with the JWT token, username, and role
     * @throws team2.lmssystem.exception.UnauthorizedException
     *         if the username does not exist or the password does not match
     */
    AuthResponse login(LoginRequest request);

    /**
     * Initiates the forgot-password flow for the given email address.
     *
     * <p>If the email belongs to a registered user, a 6-digit OTP is
     * generated, persisted, and sent to that address. The response is
     * deliberately vague ("If the email exists …") regardless of whether
     * the address is found, to prevent user-enumeration attacks.</p>
     *
     * @param email the email address submitted by the user
     * @return a generic confirmation message safe to show publicly
     */
    String forgotPassword(String email);

    /**
     * Verifies the submitted OTP and resets the user's password.
     *
     * <p>Checks that the OTP:</p>
     * <ul>
     *   <li>Exists in the database</li>
     *   <li>Has not already been used</li>
     *   <li>Has not expired (valid for 3 minutes after generation)</li>
     * </ul>
     *
     * <p>On success, the password is BCrypt-hashed and saved, and the
     * OTP is marked as used.</p>
     *
     * @param request contains the OTP string and the desired new password
     * @return a success message on valid OTP and successful password update
     * @throws team2.lmssystem.exception.UnauthorizedException
     *         if the OTP is not found, already used, or expired
     */
    String verifyOtpAndResetPassword(VerifyOtpResetPasswordRequest request);
}
