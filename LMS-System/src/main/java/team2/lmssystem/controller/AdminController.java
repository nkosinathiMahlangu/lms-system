package team2.lmssystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.lmssystem.dto.request.CreateUserDTO;
import team2.lmssystem.dto.request.respond.UserResponseDTO;
import team2.lmssystem.service.LeaveService;
import team2.lmssystem.service.UserService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final LeaveService leaveService;
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody CreateUserDTO request) {

        return ResponseEntity.ok(
                userService.createUser(request)
        );
    }
}