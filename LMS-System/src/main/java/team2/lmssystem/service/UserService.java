package team2.lmssystem.service;

import team2.lmssystem.dto.request.CreateUserDTO;
import team2.lmssystem.dto.request.respond.UserResponseDTO;

public interface UserService {

    UserResponseDTO createUser(CreateUserDTO request);

}
