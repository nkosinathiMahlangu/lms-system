package team2.lmssystem.service.userservice;

import team2.lmssystem.dto.request.CreateUserRequest;
import team2.lmssystem.dto.respond.UserResponse;

import java.util.List;

/**
 * Contract for user-management operations available to admins.
 *
 * <p>Implemented by
 * {@link team2.lmssystem.service.userservice.implement.UserServiceImpl}.</p>
 *
 * <p>Covers two operations:</p>
 * <ol>
 *   <li><b>Create user</b> — admin provisions a new EMPLOYEE or ADMIN account</li>
 *   <li><b>List all users</b> — admin retrieves a directory of all system users</li>
 * </ol>
 */
public interface UserService {

    /**
     * Creates a new user account in the system.
     *
     * <p>The password is BCrypt-hashed before persistence.
     * The role must match a pre-seeded value in the {@code roles} table
     * (i.e. {@code "ADMIN"} or {@code "EMPLOYEE"}).</p>
     *
     * @param request the details for the new account (name, username, email, password, role)
     * @return a success message confirming the user was created
     * @throws team2.lmssystem.exception.DuplicateResourceException
     *         if the username or email is already taken
     * @throws team2.lmssystem.exception.ResourceNotFoundException
     *         if the specified role does not exist in the database
     * @throws IllegalArgumentException
     *         if the role string is not a valid {@link team2.lmssystem.enums.RoleName} constant
     */
    String createUser(CreateUserRequest request);

    /**
     * Retrieves a list of all registered users in the system.
     *
     * <p>Returns a projected {@link UserResponse} — sensitive fields like
     * the password hash are never included in the response.</p>
     *
     * @return list of all users; empty list if no users exist
     */
    List<UserResponse> getAllUsers();
}
