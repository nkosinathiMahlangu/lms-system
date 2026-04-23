package team2.lmssystem.service.userservice;

import team2.lmssystem.dto.request.CreateUserRequest;
import team2.lmssystem.dto.respond.UserResponse;

import java.util.List;

public interface UserService {

    String createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();
}
