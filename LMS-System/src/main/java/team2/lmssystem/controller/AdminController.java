package team2.lmssystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.lmssystem.dto.request.CreateUserRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.respond.ApiResponse;
import team2.lmssystem.dto.respond.UserResponse;
import team2.lmssystem.service.leaveservice.LeaveService;
import team2.lmssystem.service.userservice.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final LeaveService leaveService;

    // 👤 Create User
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        String response = userService.createUser(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(response)
                        .build()
        );
    }

    // Get All Users
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .message("Users fetched successfully")
                        .data(users)
                        .build()
        );
    }

    // Approve / Reject Leave
    @PutMapping("/leave/action")
    public ResponseEntity<ApiResponse<String>> processLeave(
            @Valid @RequestBody LeaveActionRequest request,
            Principal principal) {

        String response = leaveService.approveOrRejectLeave(
                principal.getName(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(response)
                        .build()
        );
    }
}
