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

/** Admin-only endpoints — requires ROLE_ADMIN. */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final LeaveService leaveService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(userService.createUser(request))
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(userService.getAllUsers())
                .build());
    }

    @PutMapping("/leave/action")
    public ResponseEntity<ApiResponse<String>> processLeave(
            @Valid @RequestBody LeaveActionRequest request,
            Principal principal) {

        // principal.getName() comes from the JWT subject — identifies the acting admin
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(leaveService.approveOrRejectLeave(principal.getName(), request))
                .build());
    }
}
