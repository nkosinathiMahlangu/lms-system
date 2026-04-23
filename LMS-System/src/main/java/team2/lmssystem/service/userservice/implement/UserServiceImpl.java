package team2.lmssystem.service.userservice.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.CreateUserRequest;
import team2.lmssystem.dto.respond.UserResponse;
import team2.lmssystem.entity.Role;
import team2.lmssystem.entity.User;
import team2.lmssystem.enums.RoleName;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public String createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        RoleName roleName = RoleName.valueOf(request.getRole());

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .enabled(true)
                //.passwordChanged(false) // 🔥 force first change
                .build();

        userRepository.save(user);

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
}
