package team2.lmssystem.service.userservice.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.CreateUserRequest;
import team2.lmssystem.dto.respond.UserResponse;
import team2.lmssystem.entity.LeaveBalance;
import team2.lmssystem.entity.Role;
import team2.lmssystem.entity.User;
import team2.lmssystem.enums.RoleName;
import team2.lmssystem.exception.BadRequestException;
import team2.lmssystem.exception.DuplicateResourceException;
import team2.lmssystem.exception.ResourceNotFoundException;
import team2.lmssystem.repository.LeaveBalanceRepository;
import team2.lmssystem.repository.LeaveTypeRepository;
import team2.lmssystem.repository.RoleRepository;
import team2.lmssystem.repository.UserRepository;
import team2.lmssystem.service.userservice.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // valueOf throws IllegalArgumentException if the role string is invalid —
        // caught by GlobalExceptionHandler and returned as HTTP 400
        RoleName roleName = RoleName.valueOf(request.getRole().toUpperCase());

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName.name()));

        User user = userRepository.save(User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .enabled(true)
                .build());

        // Auto-seed leave balances for every leave type when an EMPLOYEE is created.
        // ADMIN accounts don't need balances since they don't apply for leave.
        if (roleName == RoleName.EMPLOYEE) {
            leaveTypeRepository.findAll().forEach(leaveType ->
                    leaveBalanceRepository.save(LeaveBalance.builder()
                            .user(user)
                            .leaveType(leaveType)
                            .remainingDays(leaveType.getDefaultDays())
                            .build())
            );
        }

        return "User created successfully";
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Prevent admin from deleting themselves or other admins —
        // only EMPLOYEE accounts can be removed this way
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);

        if (isAdmin) {
            throw new BadRequestException("Admin accounts cannot be deleted through this endpoint");
        }

        userRepository.delete(user);
        return "Employee '" + user.getUsername() + "' deleted successfully";
    }
}
