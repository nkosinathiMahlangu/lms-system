package team2.lmssystem.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.CreateUserDTO;
import team2.lmssystem.dto.request.respond.UserResponseDTO;
import team2.lmssystem.entity.Department;
import team2.lmssystem.entity.User;
import team2.lmssystem.repository.DepartmentRepository;
import team2.lmssystem.repository.UserRepository;
import team2.lmssystem.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public UserResponseDTO createUser(CreateUserDTO request) {

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        User manager = null;

        if (request.getManagerId() != null) {
            manager = userRepository
                    .findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .department(department)
                .manager(manager)
                .leaveBalance(20) // default leave days
                .build();

        userRepository.save(user);

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(department.getDepartmentName())
                .manager(manager != null ? manager.getName() : null)
                .build();
    }
}